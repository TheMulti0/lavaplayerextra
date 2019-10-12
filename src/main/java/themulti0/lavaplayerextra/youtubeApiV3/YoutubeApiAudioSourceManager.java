/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.youtubeApiV3;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import themulti0.lavaplayerextra.SourceMatcher;
import themulti0.lavaplayerextra.youtubeApiV3.apiFacade.YoutubeApiAuthentication;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Consumer;
import java.util.function.Function;

public class YoutubeApiAudioSourceManager implements AudioSourceManager, HttpConfigurable {
    private final YoutubeApiSearchProvider searchProvider;
    private final int defaultMaxResults;

    public YoutubeApiAudioSourceManager(String apiKey) {
        this(apiKey, 0);
    }

    public YoutubeApiAudioSourceManager(String apiKey, int defaultMaxResults){
        this.searchProvider = new YoutubeApiSearchProvider(apiKey);
        this.defaultMaxResults = defaultMaxResults;
    }

    @Override
    public String getSourceName() {
        return "youtubeApi";
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        String identifier = track.getIdentifier();
        if (SourceMatcher.match(identifier) != SourceMatcher.TERM) {
            return false;
        }
        // If the track is not a playlist (URL does not contain 'list=') return true
        return !identifier.contains("list=");
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (hasSearchPrefix(reference.identifier) && YoutubeApiAuthentication.isApiFunctional.get()) {
            String trimmedQuery = reference.identifier.substring(SEARCH_PREFIX.length()).trim(); // Remove the "ytsearch:" prefix

            return searchProvider.loadSearchResult(trimmedQuery, defaultMaxResults);
        }
        return null;
    }

    // Other implementations

    private static String SEARCH_PREFIX = "ytsearch:";
    private YoutubeAudioSourceManager sourceManager = new YoutubeAudioSourceManager();
    private HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();

    protected boolean hasSearchPrefix(String identifier) {
        return identifier.startsWith(SEARCH_PREFIX);
    }

    /**
     * @return Get an HTTP interface for a playing track.
     */
    public HttpInterface getHttpInterface() {
        return httpInterfaceManager.getInterface();
    }

    @Override
    public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
        httpInterfaceManager.configureRequests(configurator);
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        // No custom values that need saving
    }

    @Override
    public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
        httpInterfaceManager.configureBuilder(configurator);
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new YoutubeAudioTrack(trackInfo, sourceManager);
    }

    @Override
    public void shutdown() {
        ExceptionTools.closeWithWarnings(httpInterfaceManager);
    }

}
