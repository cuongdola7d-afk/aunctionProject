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

                        User user = new User();

                        if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull()) {
                            user.setId(jsonObject.get("id").getAsString());
                        }

                        if (jsonObject.has("action") && !jsonObject.get("action").isJsonNull()) {
                            user.setAction(jsonObject.get("action").getAsString());
                        }

                        if (jsonObject.has("username") && !jsonObject.get("username").isJsonNull()) {
                            user.setUsername(jsonObject.get("username").getAsString());
                        }

                        if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull()) {
                            user.setName(jsonObject.get("name").getAsString());
                        } else if (jsonObject.has("username") && !jsonObject.get("username").isJsonNull()) {
                            user.setName(jsonObject.get("username").getAsString());
                        }

                        if (jsonObject.has("email") && !jsonObject.get("email").isJsonNull()) {
                            user.setEmail(jsonObject.get("email").getAsString());
                        }

                        if (jsonObject.has("password") && !jsonObject.get("password").isJsonNull()) {
                            user.setPassword(jsonObject.get("password").getAsString());
                        }

                        return user;
                    }
                })
                .create();
    }
}