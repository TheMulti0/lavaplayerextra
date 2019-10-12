package themulti0.lavaplayerextra.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import themulti0.lavaplayerextra.LoadTestsHelper;
import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrack;
import themulti0.lavaplayerextra.spotify.providers.TrackProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpotifyTrackTests {

    private static final Logger log = LoggerFactory.getLogger(SpotifyTrackTests.class);
    private static final String url = "https://open.spotify.com/track/3vWzyGTu6Ovo1GdrcJqH6e?si=DbvsrIXQT2OINPhxmMlJuw";

    @Test
    public void test() throws IOException, SpotifyWebApiException, InterruptedException {
        DefaultAudioPlayerManager manager = getManager();
        LoadTestsHelper.registerSpotify(manager);

        AudioLoadResultHandler resultHandler = new FunctionalResultHandler(
                this::checkTrack,
                playlist -> Assert.fail("Got audioplaylist"),
                () -> Assert.fail("No matches"),
                e -> log.error("Exceptions", e)
        );
        manager.loadItem(url, resultHandler);
        Thread.sleep(100000000);
    }

    @Test
    public void testSourceManager() throws Exception {
        var spotifyManager = new SpotifyAudioSourceManager(LoadTestsHelper.getSpotifyFacade(), getManager());

        var item = spotifyManager.loadSpotifyItem(getManager(), url, 0, 0, new MultiThreadingOptions(0, 1));
        checkTrack((AudioTrack) item);
    }

    @Test
    public void testDirect() throws Exception {
        var trackProvider = new TrackProvider(LoadTestsHelper.getSpotifyFacade(), getManager());
        AudioTrack track = trackProvider.getTrack(url);
        checkTrack(track);
    }

    private DefaultAudioPlayerManager getManager() {
        var manager = new DefaultAudioPlayerManager();
        LoadTestsHelper.registerAllYoutube(manager);
        Assert.assertNotNull("Manager is null", manager);
        return manager;
    }

    private void checkTrack(AudioTrack track) {
        Assert.assertNotNull(track.getIdentifier());

        Assert.assertTrue("Track was not SpotifyAudioTrack", track instanceof SpotifyAudioTrack);
        Assert.assertNotNull(((SpotifyAudioTrack) track).getSpotifyInfo());
    }
}
