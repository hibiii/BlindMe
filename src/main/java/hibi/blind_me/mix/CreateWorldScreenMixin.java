package hibi.blind_me.mix;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import hibi.blind_me.Main;
import hibi.blind_me.MixinStorage;
import hibi.blind_me.config.ConfigFile;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    
    @Inject(
        method = "createNewWorld",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createNewWorldDirectory(Lnet/minecraft/client/Minecraft;Ljava/lang/String;Ljava/nio/file/Path;)Ljava/util/Optional;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    void saveSettings(LayeredRegistryAccess<?> _lra, WorldData _wd, CallbackInfoReturnable<Boolean> info, String _s, WorldCreationContext _wcc, Optional<LevelStorageAccess> optional) {
        if (optional.isEmpty()) {
            return;
        }
        var access = optional.get();
        if (MixinStorage.OPTIONS != null) {
            Main.CONFIG.setServerOptions("s@" + access.getLevelId(), MixinStorage.OPTIONS);
            ConfigFile.save(Main.CONFIG);
            MixinStorage.OPTIONS = null;
        }
    }
}
