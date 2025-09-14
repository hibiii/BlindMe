package hibi.blind_me.config;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Networking;

public class Config {

    // If set to true, creative mode won't have the blindness effect applied
    public boolean creativeBypass = false;

    // If set to true, spectator mode won't have the blindness effect applied
    public boolean spectatorBypass = true;

    // The list of servers/worlds and configuration for each individually
    private Map<String, ServerOptions> servers = new HashMap<String, ServerOptions>();

    // If set to true, the Darkness effect will not pulse the brightness when it is applied by the mod
    public boolean disableDarknessPulse = true;

    // The default effect to apply when connected to a server without a specific setting
    public ServerEffect defaultServerEffect = ServerEffect.OFF;

    // Returns *any* `ServerEffect` for the given server.
    // If there isn't a server-specific mapping, this function returns the default one.
    public ServerEffect getEffectForServer(String uniqueId) {
        var ef = this.servers.getOrDefault(uniqueId, ServerOptions.DEFAULT).effect();
        return (ef == null) ? this.defaultServerEffect : ef;
    }

    // Sets the current effect for a server, then serializes.
    // If effect is `null`, unmaps the server-specific effect binding.
    public void setEffectForServer(String uniqueId, @Nullable ServerEffect ef) {
        var opts = this.servers.getOrDefault(uniqueId, ServerOptions.DEFAULT);
        opts = opts.withEffect(ef);
        this.servers.put(uniqueId, opts);
        ConfigFile.save(this);
        if (ef == null) {
            EffectManager.setDesiredEffect(this.defaultServerEffect);
        } else {
            EffectManager.setDesiredEffect(ef);
        }
    }

    // Saves the options for a given server, then serializes.
    // If the options are exactly equal to `ServerOptions.DEFAULT`, it is discarded instead.
    public void setServerOptions(String uniqueId, ServerOptions opts) {
        if (ServerOptions.DEFAULT.equals(opts)) {
            this.servers.remove(uniqueId);
        } else {
            this.servers.put(uniqueId, opts);
        }
        ConfigFile.save(this);
    }

    // Returns options for a given server.
    // If it doesn't have specific optons, then defaults are returned.
    public ServerOptions getServerOptions(String uniqueId) {
        ServerOptions opts;
        if (Networking.serverEnforced) {
            if (Networking.goodSettings) {
                opts = new ServerOptions(Networking.effect, false, Networking.creativeBypass, Networking.spectatorBypass);
            } else {
                opts = new ServerOptions(ServerEffect.OFF, false, false, false);
            }
        } else {
            opts = this.servers.getOrDefault(uniqueId, ServerOptions.DEFAULT);
        }
        return opts;
    }

    // Refreshes settings and configurations in other parts of the code base.
    public void configureInstance() {
        var opts = this.getServerOptions(Networking.uniqueId);
        EffectManager.setDisabledCreative(opts.creativeBypass() instanceof Boolean b? b: this.creativeBypass);
        EffectManager.setDisabledSpectator(opts.spectatorBypass() instanceof Boolean b? b: this.spectatorBypass);
        EffectManager.setDesiredEffect(opts.effect() instanceof ServerEffect e? e: this.defaultServerEffect);
    }
}
