package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(
        method = "isMobilityRestricted()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    void sprintIgnoresBlindness(CallbackInfoReturnable<Boolean> ci) {
        MobEffectInstance modEf = EffectManager.getModEffect();
        MobEffectInstance playerEf = ((Player)(Object)this).getEffect(MobEffects.BLINDNESS);
        if (modEf != null && modEf == playerEf) {
            if (((MobEffectInstanceAccessor)modEf).getHiddenEffect() == null) {
                ci.setReturnValue(false);
            }
        }
    }
}
