package hibi.blind_me.server.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import hibi.blind_me.server.Main;
import hibi.blind_me.server.Networking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @WrapWithCondition(
        method = "onEffectAdded",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    boolean preventApplication(ServerGamePacketListenerImpl handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (ClientboundUpdateMobEffectPacket)packet; // Packet will always be ClientboundUpdateMobEffectPacket
        return pack.getEffect() != Main.CONFIG.effect.getType();
    }

    @WrapWithCondition(
        method = "onEffectUpdated",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    // IMPORTANT IMPLEMENTATION DETAIL: Blindness and Darkness effects do not change when upgraded, therefore, we can
    // take a shortcut and disable upgrading those straight up.
    boolean preventUpgrade(ServerGamePacketListenerImpl handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (ClientboundUpdateMobEffectPacket)packet; // Packet will always be ClientboundUpdateMobEffectPacket
        return pack.getEffect() != Main.CONFIG.effect.getType();
    }

    @WrapWithCondition(
        method = "onEffectsRemoved",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
        )
    )
    boolean preventRemoval(ServerGamePacketListenerImpl handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (ClientboundRemoveMobEffectPacket)packet; // Packet will always be ClientboundRemoveMobEffectPacket
        return pack.effect() != Main.CONFIG.effect.getType();
    }
}
