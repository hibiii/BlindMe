package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

public enum ServerEffect {
    OFF,
    BLINDNESS,
    DARKNESS;

    public static @Nullable ServerEffect parse(Object string) {
        if ("BLINDNESS".equals(string)) {
            return BLINDNESS;
        }
        if ("DARKNESS".equals(string)) {
            return DARKNESS;
        }
        if ("OFF".equals(string)) {
            return OFF;
        }
        return null;
    }
}
