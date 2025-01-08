package info.sitnikov.shortservice.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Map.entry;

public class Config {
    private static long maxClicks = 10;
    private static long maxMinutes = 1440;
    private static int shortLength = 10;
    private static long deleteExpiredAfterMinutes = 5;
    private static String siteName = "https://shortlk.ru/";

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private final Type type = new TypeToken<Map<String, String>>() {
    }.getType();

    private Map<String, String> load(String filename) throws IOException {
        return gson.fromJson(Files.readString(Paths.get(filename)), type);
    }

    public void store(String filename) {
        // Сохранять данные будем в отсортированной мапе
        TreeMap<String, ? extends Serializable> settings = new TreeMap<>(Map.ofEntries(
                entry("max_clicks", maxClicks),
                entry("max_minutes", maxMinutes),
                entry("short_length", shortLength),
                entry("delete_expired_after_minutes", deleteExpiredAfterMinutes),
                entry("site_name", siteName)
        ));
        String json = gson.toJson(settings, this.type);
        try {
            Files.writeString(Paths.get(filename), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Config(String fileName) {
        Map<String, String> config;
        boolean needStore = false;
        try {
            config = load(fileName);
            for (var entry : config.entrySet()) {
                String value = entry.getValue();
                try {
                    switch (entry.getKey()) {
                        case "max_clicks":
                            maxClicks = Long.parseLong(value);
                            break;
                        case "max_minutes":
                            maxMinutes = Long.parseLong(value);
                            break;
                        case "short_length":
                            shortLength = Integer.parseInt(value);
                            break;
                        case "delete_expired_after_minutes":
                            deleteExpiredAfterMinutes = Long.parseLong(value);
                            break;
                        case "site_name":
                            siteName = value;
                            break;
                    }
                } catch (NumberFormatException ignored) {
                    needStore = true;
                }
            }
        } catch (Exception ignored) {
            needStore = true;
        }
        if (needStore) {
            store(fileName);
        }
    }

    public String getSiteName() {
        return siteName;
    }

    public long getMaxClicks() {
        return maxClicks;
    }

    public long getMaxMinutes() {
        return maxMinutes;
    }

    public int getShortLength() {
        return shortLength;
    }

    public long getDeleteExpiredAfterMinutes() {
        return deleteExpiredAfterMinutes;
    }
}
