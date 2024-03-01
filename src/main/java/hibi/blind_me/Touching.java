package hibi.blind_me;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public final class Touching {
    Touching() {}

    public static final KeyBind TOUCH_KEY;

    public static void tickCallback(MinecraftClient client, ClientWorld world) {
        if (client.crosshairTarget == null) {
            return;
        }
        Main.touchTextRenderer.tick();
        if (TOUCH_KEY.wasPressed()) {
            client.player.swingHand(Hand.field_5808);
            Text text = switch(client.crosshairTarget.getType()) {
                case field_1333 -> null; // miss
                case field_1332 -> world.getBlockState(((BlockHitResult)client.crosshairTarget).getBlockPos()).getBlock().getName(); // block
                case field_1331 -> ((EntityHitResult)client.crosshairTarget).getEntity().getName(); // entity
            };
            Main.touchTextRenderer.setText(text);
        }
    }

    static {
        TOUCH_KEY = new KeyBind("key.blindme.touch", InputUtil.Type.field_1672, GLFW.GLFW_MOUSE_BUTTON_4, KeyBind.GAMEPLAY_CATEGORY);
    }
}
