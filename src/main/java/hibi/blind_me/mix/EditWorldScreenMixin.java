package hibi.blind_me.mix;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hibi.blind_me.Main;
import hibi.blind_me.config.ConfigFile;
import hibi.blind_me.config.ServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

@Mixin(EditWorldScreen.class)
public abstract class EditWorldScreenMixin extends Screen {

    protected EditWorldScreenMixin(Component component) {
        super(component);
    }

    @Shadow
    private @Final LevelStorageAccess levelAccess;
    
    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    void addBlindMeButton(CallbackInfo info) {
        var button = ServerScreen.getButton(openBtn -> {
            var uniqueId = "s@" + this.levelAccess.getLevelId();
            var screen = new ServerScreen(this, uniqueId, serverOptions -> {
                if (serverOptions == null) {
                    return;
                }
                Main.CONFIG.setServerOptions(uniqueId, serverOptions);
                ConfigFile.save(Main.CONFIG);
            });
            this.minecraft.setScreen(screen);
        }, ServerScreen.K_BLINDME_BUTTON_TOOLTIP_SINGLEPLAYER).build();
        this.addRenderableWidget(button);
    }
}
