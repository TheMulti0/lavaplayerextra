/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.models;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;


/**
 * Meta info for a Spotify audio track
 */
public class SpotifyAudioTrackInfo extends AudioTrackInfo {
    /**
     * Authors of the track
     */
    public final ArtistSimplified[] authors;

    /**
     * Generate the SpotifyTrackInfo using the Track straight from Spotify
     * @param track Spotify track
     */
    public SpotifyAudioTrackInfo(Track track) {
        this(track, track.getExternalUrls().get("spotify"));
    }

    /**
     * Generate the SpotifyTrackInfo using the Track straight from Spotify
     * @param track Spotify track
     * @param url url that is not supplied by the track response
     */
    public SpotifyAudioTrackInfo(Track track, String url) {
        this(
            track.getName(),
            track.getArtists(),
            track.getDurationMs(),
            track.getId(),
            url);
    }

    /**
     * Generate the SpotifyTrackInfo using the Track straight from Spotify
     * @param track Spotify track
     */
    public SpotifyAudioTrackInfo(TrackSimplified track) {
        this(
            track.getName(),
            track.getArtists(),
            track.getDurationMs(),
            track.getId(),
            track.getExternalUrls().get("spotify"));
    }

    /**
     * @param title Track title
     * @param authors Authors of the track
     * @param length Length of the track in milliseconds
     * @param identifier Audio source specific track identifier
     * @param url url of the track or path to its file.
     */
    public SpotifyAudioTrackInfo(
            String title,
            ArtistSimplified[] authors,
            long length,
            String identifier,
            String url) {
        super(title, authors[0].getName(), length, identifier, false, url);
        this.authors = authors;
    }

    /**
     * Gets the correct query to search on Youtube
     * @return 'yt:search The first author - song title'
     */
    public String getSearchableTitle(){
        return String.format("ytsearch:%s - %s", author, title);
    }
}
