package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.config.ServerOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

public class Networking {

    public static String uniqueId = null;
    public static @Nullable ServerEffect effect = null;
    public static boolean opsBypass = false;
    public static boolean creativeBypass = false;
    public static boolean spectatorBypass = false;

    public static ServerOptions getServerOptions() {
        return getServerOptions(Networking.uniqueId);
    }

    public static ServerOptions getServerOptions(String uniqueId) {
        return Main.CONFIG.getServerOptions(uniqueId);
        
    }

    public static void joinCallback(ClientPacketListener handler, Object packetSender, Minecraft client) {
        if (client.isLocalServer()) {
            return;
        }
        ServerData info = handler.getServerData();
        uniqueId = "m@" + info.ip;
        Main.CONFIG.configureInstance();
    }

    public static void joinSingleplayerCallback(String worldName) {
        uniqueId = "s@" + worldName;
        Main.CONFIG.configureInstance();
    }

    public static void disconnectCallback(ClientPacketListener handler, Minecraft client) {
        effect = null;
    }
}
