package hibi.blind_me.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigFile {

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

    public static void save(Config config) {
        try (var writer = new BufferedWriter(new FileWriter(PATH))) {
            var json = new Gson().toJson(config);
            writer.write(json);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save config file: ", e);
        }
    }

    @Deprecated()
    public static @Nullable Properties loadFromFile(String path) {
        try (var reader = new BufferedReader(new FileReader(path))) {
            var props = new Properties();
            props.load(reader);
            return props;
        } catch (Exception e) {
            Main.LOGGER.error("Could not load config file: ", e);
            return null;
        }
    }

    @Deprecated()
    public static void saveToFile(Properties prop, String path) {
        try (var writer = new BufferedWriter(new FileWriter(path))) {
            prop.store(writer, null);
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
