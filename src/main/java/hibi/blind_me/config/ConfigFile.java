package hibi.blind_me.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class ConfigFile {

    /**
     * Loads the configuration from disk.
     * When an error occours, this function returns a default configuration instead.
     */
    public static Config load() throws JsonSyntaxException {
        try {
            var json = Files.readString(Path.of(PATH));
            var config = new GsonBuilder().registerTypeAdapter(ServerEffect.class, new ServerEffectAdapter()).create().fromJson(json, Config.class);
            return config;
        } catch (IOException e) {
            Main.LOGGER.warn("Config file not found, using default configuration.");
            return new Config();
        }
    }

    /**
     * Serializes the configuration to disk.
     * When an error occours, this function fails silently.
     */
    public static void save(Config config) {
        try (var writer = new BufferedWriter(new FileWriter(PATH))) {
            var json = new GsonBuilder().registerTypeAdapter(ServerEffect.class, new ServerEffectAdapter()).create().toJson(config);
            writer.write(json);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save the config file", e);
            var client = Minecraft.getInstance();
            client.gui.toastManager().addToast(new SystemToast(
                new SystemToast.SystemToastId(10_000L),
                Component.translatable(K_SAVE_ERROR), Component.translatable(K_SAVE_ERROR_DESCRIPTION)
            ));
        }
    }

    private ConfigFile() {}

    private static final String PATH;
    private static final String K_SAVE_ERROR, K_SAVE_ERROR_DESCRIPTION;

    static {
        PATH = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve("blindme.json").toString();
        K_SAVE_ERROR = "blindme.error.save";
        K_SAVE_ERROR_DESCRIPTION = "blindme.error.save.description";
    }

    private static class ServerEffectAdapter implements JsonSerializer<ServerEffect>, JsonDeserializer<ServerEffect> {

        @Override
        public JsonElement serialize(ServerEffect src, Type typeOfSrc, JsonSerializationContext context) {
            if (src.enabled() == false) {
                return new JsonPrimitive("OFF");
            }
            if (ServerEffectPresets.BLINDNESS.equals(src)) {
                return new JsonPrimitive("BLINDNESS");
            }
            if (ServerEffectPresets.DARKNESS.equals(src)) {
                return new JsonPrimitive("DARKNESS");
            }
            JsonObject out = new JsonObject();
            out.addProperty("start", src.start());
            out.addProperty("end", src.end());
            return out;
        }

        @Override
        public ServerEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                String preset = json.getAsString();
                return switch(preset) {
                    case "OFF" -> ServerEffectPresets.OFF.toEffect();
                    case "BLINDNESS" -> ServerEffectPresets.BLINDNESS.toEffect();
                    case "DARKNESS" -> ServerEffectPresets.DARKNESS.toEffect();
                    default -> throw new JsonParseException("Unknown preset \"%s\"".formatted(preset));
                };
            }
            JsonObject object = json.getAsJsonObject();
            return new ServerEffect(object.get("start").getAsFloat(), object.get("end").getAsFloat());
        }
    }
}
