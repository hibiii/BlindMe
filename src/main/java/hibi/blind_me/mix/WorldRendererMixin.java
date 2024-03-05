package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(
        method = "hasSkyBlockingEffect",
        at = @At("HEAD"),
        cancellable = true
    )
    void trulyBlindBlocksSky(Camera camera, CallbackInfoReturnable<Boolean> info) {
        StatusEffectInstance ef = EffectManager.getModEffect();
        if (ef != null && ef.getEffectType() == Main.TRULY_BLIND) {
            info.setReturnValue(true);
        }
    }
}
