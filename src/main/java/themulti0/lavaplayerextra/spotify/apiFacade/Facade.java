/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.apiFacade;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple facade of the Spotify REST API wrapper
 */
public class Facade {
    private static final Logger log = LoggerFactory.getLogger(Facade.class);

    private static SpotifyApi spotifyApi = null;

    private static ClientCredentialsRequest clientCredentialsRequest = null;
    private static Timer credentialsRefresher = null;

    /**
     * Constructor requires authentication at first but does not authenticate until called at {@link #refreshSpotifyCredentials()}
     *
     * @param clientId     Public ID
     * @param clientSecret Private ID
     */
    public Facade(String clientId, String clientSecret) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
        clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();
    }

    /**
     * Sends an authentication requests to Spotify using ClientCredentials authorization and returns the ClientCredentialsResponse
     * does not change the private SpotifyApi used for the rest of the methods
     *
     * @return ClientCredentials response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public ClientCredentials getNewCredentials() throws IOException, SpotifyWebApiException {
        try {
            return clientCredentialsRequest.execute();
        } catch (Throwable t) {
            log.warn("An error occurred when getting the Spotify client credentials");
            throw t; // Automatically wrapped into CompletableFuture.cancelled(Exception e)
        }
    }

    /**
     * Authenticates with Spotify using ClientCredentials request and changes the private SpotifyApi used for the rest of the methods
     *
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public void refreshSpotifyCredentials() throws IOException, SpotifyWebApiException {
        var credentials = getNewCredentials();
        if (credentialsRefresher == null) {
            configureRefresher(credentials);
        }
        spotifyApi.setAccessToken(credentials.getAccessToken());
        spotifyApi.setRefreshToken(String.valueOf(credentials.getExpiresIn()));
    }

    /**
     * Configures the automatic timer that refreshes the Apis authorization
     *
     * @param credentials The client credentials response (authorization)
     */
    private void configureRefresher(ClientCredentials credentials) {
        credentialsRefresher = new Timer();
        var time = (credentials.getExpiresIn() - 3) * 1000; // Expired time duration in seconds minus 2 second (time takes to refresh token), then converted to milliseconds
        credentialsRefresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    refreshSpotifyCredentials();
                } catch (IOException | SpotifyWebApiException e) {
                    e.printStackTrace();
                }
            }
        }, time, time);
    }

    /**
     * Retrieves a track from Spotify using its url / uri
     *
     * @param uri The track url / uri
     * @return Track response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Track getTrack(String uri) throws IOException, SpotifyWebApiException {
        return spotifyApi
                .getTrack(Matcher.getTrackId(uri))
                .market(CountryCode.US)
                .build().execute();
    }

    /**
     * Retrieves a playlist from Spotify using its url / uri
     *
     * @param uri The playlist url / uri
     * @return Playlist response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Playlist getPlaylist(String uri) throws IOException, SpotifyWebApiException {
        var ids = Matcher.getPlaylistIds(uri);
        return spotifyApi
                .getPlaylist(ids.userId, ids.playlistId)
                .market(CountryCode.US)
                .build().execute();
    }

    /**
     * Retrieves only the tracks of a playlist from Spotify using its url / uri
     *
     * @param uri    The playlist url / uri
     * @param limit  Max amount of results. If smaller than 1, set to the default maximum amount (100)
     * @param offset The index of the first track to return. Default: 0
     * @return Paging of playlist tracks
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Paging<PlaylistTrack> getPlaylistTracks(
            String uri,
            int limit,
            int offset) throws IOException, SpotifyWebApiException {
        if (limit < 1) {
            limit = 100;
        }
        var ids = Matcher.getPlaylistIds(uri);
        return spotifyApi
                .getPlaylistsTracks(ids.userId, ids.playlistId)
                .fields("total, items")
                .limit(limit)
                .offset(offset)
                .market(CountryCode.US)
                .build().execute();
    }

    /**
     * Retrieves an album from Spotify using its url / uri
     *
     * @param uri The album url / uri
     * @return Album response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Album getAlbum(String uri) throws IOException, SpotifyWebApiException {
        return spotifyApi
                .getAlbum(Matcher.getAlbumId(uri))
                .market(CountryCode.US)
                .build().execute();
    }

    /**
     * Retrieves only the tracks of an album from Spotify using its url / uri
     *
     * @param uri    The album url / uri
     * @param limit  Max amount of results. If smaller than 1, set to the default maximum amount (100)
     * @param offset The index of the first track to return. Default: 0
     * @return Paging of tracks
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Paging<TrackSimplified> getAlbumTracks(
            String uri,
            int limit,
            int offset) throws IOException, SpotifyWebApiException {
        if (limit == 0) {
            limit = 50;
        }
        return spotifyApi
                .getAlbumsTracks(Matcher.getAlbumId(uri))
                .limit(limit)
                .offset(offset)
                .market(CountryCode.US)
                .build().execute();
    }

    /**
     * Retrieves metadata about an artist using its url / uri
     *
     * @param uri The artist url / uri
     * @return Artist response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Artist getArtist(String uri) throws IOException, SpotifyWebApiException {
        return spotifyApi
                .getArtist(Matcher.getArtistId(uri))
                .build().execute();
    }

    /**
     * Retrieves 5 top tracks by an artists using the artists url / uri
     *
     * @param uri The artist url / uri
     * @return Tracks response
     * @throws IOException
     * @throws SpotifyWebApiException
     */
    public Track[] getArtistsTopTracks(String uri) throws IOException, SpotifyWebApiException {
        return spotifyApi
                .getArtistsTopTracks(Matcher.getArtistId(uri), CountryCode.US)
                .build().execute();
    }
}
