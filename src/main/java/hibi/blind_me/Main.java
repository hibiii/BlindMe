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
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static Config CONFIG;

    public static void clientInit() throws JsonSyntaxException, IOException {
        CONFIG = ConfigFile.load();
        CONFIG.configureInstance();
        ClientTickEvents.START_CLIENT_TICK.register(EffectManager::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(Networking::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(Networking::joinCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
        
    }

    public static Screen produceIrisWarningScreen(Runnable runnable) {
        return new AlertScreen(() -> {
            CONFIG.hasSeenIrisWarning = true;
            ConfigFile.save(CONFIG);
            runnable.run();
        },
        Component.translatable("blindme.shaders_warning.title"),
        Component.translatable("blindme.shaders_warning.messsage"), CommonComponents.GUI_ACKNOWLEDGE,
        false);
    }
}
