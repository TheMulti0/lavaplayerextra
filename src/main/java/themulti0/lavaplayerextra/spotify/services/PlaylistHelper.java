/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.services;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;

import java.util.List;

/**
 * Tool class to help with wrapping playlists
 */
public class PlaylistHelper {

    /**
     * Creates a new AudioPlaylist with the specified name and tracks
     *
     * @param name   Name of the collection
     * @param tracks Tracks of the collection
     * @return BasicAudioPlaylist
     */
    public static AudioPlaylist getAudioPlaylist(String name, List<AudioTrack> tracks) {
        return new BasicAudioPlaylist(name, tracks, null, false);
    }
}
