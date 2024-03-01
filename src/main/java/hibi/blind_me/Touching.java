package hibi.blind_me;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

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
            HitResult hit = client.crosshairTarget;
            // TODO: upgrade to instanceof switch when JAVA 21 hits prod
            Text text = null, subtitle = null;
            double distance = client.cameraEntity.getCameraPosVec(0).distanceTo(hit.getPos());
            System.err.println(hit.getType());
            switch(hit.getType()) {
                // Miss
                case field_1333 -> { return; }
                // Block
                case field_1332 -> {
                    text = world.getBlockState(((BlockHitResult)hit).getBlockPos()).getBlock().getName();
                }
                case field_1331 -> {
                    Entity entity = ((EntityHitResult)hit).getEntity();
                    text = entity.getType().getName();
                    if (entity.hasCustomName()) {
                        subtitle = entity.getCustomName();
                    }
                    break;
                }
                default -> { throw new IllegalStateException("HitResult is not MISS, BLOCK, or ENTITY"); }
            }
            Main.touchTextRenderer.setText(text,  subtitle, distance);
        }
    }

    static {
        TOUCH_KEY = new KeyBind("key.blindme.touch", InputUtil.Type.field_1672, GLFW.GLFW_MOUSE_BUTTON_4, KeyBind.GAMEPLAY_CATEGORY);
    }
}
