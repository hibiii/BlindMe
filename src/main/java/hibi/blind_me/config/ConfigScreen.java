package hibi.blind_me.config;

import hibi.blind_me.EffectManager;
import hibi.blind_me.config.Enums.ServerEffect;
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
        super(parent, null, Text.translatable(K_TITLE));
    }

    protected void init() {
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Manager.CONFIG.creativeBypass.getRealValue())
        .build(
            this.width / 2 - 155, this.height / 6,
            310, 20,
            Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.creativeBypass.setValue(set, false);
            }
        ));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Manager.CONFIG.spectatorBypass.getRealValue())
        .build(
            this.width / 2 - 155, this.height / 6 + 24,
            310, 20,
            Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.spectatorBypass.setValue(set, false);
            }
        ));
        boolean ingame = this.client.world != null;
        ServerEffect initial = ingame
            ? Manager.CONFIG.getEffectForServer(EffectManager.getUniqueId())
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
                    Manager.CONFIG.setEffectForServer(EffectManager.getUniqueId(), value);
                }
            );
        serverEffectButton.active = ingame;
        this.addDrawableChild(serverEffectButton);

        this.addDrawableChild(ButtonWidget.builder(CommonTexts.DONE,
            button -> {
                if (this.changed) {
                    Manager.CONFIG.save();
                }
                this.client.setScreen(this.parent);
            })
            .positionAndSize(this.width / 2 - 100, this.height / 6 + 72, 200, 20)
            .build()
        );
    }

    public void render(GuiGraphics graphics, int mX, int mY, float delta) {
        this.renderBackground(graphics);
        graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mX, mY, delta);
    }

    private static final String
        K_TITLE = "blindme.options.title",
        K_CREATIVE_BYPASS = "blindme.options.creative_bypass",
        K_SPECTATOR_BYPASS = "blindme.options.spectator_bypass",
        K_CURRENT_SERVER = "blindme.options.current_world_effect",
        K_SERVER_EFFECT_TOOLTIP = "blindme.options.current_world_effect.tooltip."
    ;
}
