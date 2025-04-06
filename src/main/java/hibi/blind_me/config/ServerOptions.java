package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

public record ServerOptions(
    // The effect for this server.
    // `null` in this context refers to defer to the default effect.
    @Nullable ServerEffect effect,

    // If locked, the options cannot be modified.
    boolean locked
) {
    public ServerOptions withEffect(@Nullable ServerEffect effect) {
        return new ServerOptions(effect, this.locked);
    }
    public ServerOptions butLocked() {
        return new ServerOptions(this.effect, true);
    }
    public ServerOptions butUnlocked() {
        return new ServerOptions(this.effect, false);
    }

    public static final ServerOptions DEFAULT = new ServerOptions(null, false);
}
