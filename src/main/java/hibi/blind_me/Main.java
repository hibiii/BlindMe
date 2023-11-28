package hibi.blind_me;

import org.apache.commons.lang3.StringEscapeUtils;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static String uniqId = null;

    public static void clientInit() {
        LOGGER.info("Hello, world");
        ClientWorldTickEvents.START.register(Main::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(Main::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(Main::joinCallback);
    }

    private static StatusEffectInstance effect = null;

    private static void tickCallback(MinecraftClient client, ClientWorld world) {
        if (effect != null && client.player.hasStatusEffect(StatusEffects.field_5919)) {
            return;
        }
        StatusEffectInstance ef = new StatusEffectInstance(
            StatusEffects.field_5919,
            StatusEffectInstance.INFINITE_DURATION, 0,
            false, true, true
        );
        if (client.player.addStatusEffect(ef)) {
            effect = ef;
        }
        LOGGER.info("Applied blindness for \"" + StringEscapeUtils.escapeJava(Main.uniqId) + "\"");
    }

    private static void disconnectCallback(ClientPlayNetworkHandler handler, MinecraftClient client) {
        effect = null;
    }

    private static void joinCallback(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.isSingleplayer()) {
            return;
        }
        ServerInfo info = handler.getServerInfo();
        Main.uniqId = "m@" + info.address;
    }
}
