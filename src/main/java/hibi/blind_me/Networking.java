package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.server.AcknowledgeForcePayload;
import hibi.blind_me.server.ForceSettingsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.Context;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;

public class Networking {

    public static boolean serverEnforced;
    public static boolean goodSettings;
    public static String uniqueId = null;
    public static @Nullable ServerEffect effect = null;
    public static boolean opsBypass = false;
    public static boolean creativeBypass = false;
    public static boolean spectatorBypass = false;
    
    public static void joinBlindMeServerCallback(ForceSettingsPayload payload, Context ctx) {
        serverEnforced = true;
        goodSettings = false;
        var effect = payload.effect();
        var opsBypass = payload.opsBypass();
        var creativeBypass = payload.creativeBypass();
        var spectatorBypass = payload.spectatorBypass();
        if (effect != null && opsBypass != null && creativeBypass != null && spectatorBypass != null) {
            Networking.effect = effect;
            Networking.opsBypass = opsBypass;
            Networking.creativeBypass = creativeBypass;
            Networking.spectatorBypass = spectatorBypass;
            goodSettings = true;
            ClientConfigurationNetworking.send(new AcknowledgeForcePayload());
        }
    }

    public static void joinCallback(ClientPlayNetworkHandler handler, Object packetSender, MinecraftClient client) {
        if (client.isInSingleplayer()) {
            serverEnforced = false;
            return;
        }
        ServerInfo info = handler.getServerInfo();
        uniqueId = "m@" + info.address;
        Main.CONFIG.configureInstance();
    }

    public static void joinSingleplayerCallback(String worldName) {
        uniqueId = "s@" + worldName;
        serverEnforced = false;
        Main.CONFIG.configureInstance();
    }

    public static void disconnectCallback(ClientPlayNetworkHandler handler, MinecraftClient client) {
        serverEnforced = false;
        effect = null;
    }
}
