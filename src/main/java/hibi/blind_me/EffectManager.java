package hibi.blind_me;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.Manager;
import hibi.blind_me.config.Enums.ServerEffect;
import hibi.blind_me.mix.StatusEffectInstanceAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

public final class EffectManager {

    private static String uniqueId = null;
    private static StatusEffectInstance effect = null;
    private static boolean skipCreative = false;
    private static boolean skipSpectator = true;
    private static RegistryEntry<StatusEffect> desiredEffect = null;
    private static boolean effectChanged = false;

    private EffectManager() {};

    public static void tickCallback(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        if (effectChanged) {
            removeModEffect(player);
            effectChanged = false;
            effect = null;
        }
        if (desiredEffect == null) {
            return;
        }
        if (skipCreative && player.isCreative() || skipSpectator && player.isSpectator()) {
            if (effect != null && player.hasStatusEffect(desiredEffect)) {
                removeModEffect(player);
                effect = null;
            }
            return;
        }
        if (effect != null && player.hasStatusEffect(desiredEffect)) {
            return;
        }
        StatusEffectInstance ef = new StatusEffectInstance(
            desiredEffect,
            StatusEffectInstance.INFINITE, 0,
            true, false, false,
            (StatusEffectInstance) null
        );
        ef.skipFading();
        if (player.addStatusEffect(ef)) {
            effect = ef;
        }
    }

    public static void disconnectCallback(ClientPlayNetworkHandler handler, MinecraftClient client) {
        effect = null;
    }

    public static void joinCallback(ClientPlayNetworkHandler handler, Object packetSender, MinecraftClient client) {
        if (client.isInSingleplayer()) {
            return;
        }
        ServerInfo info = handler.getServerInfo();
        uniqueId = "m@" + info.address;
        getEffectForServer();
    }
    
    public static void joinSingleplayerCallback(String worldName) {
        uniqueId = "s@" + worldName;
        getEffectForServer();
    }

    private static void getEffectForServer() {
        EffectManager.setDesiredEffect(Manager.CONFIG.getEffectForServer(uniqueId));
    }

    public static void setDisabledCreative(boolean skipsCreative) {
        skipCreative = skipsCreative;
    }

    public static void setDisabledSpectator(boolean skipsSpectator) {
        skipSpectator = skipsSpectator;
    }

    public static void setDesiredEffect(ServerEffect serverEffect) {
        desiredEffect = switch(serverEffect) {
            case BLINDNESS -> StatusEffects.BLINDNESS;
            case DARKNESS -> StatusEffects.DARKNESS;
            case OFF ->  null;
        };
        effectChanged = true;
    }

    public static RegistryEntry<StatusEffect> getDesiredEffect() {
        return desiredEffect;
    }

    public static @Nullable String getUniqueId() {
        return uniqueId;
    }

    private static void removeModEffect(ClientPlayerEntity player) {
        if (effect == null) {
            return;
        }
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> map = player.getActiveStatusEffects();
        RegistryEntry<StatusEffect> type = effect.getEffectType();
        StatusEffectInstance ef = player.getStatusEffect(type);
        if (ef == null) {
            return;
        }
        if (ef == effect) {
            StatusEffectInstance shadowed = ((StatusEffectInstanceAccessor)ef).getHiddenEffect();
            if (shadowed != null) {
                ef = shadowed;
                map.put(type, shadowed);
            } else {
                map.remove(type);
            }
            return;
        }
        ef = ((StatusEffectInstanceAccessor)ef).getHiddenEffect();
        while (ef != null) {
            StatusEffectInstance shadowed = ((StatusEffectInstanceAccessor)ef).getHiddenEffect();
            if (shadowed == null) {
                effect = null;
                return;
            }
            if (shadowed != effect) {
                ef = shadowed;
                continue;
            }
            StatusEffectInstance shadowed2 = ((StatusEffectInstanceAccessor)shadowed).getHiddenEffect();
            ((StatusEffectInstanceAccessor)ef).setHiddenEffect(shadowed2);
            effect = null;
            return;
        }
    }

    public static @Nullable StatusEffectInstance getModEffect() {
        return effect;
    }
}
