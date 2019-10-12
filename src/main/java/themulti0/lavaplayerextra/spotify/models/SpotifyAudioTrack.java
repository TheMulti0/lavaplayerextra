/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.models;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;

/**
 * Audio track that holds the searched track from Youtube
 * and the metadata of the original Spotify track (SpotifyAudioTrackInfo)
 */
public class SpotifyAudioTrack implements AudioTrack {
    private final AudioTrack searchedTrack;
    private final SpotifyAudioTrackInfo spotifyInfo;

    /**
     * @param searchedTrack The actual track that has been searched
     * @param info          The special SpotifyTrackInfo which contains the Spotify track metadata
     */
    public SpotifyAudioTrack(AudioTrack searchedTrack, SpotifyAudioTrackInfo info) {
        this.searchedTrack = searchedTrack;
        this.spotifyInfo = info;
    }

    public SpotifyAudioTrackInfo getSpotifyInfo() {
        return spotifyInfo;
    }

    @Override
    public AudioTrackInfo getInfo() {
        return searchedTrack.getInfo();
    }

    @Override
    public String getIdentifier() {
        return searchedTrack.getIdentifier();
    }

    @Override
    public AudioTrackState getState() {
        return searchedTrack.getState();
    }

    @Override
    public void stop() {
        searchedTrack.stop();
    }

    @Override
    public boolean isSeekable() {
        return searchedTrack.isSeekable();
    }

    @Override
    public long getPosition() {
        return searchedTrack.getPosition();
    }

    @Override
    public void setPosition(long l) {
        searchedTrack.setPosition(l);
    }

    @Override
    public void setMarker(TrackMarker trackMarker) {
        searchedTrack.setMarker(trackMarker);
    }

    @Override
    public long getDuration() {
        return searchedTrack.getDuration();
    }

    @Override
    public AudioTrack makeClone() {
        return searchedTrack.makeClone();
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return searchedTrack.getSourceManager();
    }

    @Override
    public Object getUserData() {
        return searchedTrack.getUserData();
    }

    @Override
    public void setUserData(Object o) {
        searchedTrack.setUserData(o);
    }

    @Override
    public <T> T getUserData(Class<T> aClass) {
        return searchedTrack.getUserData(aClass);
    }
}
