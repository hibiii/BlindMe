package hibi.blind_me;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;

public final class EffectManager {

    private static final BlindmeFogEnvironment effect = BlindmeFogEnvironment.getInstance();
    private static boolean skipCreative = false;
    private static boolean skipSpectator = true;
    private static @Nullable ServerEffect desiredEffect = null;
    private static boolean effectEnabled = false;
    private static boolean skip = false;

    private EffectManager() {};

    public static void tickCallback(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        if (desiredEffect != null) {
            effectEnabled = true;
            switch (desiredEffect) {
                case OFF:
                    effectEnabled = false;
                    break;
                case BLINDNESS:
                    effect.setProperties(1.25f, 5f, 0xFF000000);
                    break;
                case DARKNESS:
                    effect.setProperties(11.25f, 15f, 0xFF003300);
                    break;
            }
            desiredEffect = null;
        }
        skip = skipCreative && player.isCreative() || skipSpectator && player.isSpectator();
        effect.setEnabled(effectEnabled && !skip);
    }

    public static void setDisabledCreative(boolean skipsCreative) {
        skipCreative = skipsCreative;
    }

    public static void setDisabledSpectator(boolean skipsSpectator) {
        skipSpectator = skipsSpectator;
    }

    public static void setDesiredEffect(ServerEffect serverEffect) {
        desiredEffect = serverEffect;
    }

    public static boolean blockingSky() {
        return effectEnabled && !skip;
    }
}
