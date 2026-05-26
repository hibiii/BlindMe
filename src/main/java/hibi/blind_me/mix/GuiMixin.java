package hibi.blind_me.mix;

import java.util.List;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import hibi.blind_me.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(
        method = "buildInitialScreens",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Ljava/util/ArrayList;<init>()Ljava/util/ArrayList;"
        )
    )
    void addIrisWarningScreen(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> info, @Local List<Function<Runnable, Screen>> screens) {
        if(FabricLoader.getInstance().isModLoaded("iris") && !Main.CONFIG.hasSeenIrisWarning) {
            list.add(Main::produceIrisWarningScreen);
        }
    }
}
