package hibi.blind_me;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TouchTextRenderer {
    
    private MinecraftClient client;
    private @Nullable Text text;
    private int ticksDislaying;
    private int fadeoutTicks;

    public TouchTextRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void setText(@Nullable Text text) {
        if (text == null) {
            return;
        }
        this.text = text;
        this.ticksDislaying = 20;
        this.fadeoutTicks = 10;
    }

    public void render(GuiGraphics graphics, float tickDelta) {
        if (this.text == null) {
            return;
        }
        int scaledWidth = graphics.getScaledWindowWidth();
        int scaledHeight = graphics.getScaledWindowHeight();
        TextRenderer textRenderer = client.textRenderer;
        int textWidth = textRenderer.getWidth(this.text);
        int alpha = MathHelper.clamp((int)((this.fadeoutTicks - tickDelta) * 25.5f), 0, 255);
        if (alpha > 8) {
            alpha = alpha << 24 & 0xFF000000;
            graphics.drawText(textRenderer, this.text, (scaledWidth - textWidth) / 2, scaledHeight / 2 + 16, 0xFFFFFF | alpha, true);
        }
    }

    public void tick() {
        if (this.text != null) {
            if (this.ticksDislaying > 0) {
                this.ticksDislaying -= 1;
            } else if (this.fadeoutTicks > 0) {
                this.fadeoutTicks -= 1;
            } else {
                this.text = null;
            }
        }
    }
}
