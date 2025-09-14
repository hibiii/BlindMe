package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.config.ServerOptions;
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
    public static boolean isOpForBypass = false;

    public static ServerOptions getServerOptions() {
        ServerOptions opts;
        if (serverEnforced) {
            if (goodSettings) {
                if (isOpForBypass) {
                    opts = Main.CONFIG.getServerOptions(uniqueId);
                } else {
                    opts = new ServerOptions(Networking.effect, false, Networking.creativeBypass, Networking.spectatorBypass);
                }
            } else {
                opts = new ServerOptions(ServerEffect.OFF, false, false, false);
            }
        } else {
            opts = Main.CONFIG.getServerOptions(uniqueId);
        }
        return opts;
        
    }
    
    public static void joinBlindMeServerCallback(ForceSettingsPayload payload, Context ctx) {
        serverEnforced = true;
        goodSettings = false;
        isOpForBypass = false;
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
            goodSettings = false;
            isOpForBypass = false;
            return;
        }
        ServerInfo info = handler.getServerInfo();
        uniqueId = "m@" + info.address;
        Main.CONFIG.configureInstance();
    }

    public static void joinSingleplayerCallback(String worldName) {
        uniqueId = "s@" + worldName;
        serverEnforced = false;
        goodSettings = false;
        isOpForBypass = false;
        Main.CONFIG.configureInstance();
    }

    public static void disconnectCallback(ClientPlayNetworkHandler handler, MinecraftClient client) {
        serverEnforced = false;
        goodSettings = false;
        isOpForBypass = false;
        effect = null;
    }
}
