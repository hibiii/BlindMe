package hibi.blind_me.server;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import hibi.blind_me.config.ServerEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class Networking {
    
    // Undefined Behavior when: a connection's game profile is changed
    private static Map<UUID,Boolean> acknowledgements = new WeakHashMap<>();

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ForceEffectPayload.ID, ForceEffectPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeForcePayload.ID, Networking::acknowledgeForcingCallback);
        ServerConfigurationConnectionEvents.CONFIGURE.register(Networking::configureCallback);
        ServerPlayConnectionEvents.JOIN.register(Networking::initFallbackEffect);
        ServerPlayerEvents.AFTER_RESPAWN.register(Networking::fallbackEffectManager);
    }

    public static void configureCallback(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        if (server.isSingleplayer()) {
            return;
        }
        var forcePayload = new ForceEffectPayload(ServerEffect.BLINDNESS);
        handler.sendPacket(ServerConfigurationNetworking.createS2CPacket(forcePayload));
    }

    public static void acknowledgeForcingCallback(AcknowledgeForcePayload _1, Context ctx) {
       var uuid = ctx.networkHandler().getDebugProfile().getId();
       Networking.acknowledgements.put(uuid, true);
    }

    static void initFallbackEffect(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        applyPhantomEffect(handler);
    }

    static void fallbackEffectManager(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        applyPhantomEffect(newPlayer.networkHandler);
    }

    private static void applyPhantomEffect(ServerPlayNetworkHandler handler) {
        var uuid = handler.getDebugProfile().getId();
        if (Networking.acknowledgements.getOrDefault(uuid, false)) {
            return;
        }
        var blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, StatusEffectInstance.INFINITE, 0, true, false);
        var packet = new EntityStatusEffectS2CPacket(handler.player.getId(), blindness, false);
        handler.sendPacket(packet);
    }
}
