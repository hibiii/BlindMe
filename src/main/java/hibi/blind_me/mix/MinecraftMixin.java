package hibi.blind_me.mix;

import java.util.List;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
        method = "addInitialScreens",
        at = @At("TAIL")
    )
    void addIrisWarningScreen(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> info) {
        if(FabricLoader.getInstance().isModLoaded("iris") && !Main.CONFIG.hasSeenIrisWarning) {
            list.add(Main::produceIrisWarningScreen);
        }
    }
}
