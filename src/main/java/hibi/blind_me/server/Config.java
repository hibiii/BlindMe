package hibi.blind_me.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

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

    public static Config load() throws IOException {
        try (var reader = new BufferedReader(new FileReader(PATH))) {
            var props = new Properties();
            props.load(reader);
            var config = new Config();
            config.effect = switch(props.getProperty("effect").toLowerCase()) {
                case "blindness" -> ServerEffect.BLINDNESS;
                case "darkness" -> ServerEffect.BLINDNESS;
                default -> throw new IllegalArgumentException("Bad effect in config file, expected \"blindness\" or \"darkness\"");
            };
            config.creativeBypass = switch(props.getProperty("creative_bypass").toLowerCase()) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException("Bad creative bypass in config file, expected \"true\" or \"false\"");
            };
            config.spectatorBypass = switch(props.getProperty("spectators_bypass").toLowerCase()) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException("Bad spectator bypass in config file, expected \"true\" or \"false\"");
            };
            config.opsBypass = switch(props.getProperty("ops_bypass").toLowerCase()) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException("Bad OPs bypass in config file, expected \"true\" or \"false\"");
            };
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
            writer.write(DEFAULT_CONFIG_FILE);
        } catch (Exception e) {
            Main.LOGGER.error("Could not save the config file", e);
        }
    }

    private static final String PATH = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve("blindme.server.json").toString();

    private static final String DEFAULT_CONFIG_FILE = """
        # The selected vision-impairing effect for this server.
        # Can be one of "blindness" or "darkness".
        effect = blindness
        # When clients without BlindMe log in, they will be given that effect purely at
        # the network level, but the real player in the world will not have it. When a
        # client with BlindMe logs in, it will enforce the chosen effect, since it
        # employs quality-of-life improvements, such as the ability to sprint when
        # blinded, and visual strobe reduction.

        # Controls whether players in creative mode have their effect removed.
        # - Only available for clients using BlindMe. The server-side fallback system
        #   is relatively primitive.
        # Can be one of "true" or "false".
        creative_bypass = false

        # Controls whether players in spectator mode have their effect removed.
        # - Only available for clients using BlindMe. The server-side fallback system
        #   is relatively primitive.
        # Can be one of "true" or "false".
        spectators_bypass = true

        # Controls whether operators using BlindMe can change their settings.
        # - Applicable to players with permission level 2 or higher: game masters,
        #   administrators and owners.
        # - Only available for clients using BlindMe. The server-side fallback system
        #   is relatively primitive.
        # Can be one of "true" or "false".
        ops_bypass = false
        """;
}
