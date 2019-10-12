/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.apiFacade;

import java.util.regex.Pattern;

/**
 * Matches between Spotify uri / URI to the correct ItemType
 */
public class Matcher {
    private static final Pattern firstIdRegex = Pattern.compile("(?:\\w{21,})");

    Matcher.Playlist playlistId = null;
    String albumId = null;
    String trackId = null;
    String artistId = null;

    /**
     * Fills the correct ID fields in the Matcher class
     *
     * @param uri url / uri of the Spotify item
     */
    public Matcher(String uri) {
        if (uri.contains("playlist")) {
            playlistId = getPlaylistIds(uri);
        } else if (uri.contains("album")) {
            albumId = getAlbumId(uri);
        } else if (uri.contains("artist")) {
            artistId = getArtistId(uri);
        } else {
            trackId = getTrackId(uri);
        }
    }

    /**
     * Retrieves the track ID from its uri
     *
     * @param uri The track uri
     * @return String ID
     */
    static String getTrackId(String uri) {
        return getFirstId(uri);
    }

    static Matcher.Playlist getPlaylistIds(String playlisturi) {
        String[] separated = playlisturi.split(Pattern.quote("/user/"));
        String[] allIds = separated[separated.length - 1].split(Pattern.quote("/playlist/"));
        String userId = allIds[0];

        //Make sure the default song ID wont be included in the playlist ID
        String[] playlistId = allIds[allIds.length - 1].split(Pattern.quote("?si"));

        String result = getFirstId(playlisturi);
        return new Matcher.Playlist(
                result == null ? userId : result,
                playlistId[0]);
    }

    /**
     * Retrieves the album ID from its uri
     *
     * @param uri The album uri
     * @return String ID
     */
    static String getAlbumId(String uri) {
        return getFirstId(uri);
    }

    /**
     * Retrieves the artist ID from its uri
     *
     * @param uri The artist uri
     * @return String ID
     */
    static String getArtistId(String uri) {
        return getFirstId(uri);
    }

    private static String getFirstId(String uri) {
        java.util.regex.Matcher m = firstIdRegex.matcher(uri);
        String result = "";
        if (m.find()) {
            result = m.group();
        }
        return result;
    }

    /**
     * Converts Matcher to a Spotify uri
     *
     * @return String uri
     */
    String touri() {
        String uri = "https://open.spotify.com/";
        if (trackId != null) {
            return uri + String.join("/", "innerTrack", trackId);
        }
        if (albumId != null) {
            return uri + String.join("/", "album", albumId);
        }
        if (playlistId != null) {
            return uri + String.join("/", "user", playlistId.userId, "playlist", playlistId.playlistId);
        }
        return "";
    }

    /**
     * Returns the ItemType of the Matcher
     *
     * @return ItemType
     */
    public ItemType getType() {
        if (trackId != null) {
            return ItemType.TRACK;
        }
        if (playlistId != null) {
            return ItemType.PLAYLIST;
        }
        if (albumId != null) {
            return ItemType.ALBUM;
        }
        if (artistId != null) {
            return ItemType.ARTIST;
        }
        return null;
    }

    /**
     * Holds the userId and playlistId of a playlist
     */
    static class Playlist {
        public final String userId;
        public final String playlistId;

        Playlist(String userId, String playlistId) {
            this.userId = userId;
            this.playlistId = playlistId;
        }
    }
}