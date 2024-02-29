package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import hibi.blind_me.EffectManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Redirect(
        method = "canStartSprinting()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"
        )
    )
    boolean sprintIgnoresBlindness(ClientPlayerEntity that, StatusEffect effect) {
        StatusEffectInstance modEf = EffectManager.getModEffect();
        StatusEffectInstance playerEf = that.getStatusEffect(effect);
        if (playerEf == null) {
            return false;
        }
        if (playerEf == modEf) {
            return ((StatusEffectInstanceAccessor)modEf).getHiddenEffect() != null;
        }
        return true;
    }
}
