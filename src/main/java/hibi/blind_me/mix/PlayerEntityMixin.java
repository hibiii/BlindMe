package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(
        method = "hasBlindnessEffect()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    void sprintIgnoresBlindness(CallbackInfoReturnable<Boolean> ci) {
        StatusEffectInstance modEf = EffectManager.getModEffect();
        StatusEffectInstance playerEf = ((PlayerEntity)(Object)this).getStatusEffect(StatusEffects.BLINDNESS);
        if (modEf != null && modEf == playerEf) {
            if (((StatusEffectInstanceAccessor)modEf).getHiddenEffect() == null) {
                ci.setReturnValue(false);
            }
        }
    }
}
