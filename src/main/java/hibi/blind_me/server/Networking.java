package hibi.blind_me.server;

import java.util.HashSet;
import java.util.Set;

import hibi.blind_me.server.mix.ServerCommonPacketListenerImplAccessor;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.effect.MobEffectInstance;

public class Networking {

    public static Set<Connection> acknowledgements = new HashSet<Connection>();

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(ForceSettingsPayload.ID, ForceSettingsPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeForcePayload.ID, Networking::acknowledgeForcingCallback);
        ServerConfigurationConnectionEvents.CONFIGURE.register(Networking::configureCallback);
        ServerConfigurationConnectionEvents.DISCONNECT.register(Networking::cleanupSet);
        ServerPlayConnectionEvents.DISCONNECT.register(Networking::cleanupSet);
    }

    public static void configureCallback(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
        if (server.isSingleplayer()) {
            return;
        }
        var forcePayload = new ForceSettingsPayload(Main.CONFIG.effect, Main.CONFIG.opsBypass, Main.CONFIG.creativeBypass, Main.CONFIG.spectatorBypass);
        handler.send(ServerConfigurationNetworking.createS2CPacket(forcePayload));
    }

    public static void acknowledgeForcingCallback(AcknowledgeForcePayload _1, Context ctx) {
        var connection = Networking.connectionFromHandler(ctx.networkHandler());
        acknowledgements.add(connection);
    }

    public static void cleanupSet(ServerCommonPacketListenerImpl handler, MinecraftServer server) {
        acknowledgements.remove(connectionFromHandler(handler));
    }

    public static void applyPhantomEffect(ServerGamePacketListenerImpl handler) {
        if (acknowledgements.contains(Networking.connectionFromHandler(handler))) {
            return;
        }
        var type = Main.CONFIG.effect.getType();
        var blindness = new MobEffectInstance(type, MobEffectInstance.INFINITE_DURATION, 0, true, false);
        var packet = new ClientboundUpdateMobEffectPacket(handler.player.getId(), blindness, false);
        handler.send(packet);
    }

    public static Connection connectionFromHandler(ServerCommonPacketListenerImpl handler) {
        return ((ServerCommonPacketListenerImplAccessor)handler).getConnection();
    }
}
