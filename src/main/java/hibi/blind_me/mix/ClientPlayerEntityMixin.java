package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;

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
        return false;
    }
}
