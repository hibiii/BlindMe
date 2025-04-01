package hibi.blind_me.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    // If set to true, creative mode won't have the blindness effect applied
    public boolean creativeBypass = false;

    // If set to true, spectator mode won't have the blindness effect applied
    public boolean spectatorBypass = true;

    // The list of servers/worlds and configuration for each individually
    public Map<String,ServerEffect> servers = new HashMap<String, ServerEffect>();

    // If set to true, the Darkness effect will not pulse the brightness when it is applied by the mod
    public boolean disableDarknessPulse = true;

    public ServerEffect getEffectForServer(String uniqueId) {
        return this.servers.getOrDefault(uniqueId, ServerEffect.OFF);
    }

    public void setEffectForServer(String uniqueId, ServerEffect ef) {
        this.servers.put(uniqueId, ef);
        EffectManager.setDesiredEffect(ef);
        this.save();
    }

    public void save() {
        var props = new Properties();
        props.put("creativeBypass", Boolean.toString(this.creativeBypass));
        props.put("spectatorBypass", Boolean.toString(this.spectatorBypass));
        props.put("disableDarknessPulse", Boolean.toString(this.disableDarknessPulse));
        this.servers.forEach((key, value) -> props.put(key, value.toString()));
        ConfigSerde.saveToFile(props, CONFIG_FILE);
    }

    public void load() {
        var props = ConfigSerde.loadFromFile(CONFIG_FILE);
        if (props == null) {
            return;
        }
        this.creativeBypass = Boolean.parseBoolean((String)props.get("creativeBypass"));
        this.spectatorBypass = Boolean.parseBoolean((String)props.getOrDefault("spectatorBypass", "true"));
        this.disableDarknessPulse = Boolean.parseBoolean((String)props.getOrDefault("disableDarknessPulse", "true"));
        props.forEach((key1, val1) -> {
            var key = (String) key1;
            if ((key.charAt(0) != 's' || key.charAt(0) != 'm') && key.charAt(1) != '@') {
                return;
            }
            var val = ServerEffect.parse(val1);
            if (val == null) {
                Main.LOGGER.error("Unable to parse config entry \""+key+"\" = \""+val1+"\", defaulting to OFF");
                return;
            }
            this.servers.put(key, val);
        });
    }

    private static final String CONFIG_FILE;

    static {
        CONFIG_FILE = FabricLoader.getInstance().getConfigDir().toAbsolutePath().resolve("blindme.prop").toString();
    }
}
