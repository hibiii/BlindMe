package hibi.blind_me.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigFile {

    // FIXME: #7
    /**
     * Loads the configuration from disk.
     * When an error occours, this function returns a default configuration instead.
     */
    public static Config load() {
        try (var reader = new BufferedReader(new FileReader(PATH))) {
            var json = Files.readString(Path.of(PATH));
            var config = new Gson().fromJson(json, Config.class);
            return config;
        } catch (Exception e) {
            Main.LOGGER.error("Could not load config file: ", e);
            return new Config();
        }
    }

    // TODO: Error notifications
    /**
     * Serializes the configuration to disk.
     * When an error occours, this function fails silently.
     * @param config
     */
    public static void save(Config config) {
        try (var writer = new BufferedWriter(new FileWriter(PATH))) {
            var json = new Gson().toJson(config);
            writer.write(json);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save config file: ", e);
        }
    }

    private ConfigFile() {}

    private static final String PATH;

    static {
        PATH = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve("blindme.json").toString();
    }
}
