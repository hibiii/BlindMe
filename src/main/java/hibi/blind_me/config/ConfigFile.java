package hibi.blind_me.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
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
    public static Config load() throws IOException, JsonSyntaxException {
        try (var reader = new BufferedReader(new FileReader(PATH))) {
            var json = Files.readString(Path.of(PATH));
            var config = new Gson().fromJson(json, Config.class);
            return config;
        } catch (FileNotFoundException e) {
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
            var json = new Gson().toJson(config);
            writer.write(json);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save the config file", e);
            var client = Minecraft.getInstance();
            client.getToastManager().addToast(SystemToast.multiline(
                client, new SystemToast.SystemToastId(10_000L),
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
}
