package themulti0.lavaplayerextra.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;

public class ConfigReader {

    private static final Gson gson = new GsonBuilder().create();

    public static Config read() throws IOException {
        try (FileReader reader = new FileReader("config.json")) {
            return gson.fromJson(reader, Config.class);
        }
    }
}
