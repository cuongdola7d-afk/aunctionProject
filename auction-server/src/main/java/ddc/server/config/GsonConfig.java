package ddc.server.config;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ddc.server.model.user.User;

public class GsonConfig {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Gson newGson() {
        return new GsonBuilder()
                // Dạy Gson cách ĐÓNG GÓI (Java -> JSON)
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
                        return new JsonPrimitive(formatter.format(localDateTime));
                    }
                })
                // Dạy Gson cách BÓC QUÀ (JSON -> Java)
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDateTime.parse(jsonElement.getAsString(), formatter);
                    }
                })
                .registerTypeAdapter(User.class, new JsonDeserializer<User>() {
                    @Override
                    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();
                        return new User.Builder()
                                .action(jsonObject.has("action") ? jsonObject.get("action").getAsString() : null)
                                .username(jsonObject.has("username") ? jsonObject.get("username").getAsString() : null)
                                .name(jsonObject.has("name") ? jsonObject.get("name").getAsString() : jsonObject.get("username").getAsString())
                                .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                                .password(jsonObject.has("password") ? jsonObject.get("password").getAsString() : null)
                                .build();
                    }
                })
                .create();
    }
}
