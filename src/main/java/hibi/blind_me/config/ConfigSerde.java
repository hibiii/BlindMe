package hibi.blind_me.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.Main;

public final class ConfigSerde {

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

    public static void saveToFile(Properties prop, String path) {
        try (var writer = new BufferedWriter(new FileWriter(path))) {
            prop.store(writer, null);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save config file: ", e);
        }
    }

    private ConfigSerde() {}
}
