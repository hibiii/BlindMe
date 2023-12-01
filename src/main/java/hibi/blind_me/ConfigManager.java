package hibi.blind_me;

import org.quiltmc.loader.api.config.v2.QuiltConfig;

public final class ConfigManager {
    public static final Config CONFIG = QuiltConfig.create("BlindMe", "config", Config.class);

    public static void init() {
        ConfigManager.configureInstance();
        CONFIG.registerCallback(_1 -> ConfigManager.configureInstance());
    }

    public static void configureInstance() {
        EffectManager.setDisabledCreative(CONFIG.creativeBypass.getRealValue());
        EffectManager.setDisabledSpectator(CONFIG.spectatorBypass.getRealValue());
    }

    private ConfigManager() {}
}