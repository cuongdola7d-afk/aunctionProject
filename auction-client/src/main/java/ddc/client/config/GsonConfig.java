package ddc.client.config;

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

import ddc.client.model.ItemDTO.ArtDTO;
import ddc.client.model.ItemDTO.ElectronicsDTO;
import ddc.client.model.ItemDTO.GeneralDTO;
import ddc.client.model.ItemDTO.ItemGeneric;
import ddc.client.model.ItemDTO.VehicleDTO;

public class GsonConfig {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings({ "Convert2Lambda", "rawtypes" })
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
                .registerTypeAdapter(ItemGeneric.class, new JsonDeserializer<ItemGeneric>() {
                    @Override
                    public ItemGeneric deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();

                        if (!jsonObject.has("category")) {
                            throw new JsonParseException("Không tìm thấy trường phân loại (category) trong JSON");
                        }
                        
                        String category = jsonObject.get("category").getAsString();

                        switch (category) {
                            case "GENERAL" -> {
                                return context.deserialize(json, GeneralDTO.class);
                            }
                            case "ELECTRONICS" -> {
                                return context.deserialize(json, ElectronicsDTO.class);
                            }
                            case "VEHICLE" -> {
                                return context.deserialize(json, VehicleDTO.class);
                            }
                            case "ART" -> {
                                return context.deserialize(json, ArtDTO.class);
                            }
                            default -> throw new JsonParseException("Không hỗ trợ loại sản phẩm này: " + category);
                        }
                    }
                })
                .create();
    }
}