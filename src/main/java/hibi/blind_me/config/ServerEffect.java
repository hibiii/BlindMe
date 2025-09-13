package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

public enum ServerEffect {
    OFF, // Represents no vision-impairing effects
    BLINDNESS,
    DARKNESS;
    // Additionally in some contexts, `null` means deferral to a default effect

    public @Nullable RegistryEntry<StatusEffect> getType() {
        return switch (this) {
            case OFF -> null;
            case BLINDNESS -> StatusEffects.BLINDNESS;
            case DARKNESS -> StatusEffects.DARKNESS;
        };
    }
}
