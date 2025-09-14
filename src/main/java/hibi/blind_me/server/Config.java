package hibi.blind_me.server;

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

import hibi.blind_me.config.ServerEffect;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    // The effect the server should enforce on players
    public ServerEffect effect = ServerEffect.BLINDNESS;

    // Controls if OPs can disregard server settings and change the effect on
    // their clients.
    public boolean opsBypass = true;

    public boolean creativeBypass = false;
    public boolean spectatorBypass = true;

    public static Config load() throws IOException, JsonSyntaxException {
        try (var reader = new BufferedReader(new FileReader(PATH))) {
            var json = Files.readString(Path.of(PATH));
            var config = new Gson().fromJson(json, Config.class);
            return config;
        } catch (FileNotFoundException e) {
            Main.LOGGER.warn("Config file not found, creating a new file and using default configuration.");
            var config = new Config();
            config.save();
            return config;
        }
    }

    private void save() {
        try (var writer = new BufferedWriter(new FileWriter(PATH))) {
            var json = new Gson().toJson(this);
            writer.write(json);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save the config file", e);
        }
    }

    private static final String PATH = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve("blindme.server.json").toString();
}
