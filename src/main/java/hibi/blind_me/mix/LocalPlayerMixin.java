package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import hibi.blind_me.Main;
import hibi.blind_me.Networking;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(
        method = "handleEntityEvent",
        at = @At("HEAD")
    )
    void updateOpOverride(byte event, CallbackInfo info) {
        if (!Networking.serverEnforced) {
            return;
        }
        Networking.isOpForBypass = Networking.opsBypass && event >= 25 && event <= 28;
        Main.CONFIG.configureInstance();
    }
}
