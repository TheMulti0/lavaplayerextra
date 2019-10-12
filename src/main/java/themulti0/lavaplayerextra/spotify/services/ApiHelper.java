/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.services;

import themulti0.lavaplayerextra.spotify.apiFacade.Facade;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrackInfo;
/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

import com.sedmelluq.discord.lavaplayer.track.AudioItem;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Tool class to help with communication with the Spotify API
 */
public class ApiHelper {

    /**
     * Begins the load, refreshes Spotify credentials if fails and tries once again
     *
     * @param method The load method
     * @param facade The API facade
     * @param <T>    The return type
     * @return <T>
     * @throws Exception
     */
    public static <T extends AudioItem> T loadSafe(Callable<T> method, Facade facade) throws Exception {
        try {
            return method.call();
        } catch (Throwable ignored) {
            facade.refreshSpotifyCredentials();
            return method.call();
        }
    }

    /**
     * Retrieves the extra tracks needed, if not included in the Paging that's limited to 100 tracks
     *
     * @param tracks           Tracks converted to List<SpotifyAudioTrackInfo>
     * @param total            Total amount of tracks in the collection
     * @param limit            Limit amount of tracks in the collection
     * @param offset           Offset of tracks in the collections
     * @param collectionGetter Method to get the extra tracks of specific collection,
     *                         requires offset and returns a List<SpotifyAudioTrackInfo>
     */
    public static void getFullCollection(
            List<SpotifyAudioTrackInfo> tracks,
            int total,
            int limit,
            int offset,
            Function<Integer, List<SpotifyAudioTrackInfo>> collectionGetter) {
        if (total > 99 && tracks.size() < total) {
            for (int i = tracks.size(); i < total; ) {
                if (limit > 0 ){
                    if (i > limit){
                        break;
                    }
                }
                List<SpotifyAudioTrackInfo> newTracks = collectionGetter.apply(i + offset);
                i += newTracks.size();
                tracks.addAll(newTracks);
            }
        }
    }
}
