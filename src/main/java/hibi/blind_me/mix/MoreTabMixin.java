package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import hibi.blind_me.MixinStorage;
import hibi.blind_me.config.ServerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen.MoreTab;

@Mixin(MoreTab.class)
public class MoreTabMixin {
    
    @Inject(
        method = "<init>",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    void addBlindMeButton(CreateWorldScreen parent, CallbackInfo info, GridLayout.RowHelper rowHelper) {
        MixinStorage.OPTIONS = null;
        rowHelper.addChild(ServerScreen.getButton(button -> {
                Minecraft.getInstance().setScreen(new ServerScreen(parent, "temp@", options -> MixinStorage.OPTIONS = options));
            }, ServerScreen.K_BLINDME_BUTTON_TOOLTIP_SINGLEPLAYER
        ).width(210).build());
    }
}
