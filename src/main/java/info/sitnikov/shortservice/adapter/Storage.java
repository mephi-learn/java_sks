package info.sitnikov.shortservice.adapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import info.sitnikov.shortservice.model.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public interface Storage {

    void store(Map<String, User> users);

    Map<String, User> load() throws IOException;

    final class FileStorage implements Storage {
        private final String filename;
        private final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        private final Type type = new TypeToken<Map<String, User>>() {
        }.getType();

        public FileStorage(String filename) {
            this.filename = filename;
        }

        @Override
        public void store(Map<String, User> users) {
            String json = gson.toJson(users, this.type);
            try {
                Files.writeString(Paths.get(this.filename), json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, User> load() throws IOException {
            return gson.fromJson(Files.readString(Paths.get(this.filename)), type);
        }
    }
}

// Этот класс нужен для сериализации LocalDateTime
class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public JsonElement serialize(final @NotNull LocalDateTime date, final Type typeOfSrc, final JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(final @NotNull JsonElement json, final Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
