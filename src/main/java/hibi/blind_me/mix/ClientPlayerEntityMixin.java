package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import hibi.blind_me.Main;
import hibi.blind_me.Networking;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {

    @Inject(
        method = "setPermissionLevel",
        at = @At("HEAD")
    )
    void updateOpOverride(int level, CallbackInfo info) {
        if (!Networking.serverEnforced) {
            return;
        }
        Networking.isOpForBypass = Networking.opsBypass && level >= 2;
        Main.CONFIG.configureInstance();
    }
}
