package hibi.blind_me;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import hibi.blind_me.config.Config;
import hibi.blind_me.config.ConfigFile;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static Config CONFIG;

    public static void clientInit() throws JsonSyntaxException, IOException {
        CONFIG = ConfigFile.load();
        CONFIG.configureInstance();
        ClientTickEvents.START_CLIENT_TICK.register(EffectManager::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(EffectManager::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(EffectManager::joinCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
    }
}
