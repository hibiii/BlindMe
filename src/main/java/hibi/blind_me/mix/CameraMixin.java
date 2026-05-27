package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.EffectManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;

@Mixin(Camera.class)
public class CameraMixin {
    
    @Inject(
        method = "extractRenderState",
        at = @At("TAIL")
    )
    public void blockSky(final CameraRenderState cameraState, final float cameraEntityPartialTicks, CallbackInfo info) {
        cameraState.entityRenderState.doesMobEffectBlockSky |= EffectManager.blockingSky();
    }
}
