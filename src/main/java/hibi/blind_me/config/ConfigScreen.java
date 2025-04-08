package hibi.blind_me.config;

import java.util.List;
import java.util.Optional;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;
    private ServerOptions serverOptions;
    private boolean ingame = false;
    private ButtonWidget lockButton = null;

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(K_TITLE));
        this.serverOptions = Main.CONFIG.getServerOptions(EffectManager.getUniqueId());
    }

    @Override
    protected void addOptions() {
        this.ingame = this.client.world != null;
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
        var worldSettings = new TextWidget(310, 27, Text.translatable(K_WORLD_SETTINGS_SUBTITLE), this.textRenderer)
            .alignCenter();
        this.body.addWidgetEntry(worldSettings, null);
        this.addButtonsForCurrentServer();
    }

    private void addDefaultEffectButton() {
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
                EffectManager.setDesiredEffect(Main.CONFIG.getEffectForServer(EffectManager.getUniqueId()));
            });
        button.setWidth(310);
        this.body.addWidgetEntry(button, null);
    }

    // TODO: Organize this MESS
    private void addButtonsForCurrentServer() {
        var initiallyLocked = this.serverOptions.locked();
        var effectButton = CyclingButtonWidget
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
            .initially(Optional.ofNullable(
                this.serverOptions.effect()
            ))
            .tooltip(optional -> {
                var effect = optional.orElse(null);
                return Tooltip.of(Text.translatable(K_EFFECT_DETAILS_TOOLTIP
                    + ((effect != null) ? effect.toString() : "default")
                ));
            })
            .build(Text.translatable(K_CURRENT_SERVER), (_button, value) -> {
                this.changed = true;
                this.serverOptions = this.serverOptions.withEffect(value.orElse(null));
                EffectManager.setDesiredEffect(value.orElse(Main.CONFIG.defaultServerEffect));
            });
        effectButton.active = ingame && !initiallyLocked;
        effectButton.setWidth(310);
        this.body.addWidgetEntry(effectButton, null);

        var lockButton = ButtonWidget.builder(Text.translatable(
            initiallyLocked ? K_UNLOCK_BUTTON : K_LOCK_BUTTON
        ), (btn) -> {
            if (!this.serverOptions.locked()) {
                var scr = new ConfirmScreen(
                    lock -> {
                        if (lock) {
                            this.serverOptions = this.serverOptions.butLocked();
                            this.changed = true;
                            effectButton.active = false;
                            btn.setMessage(Text.translatable(K_UNLOCK_BUTTON));
                            btn.setTooltip(Tooltip.of(Text.translatable(K_UNLOCK_BUTTON_TOOLTIP)));
                            btn.active = false;
                        }
                        this.client.setScreen(this);
                    },
                    Text.translatable(K_LOCK_SCREEN_TITLE),
                    Text.translatable(K_LOCK_SCREEN_MESSAGE)
                );
                scr.disableButtons(20);
                this.client.setScreen(scr);
            } else {
                this.serverOptions = this.serverOptions.butUnlocked();
                this.changed = true;
                effectButton.active = true;
                btn.setMessage(Text.translatable(K_LOCK_BUTTON));
                btn.setTooltip(null);
                btn.active = true;
            }
        }).build();
        if (initiallyLocked) {
            lockButton.setTooltip(Tooltip.of(Text.translatable(K_UNLOCK_BUTTON_TOOLTIP)));
        }
        lockButton.active = ingame && !this.serverOptions.locked();
        this.body.addWidgetEntry(lockButton, null);
        this.lockButton = lockButton;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lockButton != null) {
            this.lockButton.active = ingame && (
                !this.serverOptions.locked()
                || (this.serverOptions.locked() && Screen.hasShiftDown())
            );
        }
    }

    @Override
    public void close() {
        this.save();
        Main.CONFIG.configureInstance();
        super.close();
    }

    protected void save() {
        if (this.changed) {
            Main.CONFIG.setServerOptions(EffectManager.getUniqueId(), this.serverOptions);
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
        K_DISABLE_PULSE_TOOLTIP = "blindme.options.disable_darkness_pulse.tooltip",
        K_LOCK_BUTTON = "blindme.options.lock_world",
        K_LOCK_SCREEN_TITLE = "blindme.options.lock_world.screen.title",
        K_LOCK_SCREEN_MESSAGE = "blindme.options.lock_world.screen.message",
        K_UNLOCK_BUTTON = "blindme.options.unlock_world",
        K_UNLOCK_BUTTON_TOOLTIP = "blindme.options.unlock_world.tooltip",
        K_WORLD_SETTINGS_SUBTITLE = "blindme.options.subtitle.world_specific"
    ;
}
