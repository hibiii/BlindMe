package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(
        method = "removed",
        at = @At("HEAD")
    )
    void removedHook(CallbackInfo info) {
        // empty injection body
    }
}
