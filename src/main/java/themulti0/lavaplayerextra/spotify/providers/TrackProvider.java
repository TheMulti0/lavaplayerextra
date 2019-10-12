/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.providers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import themulti0.lavaplayerextra.spotify.apiFacade.Facade;
import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import themulti0.lavaplayerextra.spotify.services.ApiHelper;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.Collections;
import java.util.List;

/**
 * ItemProvider that provides tracks
 */
public class TrackProvider extends ItemProvider {

    /**
     * {@inheritDoc}
     */
    public TrackProvider(Facade facade, DefaultAudioPlayerManager manager) {
        super(facade, manager);
    }

    /**
     * Retrieves the Spotify track
     *
     * @param uri uri / URI of the Spotify track
     * @return Upcasted SpotifyAudioTrack
     * @throws Exception
     */
    public AudioTrack getTrack(String uri) throws Exception {
        return ApiHelper.loadSafe(() ->
        {
            Track track = facade.getTrack(uri);
            List<SpotifyAudioTrackInfo> info = Collections.singletonList(new SpotifyAudioTrackInfo(track));
            List<AudioTrack> audioTracks = searcher.searchCollection(info, new MultiThreadingOptions(0, 1));
            return audioTracks.get(0);
        }, facade);
    }
}
