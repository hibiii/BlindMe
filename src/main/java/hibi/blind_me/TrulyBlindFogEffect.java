package hibi.blind_me;

import net.minecraft.client.render.BackgroundRenderer.FogEffect;
import net.minecraft.client.render.BackgroundRenderer.FogParameters;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class TrulyBlindFogEffect implements FogEffect {

    @Override
    public void applyFogEffects(FogParameters parameters, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
        // Sky
        if (parameters.fogType == FogType.field_20945) {
            parameters.fogStart = 0f;
            parameters.fogEnd = 1.9f;
        } else { // Terrain
            parameters.fogStart = 0.5f;
            parameters.fogEnd = 1.9f;
        }
    }

    @Override
    public float fadeAsEffectWearsOff(LivingEntity entity, StatusEffectInstance effect, float horizonShading, float tickDelta) {
        return 0f;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return Main.TRULY_BLIND;
    }
}
