/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import themulti0.lavaplayerextra.SourceMatcher;
import themulti0.lavaplayerextra.spotify.apiFacade.Facade;
import themulti0.lavaplayerextra.spotify.apiFacade.ItemType;
import themulti0.lavaplayerextra.spotify.apiFacade.Matcher;
import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import themulti0.lavaplayerextra.spotify.providers.AlbumProvider;
import themulti0.lavaplayerextra.spotify.providers.ArtistTopTracksProvider;
import themulti0.lavaplayerextra.spotify.providers.ICollectionProvider;
import themulti0.lavaplayerextra.spotify.providers.ItemProvider;
import themulti0.lavaplayerextra.spotify.providers.PlaylistProvider;
import themulti0.lavaplayerextra.spotify.providers.TrackProvider;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * Audio source manager that implements finding Spotify items and searching them on Youtube
 */
public class SpotifyAudioSourceManager implements AudioSourceManager {
    /**
     * Logger for error logging
     */
    private static final Logger log = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);

    /**
     * Manager used for search operations
     */
    private DefaultAudioPlayerManager manager;

    // Spotify Item Providers
    private TrackProvider trackProvider;
    private ArtistTopTracksProvider artistTopTracksProvider;
    private Map<ItemType, ICollectionProvider> collectionProviders;

    /**
     * @param clientId     Spotify API clientId
     * @param clientSecret Spotify API clientSecret
     * @param manager      Manager that contains a Youtube audiosourcemanager
     */
    public SpotifyAudioSourceManager(String clientId, String clientSecret, DefaultAudioPlayerManager manager) {
        this(new Facade(clientId, clientSecret), manager);
    }

    /**
     * @param facade  Already authenticated Spotify API Facade
     * @param manager Manager that contains a Youtube audiosourcemanager
     */
    public SpotifyAudioSourceManager(Facade facade, DefaultAudioPlayerManager manager) {
        this.manager = manager;
        trackProvider = new TrackProvider(facade, manager);
        artistTopTracksProvider = new ArtistTopTracksProvider(facade, manager);

        collectionProviders = new HashMap<>();
        collectionProviders.put(ItemType.ALBUM, new AlbumProvider(facade, manager));
        collectionProviders.put(ItemType.PLAYLIST, new PlaylistProvider(facade, manager));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceName() {
        return "spotify";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        try {
            return loadSpotifyItem(manager, reference.identifier, 0, 0, new MultiThreadingOptions(0, 1));
        } catch (Exception e) {
            log.error("Spotify engine failed to load item", e);
        }
        return null;
    }

    /**
     * Loads a Spotify Item with optional, customizable arguments
     *
     * @param manager The DefaultAudioPlayerManager used
     * @param limit   Max amount of results. If smaller than 1, set to the default maximum amount (100)
     * @param offset  The index of the first track to return. Default: 0
     * @param options MultiThreading options for the concurrent search
     * @return AudioItem of result
     * @throws Exception
     */
    public AudioItem loadSpotifyItem(
            DefaultAudioPlayerManager manager,
            String uri,
            int limit,
            int offset,
            MultiThreadingOptions options) throws Exception {
        if (!isUriLoadable(uri)) {
            return null;
        }
        changeManager(manager);

        ItemType type = new Matcher(uri).getType();
        switch (type) {
            case TRACK:
                return trackProvider.getTrack(uri);
            case ARTIST:
                return artistTopTracksProvider.getCollection(uri, options);
            default: // For playlist, album and artist's top tracks
                return collectionProviders.get(type).getCollection(uri, limit, offset, options);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return isUriLoadable(track.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    private boolean isUriLoadable(String id) {
        return SourceMatcher.match(id) == SourceMatcher.SPOTIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new YoutubeAudioTrack(trackInfo, manager.source(YoutubeAudioSourceManager.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
    }

    /**
     * Changes DefaultAudioPlayerManager on every itemprovider to the specified one
     */
    private void changeManager(DefaultAudioPlayerManager manager) {
        if (this.manager == manager) {
            return;
        }

        this.manager = manager;
        trackProvider.setManager(manager);
        artistTopTracksProvider.setManager(manager);
        collectionProviders.forEach((type, provider) -> ((ItemProvider) provider).setManager(manager));
    }
}