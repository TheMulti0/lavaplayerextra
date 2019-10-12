/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.youtubeApiV3;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import themulti0.lavaplayerextra.youtubeApiV3.apiFacade.YoutubeApiAuthentication;
import themulti0.lavaplayerextra.youtubeApiV3.apiFacade.YoutubeFacade;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class YoutubeApiSearchProvider {
    private static final Logger log = LoggerFactory.getLogger(YoutubeApiSearchProvider.class);

    private final YoutubeFacade youtubeFacade;

    public YoutubeApiSearchProvider(String apiKey){
        youtubeFacade = new YoutubeFacade(apiKey);
    }

    public AudioItem loadSearchResult(String query, int maxResults) {
        if (!YoutubeApiAuthentication.isApiFunctional.get()) {
            return null;
        }

        try {
            return loadResults(query, maxResults);
        } catch (Exception ignored) {
            log.warn("Found YouTube V3 API to not be functional. Moving traffic to normal search");
            YoutubeApiAuthentication.isApiFunctional.set(false);
        }
        return null;
    }

    private AudioItem loadResults(String identifier, int maxResults) {
        var searchResult = searchItem(identifier, maxResults);
        if (searchResult == null || searchResult.getTracks().isEmpty()) {
            return null;
        }
        return searchResult;
    }

    private AudioPlaylist searchItem(String query, int maxResults) {
        List<AudioTrack> tracks = new ArrayList<>();
        try {
            SearchListResponse response = youtubeFacade.getSearchResponse(query, maxResults);

            for (SearchResult result : response.getItems()) {
                if (!YoutubeApiAuthentication.isApiFunctional.get()){
                    return null;
                }
                tracks.add(youtubeFacade
                    .createYoutubeVideo(result)
                    .toTrack());
            }

            return new BasicAudioPlaylist("Search results for: " + query, tracks, null, true);

        } catch (Exception e) {
            log.error("Found YouTube V3 API to not be functional. Moving traffic to normal search");
            YoutubeApiAuthentication.isApiFunctional.set(false);
        }
        return null;
    }
}
