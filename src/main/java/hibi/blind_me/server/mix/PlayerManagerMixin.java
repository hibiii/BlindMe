package hibi.blind_me.server.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.server.Networking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    
    @Inject(
        method = "sendStatusEffects(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/server/network/ServerPlayNetworkHandler;)V",
        at = @At("HEAD")
    )
    void attachPhantomEffectAlways(LivingEntity _1, ServerPlayNetworkHandler handler, CallbackInfo info) {
        Networking.applyPhantomEffect(handler);
    }
}
