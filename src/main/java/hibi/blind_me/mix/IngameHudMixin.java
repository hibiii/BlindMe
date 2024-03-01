package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.Main;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public class IngameHudMixin {
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
        )
    )
    void renderTouchText(GuiGraphics graphics, float tickDelta, CallbackInfo info) {
        Main.touchTextRenderer.render(graphics, tickDelta);
    }
}
