package hibi.blind_me.server;

import hibi.blind_me.config.ServerEffect;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;

public class Networking {
    
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
        var forcePayload = new ForceEffectPayload(ServerEffect.BLINDNESS);
        handler.sendPacket(ServerConfigurationNetworking.createS2CPacket(forcePayload));
    }

    public static void acknowledgeForcingCallback(AcknowledgeForcePayload _1, Context ctx) {
        // TODO: Unimplemented
        throw new UnsupportedOperationException("Unimplemented");
    }
}
