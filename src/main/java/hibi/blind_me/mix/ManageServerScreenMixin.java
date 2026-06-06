package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.Main;
import hibi.blind_me.config.ConfigFile;
import hibi.blind_me.config.ConfigScreenFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

@Mixin(ManageServerScreen.class)
public abstract class ManageServerScreenMixin extends ScreenMixin {

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    void addBlindMeButton(CallbackInfo info) {
        ManageServerScreen that = (ManageServerScreen) (Object) this;
        OPEN_BUTTON = ConfigScreenFactory.getButton(openBtn -> {
            var uniqueId = "m@" + this.ipEdit.getValue();
            var screen = ConfigScreenFactory.create(that, false, uniqueId, () -> CHANGED = true);
            that.minecraft.gui.setScreen(screen);
        }, ConfigScreenFactory.K_BLINDME_BUTTON_TOOLTIP_MULTIPLAYER).build();
        that.addRenderableWidget(OPEN_BUTTON);
        this.updateAddButtonStatus();
    }

    @Override
    void removedHook(CallbackInfo info) {
        if (CHANGED) {
            Main.CONFIG.setServerOptions("m@" + this.ipEdit.getValue(), ConfigScreenFactory.getLastServerOptions());
            ConfigFile.save(Main.CONFIG);
        }
        OPEN_BUTTON = null;
        CHANGED = false;
    }

    @Inject(
        method = "updateAddButtonStatus",
        at = @At("TAIL")
    )
    void alsoUpdateOpenButton(CallbackInfo info) {
        if (OPEN_BUTTON == null) {
            return;
        }
        OPEN_BUTTON.active = ServerAddress.isValidAddress(this.ipEdit.getValue());
    }

    @Shadow
    private void updateAddButtonStatus() {}

    @Shadow
    private EditBox ipEdit;

    private static Button OPEN_BUTTON = null;
    private static boolean CHANGED = false;
}
