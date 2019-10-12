/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.youtubeApiV3.apiFacade;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public class YoutubeVideo {
    private static final YoutubeAudioSourceManager sourceManager = new YoutubeAudioSourceManager();

    private String title;
    private String channelName;
    private long duration;
    private String videoId;
    private String thumbnailUrl;

    YoutubeVideo(SearchResult searchResult, String formattedDuration) {
        SearchResultSnippet videoInfo = searchResult.getSnippet();
        title = videoInfo.getTitle();
        channelName = videoInfo.getChannelTitle();
        duration = formattedToLong(formattedDuration);
        videoId = searchResult.getId().getVideoId();
        thumbnailUrl = videoInfo.getThumbnails().getDefault().getUrl();
    }

    private long formattedToLong(String isoString) {
        isoString = isoString.substring(2);
        long duration = 0;
        StringBuilder prevChars = new StringBuilder();
        for (Character character : isoString.toCharArray()) {
            if (!Character.isDigit(character)) {
                int num = Integer.parseInt(prevChars.toString());

                switch (character) {
                    case 'H':
                        duration += num * 1000 * 60 * 60;
                        break;

                    case 'M':
                        duration += num * 1000 * 60;
                        break;

                    case 'S':
                        duration += num * 1000;
                        break;
                }
                prevChars = new StringBuilder();
            } else {
                prevChars.append(character.toString());
            }

        }
        return duration;
    }

    public AudioTrack toTrack() {
        var info = new AudioTrackInfo(
                title,
                channelName,
                duration,
                videoId,
                false,
                getVideoUri()
                //,thumbnailUrl
        );
        return new YoutubeAudioTrack(info, sourceManager);
    }

    private String getVideoUri() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
