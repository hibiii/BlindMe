package hibi.blind_me.config;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.EffectManager;
import net.minecraft.block.Portal.Effect;

public class Config {

    // If set to true, creative mode won't have the blindness effect applied
    public boolean creativeBypass = false;

    // If set to true, spectator mode won't have the blindness effect applied
    public boolean spectatorBypass = true;

    // The list of servers/worlds and configuration for each individually
    public Map<String, @Nullable ServerEffect> servers = new HashMap<String, @Nullable ServerEffect>();

    // If set to true, the Darkness effect will not pulse the brightness when it is applied by the mod
    public boolean disableDarknessPulse = true;

    // The default effect to apply when connected to a server without a specific setting
    public ServerEffect defaultServerEffect = ServerEffect.OFF;

    public ServerEffect getEffectForServer(String uniqueId) {
        var ef = this.servers.get(uniqueId);
        return (ef == null) ? this.defaultServerEffect : ef;
    }

    public void setEffectForServer(String uniqueId, @Nullable ServerEffect ef) {
        this.servers.put(uniqueId, ef);
        ConfigFile.save(this);
        if (ef == null) {
            EffectManager.setDesiredEffect(this.defaultServerEffect);
        } else {
            EffectManager.setDesiredEffect(ef);
        }
    }

    public void configureInstance() {
        EffectManager.setDisabledCreative(this.creativeBypass);
        EffectManager.setDisabledSpectator(this.spectatorBypass);
        EffectManager.setDesiredEffect(this.getEffectForServer(EffectManager.getUniqueId()));
    }
}
