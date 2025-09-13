package hibi.blind_me.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import hibi.blind_me.config.ServerEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class Main {

    public static boolean enabled = true;
    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static Config CONFIG;

    public void serverInit() throws JsonSyntaxException, IOException {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            // If the server is integrated, we dummy ourselves out
            Main.enabled = false;
            CONFIG = null;
            return;
        }
        CONFIG = Config.load();
        if (CONFIG.effect == ServerEffect.OFF || CONFIG.effect == null) {
            throw new IllegalStateException("Starting a server with phantom effect set to null or OFF is not supported.");
        }
        Networking.register();
    }
}