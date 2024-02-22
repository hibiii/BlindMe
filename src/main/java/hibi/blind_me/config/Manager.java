package hibi.blind_me.config;

import org.quiltmc.loader.api.config.v2.QuiltConfig;

import hibi.blind_me.EffectManager;

public final class Manager {
    
    public static final Config CONFIG = QuiltConfig.create("BlindMe", "config", Config.class);

    public static void init() {
        Manager.configureInstance();
        CONFIG.registerCallback(_1 -> Manager.configureInstance());
    }

    public static void configureInstance() {
        EffectManager.setDisabledCreative(CONFIG.creativeBypass.getRealValue());
        EffectManager.setDisabledSpectator(CONFIG.spectatorBypass.getRealValue());
        EffectManager.setDesiredEffect(CONFIG.getEffectForServer(EffectManager.getUniqueId()));
    }

    private Manager() {}
}