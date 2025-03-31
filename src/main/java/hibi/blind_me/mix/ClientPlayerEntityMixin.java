package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(
        method = "isBlind()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    void sprintIgnoresBlindness(CallbackInfoReturnable<Boolean> ci) {
        StatusEffectInstance modEf = EffectManager.getModEffect();
        StatusEffectInstance playerEf = ((ClientPlayerEntity)(Object)this).getStatusEffect(StatusEffects.BLINDNESS);
        if (modEf != null && modEf == playerEf) {
            if (((StatusEffectInstanceAccessor)modEf).getHiddenEffect() == null) {
                ci.setReturnValue(false);
            }
        }
    }
}
