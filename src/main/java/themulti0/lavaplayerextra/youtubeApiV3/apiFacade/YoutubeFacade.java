/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.youtubeApiV3.apiFacade;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.HashMap;

public class YoutubeFacade {
    private final YouTube youtube;
    private final String apiKey;

    public YoutubeFacade(String apiKey) {
        this.youtube = buildYoutube();
        this.apiKey = apiKey;
    }

    private void validateApiKey(){
        if (apiKey == null){
            throw new NullPointerException("Youtube V3 API key was null.");
        }
    }

    public SearchListResponse getSearchResponse(String identifier, long maxResults) throws IOException {
        validateApiKey();

        YouTube.Search.List search = youtube.search().list("id,snippet");
        search.setKey(apiKey);
        search.setQ(identifier);
        search.setType("video");
        if (maxResults > 0){
            search.setMaxResults(maxResults);
        }

        return search.execute();
    }

    public YoutubeVideo createYoutubeVideo(SearchResult searchResult) throws IOException {
        String videoId = searchResult.getId().getVideoId();
        Video extraVideoInfo = getExtraVideoInfo(videoId).getItems().get(0);
        var formattedDuration = extraVideoInfo.getContentDetails().getDuration();

        return new YoutubeVideo(searchResult, formattedDuration);
    }

    private YouTube buildYoutube() {

        YouTube.Builder builder = new YouTube.Builder(
                YoutubeApiAuthentication.HTTP_TRANSPORT,
                YoutubeApiAuthentication.JSON_FACTORY, request -> {
        }
        ).setApplicationName("Lavaplayer");

        return builder.build();
    }

    private VideoListResponse getExtraVideoInfo(String videoId) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("part", "contentDetails");
        parameters.put("id", videoId);

        validateApiKey();

        YouTube.Videos.List list = youtube.videos().list(parameters.get("part"));
        list.setKey(apiKey);
        list.setId(videoId);

        return list.execute();
    }
}
