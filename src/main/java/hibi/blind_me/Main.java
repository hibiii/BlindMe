package hibi.blind_me;

import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibi.blind_me.config.Manager;
import net.minecraft.client.MinecraftClient;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static TouchTextRenderer touchTextRenderer;

    public static void clientInit() {
        Manager.init();
        touchTextRenderer = new TouchTextRenderer(MinecraftClient.getInstance());
        ClientWorldTickEvents.START.register(EffectManager::tickCallback);
        ClientWorldTickEvents.END.register(Touching::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(EffectManager::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(EffectManager::joinCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
    }
}
