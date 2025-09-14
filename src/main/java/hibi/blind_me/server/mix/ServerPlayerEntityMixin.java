package hibi.blind_me.server.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import hibi.blind_me.server.Main;
import hibi.blind_me.server.Networking;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @WrapWithCondition(
        method = "onStatusEffectApplied",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
        )
    )
    boolean preventApplication(ServerPlayNetworkHandler handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (EntityStatusEffectS2CPacket)packet; // Packet will always be EntityStatusEffectS2CPacket
        return pack.getEffectId() != Main.CONFIG.effect.getType();
    }

    @WrapWithCondition(
        method = "onStatusEffectUpgraded",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
        )
    )
    // IMPORTANT IMPLEMENTATION DETAIL: Blindness and Darkness effects do not change when upgraded, therefore, we can
    // take a shortcut and disable upgrading those straight up.
    boolean preventUpgrade(ServerPlayNetworkHandler handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (EntityStatusEffectS2CPacket)packet; // Packet will always be EntityStatusEffectS2CPacket
        return pack.getEffectId() != Main.CONFIG.effect.getType();
    }

    @WrapWithCondition(
        method = "onStatusEffectsRemoved",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
        )
    )
    boolean preventRemoval(ServerPlayNetworkHandler handler, Packet<?> packet) {
        if (!Main.enabled || Networking.acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return true;
        }
        var pack = (RemoveEntityStatusEffectS2CPacket)packet; // Packet will always be RemoveEntityStatusEffectS2CPacket
        return pack.effect() != Main.CONFIG.effect.getType();
    }
}
