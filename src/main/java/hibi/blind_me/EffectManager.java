package hibi.blind_me;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketSender;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class EffectManager {

    public static @Nullable String uniqueId = null; // public readonly
    private static StatusEffectInstance effect = null;
    private static boolean skipCreative = false;
    private static boolean skipSpectator = true;

    private EffectManager() {};

    public static void tickCallback(MinecraftClient client, ClientWorld world) {
        ClientPlayerEntity player = client.player;
        if (skipCreative && player.isCreative() || skipSpectator && player.isSpectator()) {
            if (effect != null && player.hasStatusEffect(StatusEffects.field_5919)) {
                player.removeStatusEffect(StatusEffects.field_5919);
                effect = null;
            }
            return;
        }
        if (effect != null && player.hasStatusEffect(StatusEffects.field_5919)) {
            return;
        }
        StatusEffectInstance ef = new StatusEffectInstance(
            StatusEffects.field_5919,
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

    public static void joinCallback(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.isSingleplayer()) {
            return;
        }
        ServerInfo info = handler.getServerInfo();
        uniqueId = "m@" + info.address;
    }

    public static void joinSingleplayerCallback(String worldName) {
        uniqueId = "s@" + worldName;
    }

    public static void setDisabledCreative(boolean skipsCreative) {
        skipCreative = skipsCreative;
    }

    public static void setDisabledSpectator(boolean skipsSpectator) {
        skipSpectator = skipsSpectator;
    }
}
