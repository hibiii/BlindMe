package hibi.blind_me.server;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class Networking {
    
    // Undefined Behavior when: a connection's game profile is changed
    private static Map<UUID,Boolean> acknowledgements = new WeakHashMap<>();

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ForceEffectPayload.ID, ForceEffectPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeForcePayload.ID, Networking::acknowledgeForcingCallback);
        ServerConfigurationConnectionEvents.CONFIGURE.register(Networking::configureCallback);
    }

    public static void configureCallback(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        if (server.isSingleplayer()) {
            return;
        }
        var forcePayload = new ForceEffectPayload(Main.CONFIG.effect);
        handler.sendPacket(ServerConfigurationNetworking.createS2CPacket(forcePayload));
    }

    public static void acknowledgeForcingCallback(AcknowledgeForcePayload _1, Context ctx) {
       throw new IllegalStateException("Not Implemented");
    }

    public static void applyPhantomEffect(ServerPlayNetworkHandler handler) {
        // TODO: Acknowledgements
        var type = Main.CONFIG.effect.getType();
        var blindness = new StatusEffectInstance(type, StatusEffectInstance.INFINITE, 0, true, false);
        var packet = new EntityStatusEffectS2CPacket(handler.player.getId(), blindness, false);
        handler.sendPacket(packet);
    }
}
