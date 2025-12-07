package hibi.blind_me.mix;

import hibi.blind_me.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    void extractWorldName(Thread _1, Minecraft _2, LevelStorageAccess session, PackRepository _3, WorldStem _4, Services _5, LevelLoadListener _6, CallbackInfo ci) {
        Networking.joinSingleplayerCallback(session.getLevelId());
    }
}
