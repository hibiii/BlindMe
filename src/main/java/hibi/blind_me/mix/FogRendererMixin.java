package hibi.blind_me.mix;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.BlindmeFogEnvironment;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    private static @Final List<FogEnvironment> FOG_ENVIRONMENTS;

    @Inject(
        method = "<clinit>",
        at = @At("TAIL")
    )
    private static void addTheFog(CallbackInfo info) {
        for (FogEnvironment fog : FOG_ENVIRONMENTS) {
            if (fog instanceof BlindnessFogEnvironment) {
                int index = FOG_ENVIRONMENTS.indexOf(fog);
                FOG_ENVIRONMENTS.add(index, BlindmeFogEnvironment.getInstance());
                return;
            }
        }
    }
}
