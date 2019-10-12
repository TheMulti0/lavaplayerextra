/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.spotify.providers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import themulti0.lavaplayerextra.spotify.apiFacade.Facade;
import themulti0.lavaplayerextra.spotify.services.TracksSearcher;

/**
 * Base class for any class that provides Spotify items
 */
public abstract class ItemProvider {
    /**
     * API facade used inside the provider to fetch Spotify items from API
     */
    Facade facade;
    /**
     * Searcher used inside the provider to search the Spotify items on Youtube
     */
    TracksSearcher searcher;

    /**
     * @param facade API facade used inside the provider
     * @param manager Manager used for searching tracks inside the provider
     */
    public ItemProvider(Facade facade, DefaultAudioPlayerManager manager) {
        this.facade = facade;
        searcher = new TracksSearcher(manager);
    }

    /**
     * Changes the DefaultAudioPlayerManager inside the TracksSearcher
     * @param manager New DefaultAudioPlayerManager
     */
    public void setManager(DefaultAudioPlayerManager manager) {
        searcher.setManager(manager);
    }
}
