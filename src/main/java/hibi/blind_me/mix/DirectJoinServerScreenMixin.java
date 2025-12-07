package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.Main;
import hibi.blind_me.config.ConfigFile;
import hibi.blind_me.config.ServerOptions;
import hibi.blind_me.config.ServerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;

@Mixin(DirectJoinServerScreen.class)
public abstract class DirectJoinServerScreenMixin extends Screen {

    protected DirectJoinServerScreenMixin(Component component) {
        super(component);
    }

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    void addBlindMeButton(CallbackInfo info) {
        OPTIONS = null;
        OPEN_BUTTON = ServerScreen.getButton(openBtn -> {
            var uniqueId = "m@" + this.ipEdit.getValue();
            var screen = new ServerScreen(this, uniqueId, serverOptions -> OPTIONS = serverOptions);
            this.minecraft.setScreen(screen);
        }, ServerScreen.K_BLINDME_BUTTON_TOOLTIP_MULTIPLAYER).build();
        this.addRenderableWidget(OPEN_BUTTON);
        this.updateSelectButtonStatus();
    }

    @Inject(
        method = "onClose",
        at = @At("TAIL")
    )
    void saveAndDiscard(CallbackInfo info) {
        if (OPTIONS != null) {
            Main.CONFIG.setServerOptions("m@" + this.ipEdit.getValue(), OPTIONS);
            ConfigFile.save(Main.CONFIG);
        }
        OPEN_BUTTON = null;
        OPTIONS = null;
    }

    @Inject(
        method = "updateSelectButtonStatus",
        at = @At("TAIL")
    )
    void alsoUpdateOpenButton(CallbackInfo info) {
        if (OPEN_BUTTON == null) {
            return;
        }
        OPEN_BUTTON.active = ServerAddress.isValidAddress(this.ipEdit.getValue());
    }

    @Shadow
    private void updateSelectButtonStatus() {}

    @Shadow
    private EditBox ipEdit;

    private static Button OPEN_BUTTON = null;
    private static ServerOptions OPTIONS = null;
}
