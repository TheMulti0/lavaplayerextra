package themulti0.lavaplayerextra.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import themulti0.lavaplayerextra.LoadTestsHelper;
import themulti0.lavaplayerextra.spotify.models.MultiThreadingOptions;
import themulti0.lavaplayerextra.spotify.models.SpotifyAudioTrack;
import themulti0.lavaplayerextra.spotify.providers.PlaylistProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpotifyPlaylistTests {

    private static final Logger log = LoggerFactory.getLogger(SpotifyPlaylistTests.class);
    private static final String url = "https://open.spotify.com/user/21r5tndchxdatfj4lb7uyqvwy/playlist/760jzfY97OdbmzRSeBulHJ?si=EK3KgeVxRL6NGDFM1ef_vQ";
    private final MultiThreadingOptions options = new MultiThreadingOptions(0,1);

    @Test
    public void test() throws IOException, SpotifyWebApiException, InterruptedException {
        DefaultAudioPlayerManager manager = getManager();
        LoadTestsHelper.registerAllYoutube(manager);
        LoadTestsHelper.registerSpotify(manager);

        AudioLoadResultHandler resultHandler = new FunctionalResultHandler(
                track -> Assert.fail("Got track"),
                playlist -> playlist.getTracks().forEach(this::checkTrack),
                () -> Assert.fail("No matches"),
                e -> log.error("Exceptions", e)
        );
        manager.loadItem(url, resultHandler);
        Thread.sleep(100000000);
    }

    @Test
    public void testSourceManager() throws Exception {
        var spotifyManager = new SpotifyAudioSourceManager(LoadTestsHelper.getSpotifyFacade(), getManager());

        var item = spotifyManager.loadSpotifyItem(getManager(), url, 0, 0, options);
        var playlist = (AudioPlaylist) item;
        playlist.getTracks().forEach(this::checkTrack);
    }

    @Test
    public void testDirect() throws Exception {
        var playlistProvider = new PlaylistProvider(LoadTestsHelper.getSpotifyFacade(), getManager());
        AudioPlaylist playlist = playlistProvider.getCollection(url, 0, 0, options);
        playlist.getTracks().forEach(this::checkTrack);
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
