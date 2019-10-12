/*
 * Written by TheMulti0, 2019
 * https://github.com/TheMulti0
 */

package themulti0.lavaplayerextra.youtubeApiV3.apiFacade;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class YoutubeApiAuthentication {

    /**
     * Define a global instance of the HTTP transport.
     */
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * This is the global boolean that indicates whether the YouTube API quota is reached
     */
    public static AtomicBoolean isApiFunctional = new AtomicBoolean(true);

}
