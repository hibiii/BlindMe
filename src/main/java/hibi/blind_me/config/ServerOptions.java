package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

public record ServerOptions(
    @Nullable ServerEffect effect
) {
    public ServerOptions withEffect(@Nullable ServerEffect effect) {
        return new ServerOptions(effect);
    }

    public static final ServerOptions DEFAULT = new ServerOptions(null);
}
