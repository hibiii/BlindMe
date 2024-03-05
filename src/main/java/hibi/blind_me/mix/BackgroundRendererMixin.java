package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.TrulyBlindFogEffect;
import net.minecraft.client.render.BackgroundRenderer;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void appendEffectRenderer(CallbackInfo info) {
        BackgroundRenderer.FOG_EFFECTS.add(new TrulyBlindFogEffect());
    }
}
