package hibi.blind_me.config;

import hibi.blind_me.EffectManager;

public final class Manager {

    private static boolean pulseM;
    
    public static Config CONFIG = new Config();

    public static void init() {
        CONFIG = ConfigFile.load();
        Manager.configureInstance();
    }

    public static void configureInstance() {
        EffectManager.setDisabledCreative(CONFIG.creativeBypass);
        EffectManager.setDisabledSpectator(CONFIG.spectatorBypass);
        EffectManager.setDesiredEffect(CONFIG.getEffectForServer(EffectManager.getUniqueId()));
        pulseM = !CONFIG.disableDarknessPulse;
    }

    public static boolean hasDarknessPulse() {
        return pulseM;
    }

    public static void save() {
        ConfigFile.save(CONFIG);
        Manager.configureInstance();
    }

    private Manager() {}
}