package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.config.Manager;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    
    @Inject(
        method = "getDarkness",
        at = @At("HEAD"),
        cancellable = true
    )
    void pulseIgnoresDarkness(LivingEntity entity, float factor, float progress, CallbackInfoReturnable<Float> info) {
        if (Manager.CONFIG.disableDarknessPulse
            && EffectManager.getDesiredEffect() == StatusEffects.DARKNESS
            && EffectManager.getModEffect() instanceof StatusEffectInstance modEf
            && ((StatusEffectInstanceAccessor)modEf).getHiddenEffect() == null
        ) {
            info.setReturnValue(0f);
        }
    }
}
