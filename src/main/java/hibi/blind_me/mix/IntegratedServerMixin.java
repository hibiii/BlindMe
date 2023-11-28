package hibi.blind_me.mix;

import hibi.blind_me.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.pack.ResourcePackManager;
import net.minecraft.server.Services;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.WorldStem;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.storage.WorldSaveStorage.Session;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    void extractWorldName(Thread _1, MinecraftClient _2, Session session, ResourcePackManager _3, WorldStem _4, Services _5, WorldGenerationProgressListenerFactory _6, CallbackInfo ci) {
        Main.uniqId = "s@" + session.getDirectoryName();
    }
}
