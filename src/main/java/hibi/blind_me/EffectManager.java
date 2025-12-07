package hibi.blind_me;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import hibi.blind_me.mix.StatusEffectInstanceAccessor;

public final class EffectManager {

    private static MobEffectInstance effect = null;
    private static boolean skipCreative = false;
    private static boolean skipSpectator = true;
    private static Holder<MobEffect> desiredEffect = null;
    private static boolean effectChanged = false;

    private EffectManager() {};

    public static void tickCallback(Minecraft client) {
        LocalPlayer player = client.player;
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
            if (effect != null && player.hasEffect(desiredEffect)) {
                removeModEffect(player);
                effect = null;
            }
            return;
        }
        if (effect != null && player.hasEffect(desiredEffect)) {
            return;
        }
        MobEffectInstance ef = new MobEffectInstance(
            desiredEffect,
            MobEffectInstance.INFINITE_DURATION, 0,
            true, false, false,
            (MobEffectInstance) null
        );
        ef.skipBlending();
        if (player.addEffect(ef)) {
            effect = ef;
        }
    }

    public static void setDisabledCreative(boolean skipsCreative) {
        skipCreative = skipsCreative;
    }

    public static void setDisabledSpectator(boolean skipsSpectator) {
        skipSpectator = skipsSpectator;
    }

    public static void setDesiredEffect(ServerEffect serverEffect) {
        desiredEffect = serverEffect.getType();
        effectChanged = true;
    }

    public static Holder<MobEffect> getDesiredEffect() {
        return desiredEffect;
    }

    private static void removeModEffect(LocalPlayer player) {
        if (effect == null) {
            return;
        }
        Map<Holder<MobEffect>, MobEffectInstance> map = player.getActiveEffectsMap();
        Holder<MobEffect> type = effect.getEffect();
        MobEffectInstance ef = player.getEffect(type);
        if (ef == null) {
            return;
        }
        if (ef == effect) {
            MobEffectInstance shadowed = ((StatusEffectInstanceAccessor)ef).getHiddenEffect();
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
            MobEffectInstance shadowed = ((StatusEffectInstanceAccessor)ef).getHiddenEffect();
            if (shadowed == null) {
                effect = null;
                return;
            }
            if (shadowed != effect) {
                ef = shadowed;
                continue;
            }
            MobEffectInstance shadowed2 = ((StatusEffectInstanceAccessor)shadowed).getHiddenEffect();
            ((StatusEffectInstanceAccessor)ef).setHiddenEffect(shadowed2);
            effect = null;
            return;
        }
    }

    public static @Nullable MobEffectInstance getModEffect() {
        return effect;
    }
}
