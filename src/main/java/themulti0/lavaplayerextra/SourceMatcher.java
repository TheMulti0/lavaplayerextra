package themulti0.lavaplayerextra;

import java.awt.*;

/**
 * Written by TheMulti0
 * https://github.com/TheMulti0
 */
public enum SourceMatcher {

    YOUTUBE("Youtube", null, null, new Color(255, 0, 0)),
    SOUNDCLOUD("Soundcloud", null, null, new Color(255, 117, 0)),
    SPOTIFY("Spotify", null, null, new Color(132, 189, 0)),
    TWITCH("Twitch", null, null, new Color(100, 65, 165)),
    HEARTHIS("HearThis.at", null, null, null),

    TERM("Not a link", null, null, null),
    OTHER("Other", "https://cdn.gigafyde.nl/assets/apollo/playicon.png", null, null);

    private final String name;
    private final String iconUrl;
    private final Color color;
    private String songUrl;

    SourceMatcher(String name, String songUrl, String iconUrl, Color color) {
        this.name = name;
        if (iconUrl == null && name != null) {
            iconUrl = String.format("https://cdn.gigafyde.nl/assets/tune/%s.png", name.toLowerCase());
        }
        this.songUrl = songUrl;
        this.iconUrl = iconUrl;
        if (color == null) {
            color = Color.darkGray;
        }
        this.color = color;
    }

    public static SourceMatcher match(String songUrl) {
        if (songUrl.startsWith("https://www.youtube.com") || songUrl.startsWith("ytsearch:")) {
            SourceMatcher match = YOUTUBE;
            match.songUrl = songUrl;
            return match;
        }
        if (songUrl.startsWith("https://soundcloud.com/") || songUrl.startsWith("scsearch:")) {
            SourceMatcher match = SOUNDCLOUD;
            match.songUrl = songUrl;
            return match;
        }
        if (songUrl.matches("^https://(?:www\\.|go\\.)?twitch.tv/([^/]+)$")) {
            SourceMatcher match = TWITCH;
            match.songUrl = songUrl;
            return match;
        }
        if (songUrl.startsWith("https://open.spotify.com/") || songUrl.startsWith("spotify:")) {
            SourceMatcher match = SPOTIFY;
            match.songUrl = songUrl;
            return match;
        }
        if (songUrl.startsWith("http://") || songUrl.startsWith("https://")) {
            SourceMatcher match = OTHER;
            match.songUrl = "https://tunebot.me/";
            return match;
        } else {
            return TERM;
        }
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getIconUrl() {
        return songUrl;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
