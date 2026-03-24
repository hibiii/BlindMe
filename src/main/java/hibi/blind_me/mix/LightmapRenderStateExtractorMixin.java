package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
    
    @ModifyVariable(
        method = "extract",
        at = @At("STORE"),
        ordinal = 2
    )
    float pulseIgnoresDarkness(float original) {
        if (Main.CONFIG.disableDarknessPulse
            && EffectManager.getDesiredEffect() == MobEffects.DARKNESS
            && EffectManager.getModEffect() instanceof MobEffectInstance modEf
            && Minecraft.getInstance().player instanceof LocalPlayer p
            && p.getEffect(MobEffects.DARKNESS) == modEf
            && ((MobEffectInstanceAccessor)modEf).getHiddenEffect() == null
        ) {
            return 0.0f;
        } else {
            return original;
        }
    }
}
