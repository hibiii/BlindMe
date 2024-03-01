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
    private @Nullable Text subtitle;
    private Text distanceText;
    private int ticksDislaying;
    private int fadeoutTicks;

    public TouchTextRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void setText(@Nullable Text text, @Nullable Text subtitle, double distance) {
        if (text == null) {
            return;
        }
        this.text = text;
        this.subtitle = subtitle;
        this.distanceText = Text.translatable("blindme.touch.distance", String.format("%.1f", distance));
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
        int alpha = MathHelper.clamp((int)((this.fadeoutTicks - tickDelta) * 25.5f), 0, 255);
        if (alpha > 8) {
            alpha = alpha << 24 & 0xFF000000;
            int textWidth = textRenderer.getWidth(this.text);
            graphics.drawText(textRenderer, this.text, (scaledWidth - textWidth) / 2, scaledHeight / 2 + 16, 0xFFFFFF | alpha, true);
            textWidth = textRenderer.getWidth(this.distanceText);
            graphics.drawText(textRenderer, this.distanceText, (scaledWidth - textWidth) / 2, scaledHeight / 2 + 25, 0xAAAAAA | alpha, true);
            if (this.subtitle != null) {
                textWidth = textRenderer.getWidth(this.subtitle);
                graphics.drawText(textRenderer, this.subtitle, (scaledWidth - textWidth) / 2, scaledHeight / 2 + 34, 0xAAAAAA | alpha, true);
            }
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
                this.subtitle = null;
            }
        }
    }
}
