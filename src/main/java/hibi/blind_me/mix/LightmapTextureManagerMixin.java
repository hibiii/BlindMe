package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LightTexture.class)
public class LightmapTextureManagerMixin {
    
    @Inject(
        method = "calculateDarknessScale",
        at = @At("HEAD"),
        cancellable = true
    )
    void pulseIgnoresDarkness(LivingEntity entity, float factor, float progress, CallbackInfoReturnable<Float> info) {
        if (Main.CONFIG.disableDarknessPulse
            && EffectManager.getDesiredEffect() == MobEffects.DARKNESS
            && EffectManager.getModEffect() instanceof MobEffectInstance modEf
            && entity.getEffect(MobEffects.DARKNESS) == modEf
            && ((StatusEffectInstanceAccessor)modEf).getHiddenEffect() == null
        ) {
            info.setReturnValue(0f);
        }
    }
}
