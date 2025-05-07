package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;


import java.time.Duration;
import java.time.LocalDateTime;

public class GsonBuilder {
    private static final Gson gson;

    static {
        gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
    }

    private GsonBuilder() {
    }

    public static Gson getGson() {
        return gson;
    }
}
