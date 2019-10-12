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
import themulti0.lavaplayerextra.spotify.services.PlaylistHelper;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ICollectionProvider that provides playlists
 */
public class PlaylistProvider extends ItemProvider implements ICollectionProvider {

    /**
     * {@inheritDoc}
     */
    public PlaylistProvider(Facade facade, DefaultAudioPlayerManager manager) {
        super(facade, manager);
    }

    /**
     * {@inheritDoc}
     * @return Playlist as AudioPlaylist with SpotifyAudioTracks
     */
    @Override
    public AudioPlaylist getCollection(String uri, int limit, int offset, MultiThreadingOptions options) throws Exception {
        return ApiHelper.loadSafe(() ->
        {
            var playlist = facade.getPlaylist(uri);
            var infos = getFullPlaylistTrackInfos(playlist, uri, limit, offset);
            List<AudioTrack> tracks = searcher.searchCollection(infos, options);
            return PlaylistHelper.getAudioPlaylist(playlist.getName(), tracks);
        }, facade);
    }

    /**
     * Retrieves the extra tracks needed, if not included in the Paging that's limited to 100 tracks
     * @param playlist The Spotify Playlist object
     * @param uri uri of the playlist
     * @param limit Original requested limit
     * @param offset Original requested offset
     * @return List<SpotifyAudioTrackInfo> that contains all of the tracks
     */
    private List<SpotifyAudioTrackInfo> getFullPlaylistTrackInfos(Playlist playlist, String uri, int limit, int offset) {
        Paging<PlaylistTrack> tracksRequest = playlist.getTracks();
        List<SpotifyAudioTrackInfo> tracks = playlistToSpotifyInfoList(tracksRequest);
        ApiHelper.getFullCollection(
                tracks,
                tracksRequest.getTotal(),
                limit,
                offset,
                newOffset -> {
                    try {
                        return playlistToSpotifyInfoList(facade.getPlaylistTracks(uri, limit, newOffset));
                    } catch (IOException | SpotifyWebApiException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
        return tracks;
    }

    /**
     * Converts Paging<PlaylistTrack> to a List<SpotifyAudioTrackInfo>
     * @param tracks Paging of tracks which needs to be converted
     * @return Converted list of SpotifyAudioTrackInfo
     */
    private List<SpotifyAudioTrackInfo> playlistToSpotifyInfoList(Paging<PlaylistTrack> tracks) {
        return Arrays.stream(tracks.getItems())
                .map(PlaylistTrack::getTrack)
                .map(SpotifyAudioTrackInfo::new)
                .collect(Collectors.toList());
    }
}
