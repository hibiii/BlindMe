package hibi.blind_me.server;

import java.util.Map;
import java.util.WeakHashMap;

import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.server.mix.ServerCommonNetworkHandlerAccessor;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Networking {
    
    private static Map<ClientConnection,Boolean> acknowledgements = new WeakHashMap<>();

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ForceEffectPayload.ID, ForceEffectPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeForcePayload.ID, Networking::acknowledgeForcingCallback);
        ServerConfigurationConnectionEvents.CONFIGURE.register(Networking::configureCallback);
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
        var con = ((ServerCommonNetworkHandlerAccessor)ctx.networkHandler()).getConnection();
        Networking.acknowledgements.put(con, true);
    }

    static void fallbackEffectManager(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        var con = ((ServerCommonNetworkHandlerAccessor)newPlayer.networkHandler).getConnection();
        if (Networking.acknowledgements.getOrDefault(con, true)) {
            return;
        }
        newPlayer.sendMessage(Text.literal("Test"));
    }
}
