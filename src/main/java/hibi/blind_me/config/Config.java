package hibi.blind_me.config;

import java.util.HashMap;
import java.util.Map;

import hibi.blind_me.config.Enums.ServerEffect;

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
    }

    public void save() {
        // TODO: unimplemented
    }

    public void load() {
        // TODO: unimplemented
    }
}
