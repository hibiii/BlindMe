package hibi.blind_me;

import hibi.blind_me.ConfigEnums.ServerEffect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.widget.button.CyclingButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;

    public ConfigScreen(Screen parent) {
        super(parent, null, Text.translatable(K_TITLE));
    }

    protected void init() {
        this.addDrawableSelectableElement(CyclingButtonWidget.onOffBuilder(ConfigManager.CONFIG.creativeBypass.getRealValue())
        .build(
            this.width / 2 - 155, this.height / 6,
            310, 20,
            Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                ConfigManager.CONFIG.creativeBypass.setValue(set, false);
            }
        ));
        this.addDrawableSelectableElement(CyclingButtonWidget.onOffBuilder(ConfigManager.CONFIG.spectatorBypass.getRealValue())
        .build(
            this.width / 2 - 155, this.height / 6 + 24,
            310, 20,
            Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                ConfigManager.CONFIG.spectatorBypass.setValue(set, false);
            }
        ));
        boolean ingame = this.client.world != null;
        ServerEffect initial = ingame
            ? ConfigManager.CONFIG.getEffectForServer(EffectManager.uniqueId)
            : ServerEffect.BLINDNESS;
        CyclingButtonWidget<ServerEffect> serverEffectButton = CyclingButtonWidget
            .builder((ServerEffect value) -> Text.translatable(K_CURRENT_SERVER, switch(value) {
                case OFF -> CommonTexts.OFF;
                case BLINDNESS -> Text.translatable("effect.minecraft.blindness");
                case DARKNESS -> Text.translatable("effect.minecraft.darkness");
            }))
            .values(ServerEffect.values())
            .initially(initial)
            .tooltip(effect -> Tooltip.create(Text.translatable(K_SERVER_EFFECT_TOOLTIP + effect.toString())))
            .omitKeyText()
            .build(
                this.width / 2 - 155, this.height / 6 + 48,
                310, 20,
                Text.literal(K_CURRENT_SERVER),
                (button, value) -> {
                    this.changed = true;
                    ConfigManager.CONFIG.setEffectForServer(EffectManager.uniqueId, value);
                }
            );
        serverEffectButton.active = ingame;
        this.addDrawableSelectableElement(serverEffectButton);

        this.addDrawableSelectableElement(ButtonWidget.builder(CommonTexts.DONE,
            button -> {
                if (this.changed) {
                    ConfigManager.CONFIG.save();
                }
                this.client.setScreen(this.parent);
            })
            .positionAndSize(this.width / 2 - 100, this.height / 6 + 72, 200, 20)
            .build()
        );
    }

    public void render(GuiGraphics graphics, int mX, int mY, float delta) {
        super.render(graphics, mX, mY, delta);
        graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    private static final String
        K_TITLE = "blindme.options.title",
        K_CREATIVE_BYPASS = "blindme.options.creative_bypass",
        K_SPECTATOR_BYPASS = "blindme.options.spectator_bypass",
        K_CURRENT_SERVER = "blindme.options.current_world_effect",
        K_SERVER_EFFECT_TOOLTIP = "blindme.options.current_world_effect.tooltip."
    ;
}
