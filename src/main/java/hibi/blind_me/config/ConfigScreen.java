package hibi.blind_me.config;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(K_TITLE));
    }

    @Override
    protected void addOptions() {
        var creativeBypassButton = CyclingButtonWidget.onOffBuilder(Main.CONFIG.creativeBypass)
        .build(Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.creativeBypass = set;
            }
        );
        var spectatorBypassButton = CyclingButtonWidget.onOffBuilder(Main.CONFIG.spectatorBypass)
        .build(Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.spectatorBypass = set;
            }
        );
        this.body.addWidgetEntry(creativeBypassButton, spectatorBypassButton);

        var darknessPulseButton = CyclingButtonWidget
        .onOffBuilder(Main.CONFIG.disableDarknessPulse)
        .tooltip(((bool) -> Tooltip.of(Text.translatable(K_DISABLE_PULSE_TOOLTIP))))
        .build(Text.translatable(K_DISABLE_PULSE),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.disableDarknessPulse = set;
            }
        );
        darknessPulseButton.setWidth(310);
        this.body.addWidgetEntry(darknessPulseButton, null);
        this.addDefaultEffectButton();
        this.addCurrentServerEffectButton();
    }

    public void addDefaultEffectButton() {
        var button = CyclingButtonWidget
            .builder((ServerEffect ef) -> {
                return switch(ef) {
                    case BLINDNESS -> Text.translatable("effect.minecraft.blindness");
                    case DARKNESS -> Text.translatable("effect.minecraft.darkness");
                    case OFF -> ScreenTexts.OFF;
                };
            })
            .values(ServerEffect.values())
            .initially(Main.CONFIG.defaultServerEffect)
            .tooltip(effect -> Tooltip.of(Text.translatable(K_EFFECT_DETAILS_TOOLTIP + effect.toString())))
            .build(Text.translatable(K_DEFAULT_EFFECT), (_button, value) -> {
                this.changed = true;
                Main.CONFIG.defaultServerEffect = value;
            });
        button.setWidth(310);
        this.body.addWidgetEntry(button, null);
    }

    public void addCurrentServerEffectButton() {
        boolean ingame = this.client.world != null;
        var button = CyclingButtonWidget
            .builder((Optional<ServerEffect> ef) -> {
                if (ef.isEmpty()) {
                    return Text.translatable("effect.blindme.default");
                }
                return switch(ef.get()) {
                    case BLINDNESS -> Text.translatable("effect.minecraft.blindness");
                    case DARKNESS -> Text.translatable("effect.minecraft.darkness");
                    case OFF -> ScreenTexts.OFF;
                };
            })
            .values(List.of(
                Optional.empty(),
                Optional.of(ServerEffect.BLINDNESS),
                Optional.of(ServerEffect.DARKNESS),
                Optional.of(ServerEffect.OFF)
            ))
            .initially(Optional.ofNullable(Main.CONFIG.servers.get(EffectManager.getUniqueId())))
            .tooltip(optional -> {
                var effect = optional.orElse(null);
                return Tooltip.of(Text.translatable(K_EFFECT_DETAILS_TOOLTIP
                    + ((effect != null) ? effect.toString() : "default")
                ));
            })
            .build(Text.translatable(K_CURRENT_SERVER), (_button, value) -> {
                this.changed = true;
                Main.CONFIG.setEffectForServer(EffectManager.getUniqueId(), value.orElse(null));
            });
        button.active = ingame;
        button.setWidth(310);
        this.body.addWidgetEntry(button, null);
    }

    @Override
    public void close() {
        this.save();
        super.close();
    }

    protected void save() {
        if (this.changed) {
            Main.CONFIG.configureInstance();
            ConfigFile.save(Main.CONFIG);
        }
    }

    private static final String
        K_TITLE = "blindme.options.title",
        K_CREATIVE_BYPASS = "blindme.options.creative_bypass",
        K_SPECTATOR_BYPASS = "blindme.options.spectator_bypass",
        K_CURRENT_SERVER = "blindme.options.current_world_effect",
        K_DEFAULT_EFFECT = "blindme.options.default_effect",
        K_EFFECT_DETAILS_TOOLTIP = "blindme.options.world_effect.tooltip.",
        K_DISABLE_PULSE = "blindme.options.disable_darkness_pulse",
        K_DISABLE_PULSE_TOOLTIP = "blindme.options.disable_darkness_pulse.tooltip"
    ;
}
