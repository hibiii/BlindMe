package hibi.blind_me;

import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientWorldTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hibi.blind_me.config.Manager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlindMe");
    public static TouchTextRenderer touchTextRenderer;

    // Implementation notice: while BlindMe is a client-only mod, it may not be
    // a good idea to register a status effect on the client init phase, because
    // the server registry may be frozen by then.
    public static final StatusEffect TRULY_BLIND = new TrulyBlindEffect();

    public static void clientInit() {
        Manager.init();
        touchTextRenderer = new TouchTextRenderer(MinecraftClient.getInstance());
        ClientWorldTickEvents.START.register(EffectManager::tickCallback);
        ClientWorldTickEvents.END.register(Touching::tickCallback);
        ClientPlayConnectionEvents.DISCONNECT.register(EffectManager::disconnectCallback);
        ClientPlayConnectionEvents.JOIN.register(EffectManager::joinCallback);
        ClientCommandRegistrationCallback.EVENT.register(Command::registerCallback);
        KeyBindingHelper.registerKeyBinding(Touching.TOUCH_KEY);
    }

    // Since BlindMe is designed to be client-only, it's okay to throw linkage errors on dedi server
    public static void serverInit() {
        // FIXME: Vanilla players cannot join LAN worlds hosted by BlindMe clients
        Registry.register(Registries.STATUS_EFFECT, new Identifier("blindme", "truly_blind"), TRULY_BLIND);
    }

    static class TrulyBlindEffect extends StatusEffect {
        public TrulyBlindEffect() {
            super(StatusEffectType.field_18272, 0x000000);
        }
    }
}
