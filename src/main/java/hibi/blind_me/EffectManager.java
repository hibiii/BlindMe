package hibi.blind_me;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketSender;

import hibi.blind_me.ConfigEnums.ServerEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.payload.CustomPayload;

public final class EffectManager {

    public static @Nullable String uniqueId = null; // public readonly
    private static StatusEffectInstance effect = null;
    private static boolean skipCreative = false;
    private static boolean skipSpectator = true;
    private static StatusEffect desiredEffect = StatusEffects.field_5919;
    private static boolean effectChanged = false;

    private EffectManager() {};

    public static void tickCallback(MinecraftClient client, ClientWorld world) {
        ClientPlayerEntity player = client.player;
        if (effectChanged) {
            if (effect != null) {
                player.removeStatusEffect(effect.getEffectType());
                effect = null;
            }
            effectChanged = false;
        }
        if (desiredEffect == null) {
            return;
        }
        if (skipCreative && player.isCreative() || skipSpectator && player.isSpectator()) {
            if (effect != null && player.hasStatusEffect(desiredEffect)) {
                player.removeStatusEffect(desiredEffect);
                effect = null;
            }
            return;
        }
        if (effect != null && player.hasStatusEffect(desiredEffect)) {
            return;
        }
        StatusEffectInstance ef = new StatusEffectInstance(
            desiredEffect,
            StatusEffectInstance.INFINITE_DURATION, 0,
            true, false, false
        );
        if (player.addStatusEffect(ef)) {
            effect = ef;
        }
    }

    public static void disconnectCallback(ClientPlayNetworkHandler handler, MinecraftClient client) {
        effect = null;
    }

    public static void joinCallback(ClientPlayNetworkHandler handler, PacketSender<CustomPayload> sender, MinecraftClient client) {
        if (client.isSingleplayer()) {
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
        EffectManager.setDesiredEffect(ConfigManager.CONFIG.getEffectForServer(uniqueId));
    }

    public static void setDisabledCreative(boolean skipsCreative) {
        skipCreative = skipsCreative;
    }

    public static void setDisabledSpectator(boolean skipsSpectator) {
        skipSpectator = skipsSpectator;
    }

    public static void setDesiredEffect(ServerEffect serverEffect) {
        switch(serverEffect) {
            case BLINDNESS -> {
                desiredEffect = StatusEffects.field_5919;
            }
            case DARKNESS -> {
                desiredEffect = StatusEffects.field_38092;
            }
            case OFF -> {
                desiredEffect = null;
            }
        }
        effectChanged = true;
    }
}
