package hibi.blind_me;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import hibi.blind_me.config.Config;
import hibi.blind_me.config.ConfigFile;
import hibi.blind_me.server.AcknowledgeForcePayload;
import hibi.blind_me.server.ForceSettingsPayload;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static Config CONFIG;

    public static void clientInit() throws JsonSyntaxException, IOException {
        CONFIG = ConfigFile.load();
        CONFIG.configureInstance();
        ClientTickEvents.START_CLIENT_TICK.register(EffectManager::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(Networking::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(Networking::joinCallback);
        PayloadTypeRegistry.configurationS2C().register(ForceSettingsPayload.ID, ForceSettingsPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeForcePayload.ID, AcknowledgeForcePayload.CODEC);
        ClientConfigurationNetworking.registerGlobalReceiver(ForceSettingsPayload.ID, Networking::joinBlindMeServerCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
    }
}
