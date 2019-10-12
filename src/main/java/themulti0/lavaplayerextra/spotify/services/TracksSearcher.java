/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.services;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrack;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrackInfo;
import themulti0.lavaplayerextra.youtubeApiV3.YoutubeApiAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Tool class to search Spotify tracks on Youtube
 */
public class TracksSearcher {
    /**
     * Thread pool of the loaded
     */
    private ForkJoinPool loadExecutor;

    /**
     * Given DefaultAudioPlayerManager
     */
    private DefaultAudioPlayerManager manager;
    /**
     * The YoutubeApiAudioSourceManager, YoutubeAudioSourceManager used for searching on Youtube
     */
    private List<AudioSourceManager> youtube;

    /**
     * @param manager DefaultAudioPlayerManager used to extract Youtube sourcemanagers
     */
    public TracksSearcher(DefaultAudioPlayerManager manager) {
        loadExecutor = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        this.manager = manager;
        this.youtube = getYoutubeManagers(manager);
    }

    /**
     * Changes DefaultAudioPlayerManager, extracts Youtube sourcemanagers again
     *
     * @param manager DefaultAudioPlayerManager
     */
    public void setManager(DefaultAudioPlayerManager manager) {
        this.manager = manager;
        this.youtube = getYoutubeManagers(manager);
    }

    /**
     * Extracts Youtube sourcemanagers
     *
     * @param manager DefaultAudioPlayerManager to extract out of
     * @return Extracted Youtube sourcemanagers. Null if the {@param manager} is null
     * @throws IllegalArgumentException If the {@param manager} has no Youtube sourcemanagers
     */
    private List<AudioSourceManager> getYoutubeManagers(DefaultAudioPlayerManager manager) throws IllegalArgumentException {
        if (manager == null) {
            return null;
        }

        List<AudioSourceManager> youtube = new ArrayList<>();
        youtube.add(manager.source(YoutubeApiAudioSourceManager.class));
        youtube.add(manager.source(YoutubeAudioSourceManager.class));

        if (youtube.isEmpty()) {
            throw new IllegalArgumentException("DefaultAudioPlayerManager must have a youtube source manager");
        }
        return youtube;
    }

    /**
     * Searches the Spotify collection on Youtube concurrently
     *
     * @param spotifyInfos The spotify infos that need to be searched
     * @param options      MultiThreading options for the concurrent search
     * @return List of upcasted SpotifyAudioTracks
     */
    public List<AudioTrack> searchCollection(List<SpotifyAudioTrackInfo> spotifyInfos, MultiThreadingOptions options) {
        loadExecutor = new ForkJoinPool(getPoolSize(spotifyInfos.size(), options.poolSize));
        return startItemsLoad(spotifyInfos, options.operationsPerThread);
    }

    /**
     * Gets an appropriate number of poolSize
     *
     * @param spotifyInfosSize Size of the infos collection
     * @param poolSize         Given poolSize
     * @return int poolSize
     */
    private int getPoolSize(int spotifyInfosSize, int poolSize) {
        return poolSize < 1 ? spotifyInfosSize : poolSize;
    }

    /**
     * Begins item load for every item
     *
     * @param infos The spotify infos that need to be searched
     */
    private List<AudioTrack> startItemsLoad(List<SpotifyAudioTrackInfo> infos, int operationsPerThread) {
        var result = loadExecutor.submit(() -> {
            var mapped = getBatches(infos, operationsPerThread).entrySet();
            return mapped.parallelStream()
                    .map(batch -> loadSpecificItems(batch.getValue()))
                    .reduce(TracksSearcher::combine).get();
        }).join();

        return new ArrayList<>(result.values());

    }

    /**
     * Split list to batches of Map<Integer, SpotifyAudioTrackInfo>
     *
     * @param infos               The original infos to split to batches
     * @param operationsPerThread Items in a batch
     * @return Map<Index   ,       Batches>
     */
    private Map<Integer, Map<Integer, SpotifyAudioTrackInfo>> getBatches(List<SpotifyAudioTrackInfo> infos, int operationsPerThread) {
        int infosSize = infos.size();
        int batchSize = infosSize / operationsPerThread;

        Map<Integer, Map<Integer, SpotifyAudioTrackInfo>> batches = new HashMap<>();
        int batchIndex = 0;
        for (int i = 0; i < infosSize; i += batchSize) {
            List<SpotifyAudioTrackInfo> batchList = getBatch(infos, batchSize, i);
            batches.put(batchIndex, listToSortedMap(batchList));
            batchIndex++;
        }
        return batches;
    }

    /**
     * Convert a List<T> to a Map<Integer, T>
     *
     * @param list List to convert
     * @return Map<Integer   ,       T>
     */
    private <T> Map<Integer, T> listToSortedMap(List<T> list) {
        var map = new HashMap<Integer, T>();
        int index = 0;
        for (T item : list) {
            map.put(index, item);
            index++;
        }
        return map;
    }

    /**
     * Returns a batch by its size and iteration index
     */
    private List<SpotifyAudioTrackInfo> getBatch(List<SpotifyAudioTrackInfo> infos, int batchSize, int iterationIndex) {
        var batchEnd = iterationIndex + batchSize >= infos.size() ? infos.size() : iterationIndex + batchSize;
        return infos.subList(iterationIndex, batchEnd);
    }

    /**
     * Combine lefthandbatch and righthandbatch
     *
     * @param lhs LeftHandSide
     * @param rhs RightHandSide
     * @return BothSides combined
     */
    private static Map<Integer, SpotifyAudioTrack> combine(Map<Integer, SpotifyAudioTrack> lhs, Map<Integer, SpotifyAudioTrack> rhs) {
        var accumulated = new HashMap<Integer, SpotifyAudioTrack>();
        accumulated.putAll(lhs);
        accumulated.putAll(rhs);
        return accumulated;
    }

    /**
     * Loads an item in the threadPool using its index in the collection and info
     */
    private Map<Integer, SpotifyAudioTrack> loadSpecificItems(Map<Integer, SpotifyAudioTrackInfo> map) {
        Map<Integer, SpotifyAudioTrack> results = new HashMap<>();
        for (Map.Entry<Integer, SpotifyAudioTrackInfo> pair : map.entrySet()) {
            var info = pair.getValue();
            var audioItem = loadItem(info);
            results.put(pair.getKey(), handleLoadResult(info, audioItem));
        }
        return results;
    }

    /**
     * Loads an item synchronously from Youtube
     *
     * @param info Spotify track info
     * @return AudioItem result from Youtube
     */
    private AudioItem loadItem(SpotifyAudioTrackInfo info) {
        AudioItem item = null;
        for (AudioSourceManager yt : youtube) {
            if (yt == null) {
                continue;
            }
            String query = info.getSearchableTitle();
            item = yt.loadItem(manager, new AudioReference(query, ""));
            if (item != null) {
                break;
            }
        }
        return item;
    }

    /**
     * Handles the load result (Converts AudioItem to a SpotifyAudioTrack)
     *
     * @param info   Spotify track info
     * @param result AudioItem result from Youtube
     * @return Converted SpotifyAudioTrack
     */
    private SpotifyAudioTrack handleLoadResult(SpotifyAudioTrackInfo info, AudioItem result) {
        if (result instanceof AudioTrack) {
            return new SpotifyAudioTrack((AudioTrack) result, info);
        }
        if (result instanceof AudioPlaylist) {
            var playlist = (AudioPlaylist) result;
            return new SpotifyAudioTrack(playlist.getTracks().get(0), info);
        }
        return null;
    }
}