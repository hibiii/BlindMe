package hibi.blind_me;

import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class ConfigManager {
    public static final Config CONFIG = QuiltConfig.create("blind_me", "config", Config.class);

    public static void init() {
        ConfigManager.configureInstance();
        CONFIG.registerCallback(_1 -> ConfigManager.configureInstance());
    }

    public static void configureInstance() {
    }
}