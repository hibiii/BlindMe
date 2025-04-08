package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

public record ServerOptions(
    // The effect for this server.
    // `null` in this context defers to the default effect.
    @Nullable ServerEffect effect,

    // If locked, the options cannot be modified.
    boolean locked,

    // Controls whether or not creative mode bypasses the mod effect.
    // `null` in this context defers to the global setting.
    @Nullable Boolean creativeBypass,

    // Controls whether or not spectator mode bypasses the mod effect.
    // `null` in this context defers to the global setting.
    @Nullable Boolean spectatorBypass
) {
    public ServerOptions withEffect(@Nullable ServerEffect effect) {
        return new ServerOptions(effect, this.locked, this.creativeBypass, this.spectatorBypass);
    }
    public ServerOptions butLocked() {
        return new ServerOptions(this.effect, true, this.creativeBypass, this.spectatorBypass);
    }
    public ServerOptions butUnlocked() {
        return new ServerOptions(this.effect, false, this.creativeBypass, this.spectatorBypass);
    }
    public ServerOptions withCreativeBypass(@Nullable Boolean creativeBypass) {
        return new ServerOptions(this.effect, this.locked, creativeBypass, this.spectatorBypass);
    }
    public ServerOptions withSpectatorBypass(@Nullable Boolean spectatorBypass) {
        return new ServerOptions(this.effect, this.locked, this.creativeBypass, spectatorBypass);
    }

    public static final ServerOptions DEFAULT = new ServerOptions(null, false, null, null);
}
