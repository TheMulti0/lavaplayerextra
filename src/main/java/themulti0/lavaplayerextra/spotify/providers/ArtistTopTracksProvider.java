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
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ItemProvider that provides the top 5 tracks by an Artist
 */
public class ArtistTopTracksProvider extends ItemProvider {

    /**
     * {@inheritDoc}
     */
    public ArtistTopTracksProvider(Facade facade, DefaultAudioPlayerManager manager) {
        super(facade, manager);
    }

    /**
     * Retrieves the top tracks by a Spotify artist
     *
     * @param uri     url / uri of the Spotify item
     * @param options MultiThreading options for the concurrent search
     * @return Artist's top tracks as AudioPlaylist with SpotifyAudioTracks
     * @throws Exception
     */
    public AudioPlaylist getCollection(String uri, MultiThreadingOptions options) throws Exception {
        return ApiHelper.loadSafe(() -> {
            Track[] tracks = facade.getArtistsTopTracks(uri);
            List<AudioTrack> newTracks = getAudioTracks(tracks, options);
            return getAudioPlaylist(newTracks);
        }, facade);
    }

    /**
     * Converts SpotifyTrack[] to List<SpotifyAudioTrackInfo>,
     * searches the collection
     *
     * @param tracks               Original tracks from Spotify
     * @param concurrentOperations Maximum concurrentOperations for the searcher
     * @return List<AudioTrack> which are upcasted searched SpotifyAudioTracks
     */
    private List<AudioTrack> getAudioTracks(Track[] tracks, MultiThreadingOptions options) {
        List<SpotifyAudioTrackInfo> spotifyInfos = Arrays.stream(tracks)
                .map(SpotifyAudioTrackInfo::new)
                .collect(Collectors.toList());
        return searcher.searchCollection(spotifyInfos, options);
    }

    /**
     * Wraps the tracks inside an AudioPlaylist
     *
     * @param newTracks Top AudioTracks
     * @return AudioPlaylist of the top tracks
     */
    private AudioPlaylist getAudioPlaylist(List<AudioTrack> newTracks) {
        return new BasicAudioPlaylist("Artist's top tracks", newTracks, null, true);
    }
}