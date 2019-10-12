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
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ICollectionProvider that provides albums
 */
public class AlbumProvider extends ItemProvider implements ICollectionProvider {

    /**
     * {@inheritDoc}
     */
    public AlbumProvider(Facade facade, DefaultAudioPlayerManager manager) {
        super(facade, manager);
    }

    /**
     * {@inheritDoc}
     * @return Album as AudioPlaylist with SpotifyAudioTracks
     */
    @Override
    public AudioPlaylist getCollection(String uri, int limit, int offset, MultiThreadingOptions options) throws Exception {
        return ApiHelper.loadSafe(() ->
        {
            Album album = facade.getAlbum(uri);
            var infos = getFullAlbumTrackInfos(album, uri, limit, offset);
            var audioTracks = searcher.searchCollection(infos, options);
            return PlaylistHelper.getAudioPlaylist(album.getName(), audioTracks);
        }, facade);
    }

    /**
     * Retrieves the extra tracks needed, if not included in the Paging that's limited to 100 tracks
     * @param album The Spotify Album object
     * @param uri url / uri of the album
     * @param limit Original requested limit
     * @param offset Original requested offset
     * @return List<SpotifyAudioTrackInfo> that contains all of the tracks
     */
    private List<SpotifyAudioTrackInfo> getFullAlbumTrackInfos(Album album, String uri, int limit, int offset) {
        Paging<TrackSimplified> tracksRequest = album.getTracks();
        List<SpotifyAudioTrackInfo> tracks = albumToSpotifyInfoList(tracksRequest);
        ApiHelper.getFullCollection(
                tracks,
                tracksRequest.getTotal(),
                limit,
                offset,
                newOffset -> {
                    Paging<TrackSimplified> currentTracks;
                    try {
                        currentTracks = facade.getAlbumTracks(uri, limit, newOffset);
                        return albumToSpotifyInfoList(currentTracks);
                    } catch (IOException | SpotifyWebApiException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

        return tracks;
    }

    /**
     * Converts Paging<TrackSimplified> to a List<SpotifyAudioTrackInfo>
     * @param tracks Paging of tracks which needs to be converted
     * @return Converted list of SpotifyAudioTrackInfo
     */
    private List<SpotifyAudioTrackInfo> albumToSpotifyInfoList(Paging<TrackSimplified> tracks) {
        return Arrays.stream(tracks.getItems())
                .map(SpotifyAudioTrackInfo::new)
                .collect(Collectors.toList());
    }

}
