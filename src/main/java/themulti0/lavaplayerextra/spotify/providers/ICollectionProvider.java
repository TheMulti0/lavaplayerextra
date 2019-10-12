/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.providers;

import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

/**
 * Interface for classes that provide Spotify collections and take advantage of limit and offset
 */
public interface ICollectionProvider {
    /**
     * Retrieves the Spotify collection
     *
     * @param uri     url / uri of the Spotify item
     * @param limit   Max amount of results. If smaller than 1, set to the default maximum amount (100)
     * @param offset  The index of the first track to return. Default: 0
     * @param options MultiThreading options for the concurrent search
     * @return AudioPlaylist that has SpotifyAudioTracks
     * @throws Exception
     */
    AudioPlaylist getCollection(String uri, int limit, int offset, MultiThreadingOptions options) throws Exception;
}
