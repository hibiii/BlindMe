package hibi.blind_me.config;

import hibi.blind_me.EffectManager;
import hibi.blind_me.config.Enums.ServerEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.button.CyclingButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(K_TITLE));
    }

    protected void initOptionButtons() {
        var creativeBypassButton = CyclingButtonWidget.onOffBuilder(Manager.CONFIG.creativeBypass.getRealValue())
        .build(Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.creativeBypass.setValue(set, false);
            }
        );
        var spectatorBypassButton = CyclingButtonWidget.onOffBuilder(Manager.CONFIG.spectatorBypass.getRealValue())
        .build(Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.spectatorBypass.setValue(set, false);
            }
        );
        this.buttonList.addEntry(creativeBypassButton, spectatorBypassButton);

        var darknessPulseButton = CyclingButtonWidget
        .onOffBuilder(Manager.CONFIG.disableDarknessPulse.getRealValue())
        .tooltip(((bool) -> Tooltip.create(Text.translatable(K_DISABLE_PULSE_TOOLTIP))))
        .build(Text.translatable(K_DISABLE_PULSE),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.disableDarknessPulse.setValue(set, false);
            }
        );
        darknessPulseButton.setWidth(310);
        this.buttonList.addEntry(darknessPulseButton, null);

        boolean ingame = this.client.world != null;
        ServerEffect initial = ingame
            ? Manager.CONFIG.getEffectForServer(EffectManager.getUniqueId())
            : ServerEffect.OFF;
        var serverEffectButton = CyclingButtonWidget
            .builder((ServerEffect effect) -> switch(effect) {
                case OFF -> CommonTexts.OFF;
                case BLINDNESS -> Text.translatable("effect.minecraft.blindness");
                case DARKNESS -> Text.translatable("effect.minecraft.darkness");
            })
            .values(ServerEffect.values())
            .initially(initial)
            .tooltip(effect -> Tooltip.create(Text.translatable(K_SERVER_EFFECT_TOOLTIP + effect.toString())))
            .build(Text.translatable(K_CURRENT_SERVER), (button, value) -> {
                this.changed = true;
                Manager.CONFIG.setEffectForServer(EffectManager.getUniqueId(), value);
            });
        serverEffectButton.active = ingame;
        serverEffectButton.setWidth(310);
        this.buttonList.addEntry(serverEffectButton, null);
    }

    @Override
    public void closeScreen() {
        this.save();
        super.closeScreen();
    }

    public void render2(GuiGraphics graphics, int mX, int mY, float delta) {
        super.render(graphics, mX, mY, delta);
        graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    protected void save() {
        if (this.changed) {
            Manager.CONFIG.save();
        }
    }

    private static final String
        K_TITLE = "blindme.options.title",
        K_CREATIVE_BYPASS = "blindme.options.creative_bypass",
        K_SPECTATOR_BYPASS = "blindme.options.spectator_bypass",
        K_CURRENT_SERVER = "blindme.options.current_world_effect",
        K_SERVER_EFFECT_TOOLTIP = "blindme.options.current_world_effect.tooltip.",
        K_DISABLE_PULSE = "blindme.options.disable_darkness_pulse",
        K_DISABLE_PULSE_TOOLTIP = "blindme.options.disable_darkness_pulse.tooltip"
    ;
}
