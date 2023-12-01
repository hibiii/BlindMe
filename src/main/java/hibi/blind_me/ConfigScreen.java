package hibi.blind_me;

import hibi.blind_me.ConfigEnums.ServerEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;

    public ConfigScreen(Screen parent) {
        super(parent, null, Text.literal("BlindMe Config"));
    }

    protected void init() {
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigManager.CONFIG.creativeBypass.getRealValue())
        .build(
            this.width / 2 - 155, this.height / 6,
            150, 20,
            Text.literal("Creative Bypass"),
            (button, set) -> {
                this.changed = true;
                ConfigManager.CONFIG.creativeBypass.setValue(set, false);
            }
        ));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigManager.CONFIG.spectatorBypass.getRealValue())
        .build(
            this.width / 2 + 5, this.height / 6,
            150, 20,
            Text.literal("Spectator Bypass"),
            (button, set) -> {
                this.changed = true;
                ConfigManager.CONFIG.spectatorBypass.setValue(set, false);
            }
        ));
        CyclingButtonWidget<ServerEffect> serverEffectButton = CyclingButtonWidget
            .builder((ServerEffect value) -> switch(value) {
                case OFF -> Text.literal("OFF");
                case BLINDNESS -> Text.literal("Blindness");
                case DARKNESS -> Text.literal("Darkness");
            })
            .values(ServerEffect.values())
            .initially(ServerEffect.BLINDNESS)
            .tooltip(effect -> Tooltip.create(Text.literal(effect.toString())))
            .build(
                this.width / 2 - 155, this.height / 6 + 24,
                310, 20,
                Text.literal("Server Effect"),
                (button, set) -> {
                    this.changed = true;
                    Main.LOGGER.error("TODO: implement functionality on the server effect button");
                }
            );
        serverEffectButton.active = true;
        this.addDrawableChild(serverEffectButton);

        this.addDrawableChild(ButtonWidget.builder(CommonTexts.DONE,
            button -> {
                if (this.changed) {
                    ConfigManager.CONFIG.save();
                }
                this.client.setScreen(this.parent);
            })
            .positionAndSize(this.width / 2 - 100, this.height / 6 + 48, 200, 20)
            .build()
        );
    }

    public void render(GuiGraphics graphics, int mX, int mY, float delta) {
        this.renderBackground(graphics);
        graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mX, mY, delta);
    }
}
