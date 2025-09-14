package hibi.blind_me.mix;

import hibi.blind_me.Networking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage.Session;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    void extractWorldName(Thread _1, MinecraftClient _2, Session session, ResourcePackManager _3, SaveLoader _4, ApiServices _5, WorldGenerationProgressListenerFactory _6, CallbackInfo ci) {
        Networking.joinSingleplayerCallback(session.getDirectoryName());
    }
}
