package hibi.blind_me.server.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.server.Main;
import hibi.blind_me.server.Networking;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    
    @Inject(
        method = "sendActiveEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/server/network/ServerGamePacketListenerImpl;)V",
        at = @At("HEAD")
    )
    void attachPhantomEffectAlways(LivingEntity _1, ServerGamePacketListenerImpl handler, CallbackInfo info) {
        if (Main.enabled) {
            Networking.applyPhantomEffect(handler);
        }
    }
}
