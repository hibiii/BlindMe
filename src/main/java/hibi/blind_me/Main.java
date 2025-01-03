package hibi.blind_me;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibi.blind_me.config.Manager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");

    public static void clientInit() {
        Manager.init();
        ClientTickEvents.START_CLIENT_TICK.register(EffectManager::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(EffectManager::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(EffectManager::joinCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
    }
}
