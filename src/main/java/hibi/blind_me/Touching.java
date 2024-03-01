package hibi.blind_me;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public final class Touching {
    Touching() {}

    public static void tickCallback(MinecraftClient client, ClientWorld world) {
        if (client.crosshairTarget == null) {
            return;
        }
        // TODO: change for keybind-based trigger
        Text text = switch(client.crosshairTarget.getType()) {
            case field_1333 -> null; // miss
            case field_1332 -> world.getBlockState(((BlockHitResult)client.crosshairTarget).getBlockPos()).getBlock().getName(); // block
            case field_1331 -> ((EntityHitResult)client.crosshairTarget).getEntity().getName(); // entity
        };
        Main.touchTextRenderer.tick();
        Main.touchTextRenderer.setText(text);
    }
}
