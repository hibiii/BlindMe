package hibi.blind_me.server;

import java.util.HashSet;
import java.util.Set;

import hibi.blind_me.server.mix.ServerCommonNetworkHandlerAccessor;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class Networking {

    public static Set<ClientConnection> acknowledgements = new HashSet<ClientConnection>();

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ForceSettingsPayload.ID, ForceSettingsPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeForcePayload.ID, Networking::acknowledgeForcingCallback);
        ServerConfigurationConnectionEvents.CONFIGURE.register(Networking::configureCallback);
        ServerConfigurationConnectionEvents.DISCONNECT.register(Networking::cleanupSet);
        ServerPlayConnectionEvents.DISCONNECT.register(Networking::cleanupSet);
    }

    public static void configureCallback(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        if (server.isSingleplayer()) {
            return;
        }
        var forcePayload = new ForceSettingsPayload(Main.CONFIG.effect, Main.CONFIG.opsBypass, Main.CONFIG.creativeBypass, Main.CONFIG.spectatorBypass);
        handler.sendPacket(ServerConfigurationNetworking.createS2CPacket(forcePayload));
    }

    public static void acknowledgeForcingCallback(AcknowledgeForcePayload _1, Context ctx) {
        var connection = Networking.connectionFromHandler(ctx.networkHandler());
        acknowledgements.add(connection);
    }

    public static void cleanupSet(ServerCommonNetworkHandler handler, MinecraftServer server) {
        acknowledgements.remove(connectionFromHandler(handler));
    }

    public static void applyPhantomEffect(ServerPlayNetworkHandler handler) {
        if (acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return;
        }
        var type = Main.CONFIG.effect.getType();
        var blindness = new StatusEffectInstance(type, StatusEffectInstance.INFINITE, 0, true, false);
        var packet = new EntityStatusEffectS2CPacket(handler.player.getId(), blindness, false);
        handler.sendPacket(packet);
    }

    public static ClientConnection connectionFromHandler(ServerCommonNetworkHandler handler) {
        return ((ServerCommonNetworkHandlerAccessor)handler).getConnection();
    }
}
