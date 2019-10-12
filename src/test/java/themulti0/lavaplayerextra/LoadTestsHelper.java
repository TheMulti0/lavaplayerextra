package themulti0.lavaplayerextra;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import themulti0.lavaplayerextra.config.Config;
import themulti0.lavaplayerextra.config.ConfigReader;
import themulti0.lavaplayerextra.config.SpotifyConfig;
import themulti0.lavaplayerextra.spotify.SpotifyAudioSourceManager;
import themulti0.lavaplayerextra.spotify.apiFacade.Facade;
import themulti0.lavaplayerextra.youtubeApiV3.YoutubeApiAudioSourceManager;

import java.io.IOException;

public class LoadTestsHelper {

    private static Config config;

    static {
        try {
            config = ConfigReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerAllYoutube(DefaultAudioPlayerManager manager) {
        registerYoutubeApi(manager);
        registerYoutube(manager);
    }

    public static void registerYoutube(DefaultAudioPlayerManager manager) {
        manager.registerSourceManager(new YoutubeAudioSourceManager());
    }

    public static void registerYoutubeApi(DefaultAudioPlayerManager manager) {
        manager.registerSourceManager(new YoutubeApiAudioSourceManager(config.youtubeConfig.apiKey));
    }

    public static Facade getSpotifyFacade() throws IOException, SpotifyWebApiException {
        SpotifyConfig spotifyConfig = config.spotifyConfig;
        var facade = new Facade(spotifyConfig.clientId, spotifyConfig.clientSecret);

        facade.refreshSpotifyCredentials();
        return facade;
    }

    public static void registerSpotify(DefaultAudioPlayerManager manager) throws IOException, SpotifyWebApiException {
        Facade facade = getSpotifyFacade();

        manager.registerSourceManager(new SpotifyAudioSourceManager(facade, manager));
    }
}