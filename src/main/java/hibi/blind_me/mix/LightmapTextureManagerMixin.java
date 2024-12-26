package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import hibi.blind_me.EffectManager;
import hibi.blind_me.config.Manager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Holder;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    
    @Redirect(
        method = "getDarknessGamma(F)F",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStatusEffect(Lnet/minecraft/registry/Holder;)Lnet/minecraft/entity/effect/StatusEffectInstance;"
        )
    )
    StatusEffectInstance pulseIgnoresDarkness(ClientPlayerEntity that, Holder<StatusEffect> effect) {
        StatusEffectInstance playerEf = that.getStatusEffect(effect);
        if (Manager.hasDarknessPulse()) {
            return playerEf;
        }
        StatusEffectInstance modEf = EffectManager.getModEffect();
        if (modEf == null) {
            return playerEf;
        }
        if (modEf == playerEf) {
            return ((StatusEffectInstanceAccessor)modEf).getHiddenEffect();
        }
        return playerEf;
    }
}
