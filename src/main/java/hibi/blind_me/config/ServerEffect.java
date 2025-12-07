package hibi.blind_me.config;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.Nullable;

public enum ServerEffect {
    OFF, // Represents no vision-impairing effects
    BLINDNESS,
    DARKNESS;
    // Additionally in some contexts, `null` means deferral to a default effect

    public @Nullable Holder<MobEffect> getType() {
        return switch (this) {
            case OFF -> null;
            case BLINDNESS -> MobEffects.BLINDNESS;
            case DARKNESS -> MobEffects.DARKNESS;
        };
    }
}
