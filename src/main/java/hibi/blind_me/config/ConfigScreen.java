package hibi.blind_me.config;

import java.util.Optional;

import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import hibi.blind_me.Networking;
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
    private CyclingButtonWidget<Optional<ServerEffect>> effectButton = null;
    private CyclingButtonWidget<Optional<Boolean>> worldCreativeBypassButton = null;
    private CyclingButtonWidget<Optional<Boolean>> worldSpectatorBypassButton = null;
    private ButtonWidget lockButton = null;

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(K_TITLE));
        this.serverOptions = Main.CONFIG.getServerOptions(Networking.uniqueId);
    }

    @Override
    protected void addOptions() {
        this.ingame = this.client.world != null;
        var creativeBypassButton = CyclingButtonWidget.onOffBuilder(Main.CONFIG.creativeBypass)
        .build(Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.creativeBypass = set;
                EffectManager.setDisabledCreative(this.serverOptions.creativeBypass() instanceof Boolean b? b : set);
            }
        );
        var spectatorBypassButton = CyclingButtonWidget.onOffBuilder(Main.CONFIG.spectatorBypass)
        .build(Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.spectatorBypass = set;
                EffectManager.setDisabledSpectator(this.serverOptions.spectatorBypass() instanceof Boolean b? b : set);
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
                EffectManager.setDesiredEffect(Main.CONFIG.getEffectForServer(Networking.uniqueId));
            });
        button.setWidth(310);
        this.body.addWidgetEntry(button, null);
    }

    private void addButtonsForCurrentServer() {
        var worldSettings = new TextWidget(310, 27, Text.translatable(K_WORLD_SETTINGS_SUBTITLE), this.textRenderer)
            .alignCenter();
        this.body.addWidgetEntry(worldSettings, null);

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
            .values(Optional.empty(), Optional.of(ServerEffect.BLINDNESS), Optional.of(ServerEffect.DARKNESS), Optional.of(ServerEffect.OFF)
            )
            .initially(Optional.ofNullable(
                this.serverOptions.effect()
            ))
            .tooltip(optional -> {
                var effect = optional.orElse(null);
                Text text;
                if (Networking.serverEnforced) {
                    text = Text.translatable(K_SERVER_ENFORCED_TOOLTIP);
                } else {
                    text = Text.translatable(K_EFFECT_DETAILS_TOOLTIP
                        + ((effect != null) ? effect.toString() : "default")
                    );
                }
                return Tooltip.of(text);
            })
            .build(Text.translatable(K_CURRENT_SERVER), (_button, value) -> {
                this.changed = true;
                this.serverOptions = this.serverOptions.withEffect(value.orElse(null));
                EffectManager.setDesiredEffect(value.orElse(Main.CONFIG.defaultServerEffect));
            });
        effectButton.setWidth(310);
        this.body.addWidgetEntry(effectButton, null);
        this.effectButton = effectButton;
        
        var creativeBypassButton = CyclingButtonWidget
        .builder((Optional<Boolean> bypass) -> {
            if (bypass.isEmpty()) {
                return Text.translatable("effect.blindme.default");
            }
            return (bypass.get())? ScreenTexts.ON : ScreenTexts.OFF;
        })
        .values(Optional.empty(), Optional.of(true), Optional.of(false))
        .initially(Optional.ofNullable(this.serverOptions.creativeBypass()))
        .tooltip(optional -> Tooltip.of(Text.translatable(
            Networking.serverEnforced?
            K_SERVER_ENFORCED_TOOLTIP : K_WORLD_SPECIFIC_OPTION
        )))
        .build(Text.translatable(K_WORLD_CREATIVE_BYPASS), (btn, value) -> {
            this.changed = true;
            this.serverOptions = this.serverOptions.withCreativeBypass(value.orElse(null));
            EffectManager.setDisabledCreative(value.orElse(Main.CONFIG.creativeBypass));
        });
        this.worldCreativeBypassButton = creativeBypassButton;
        
        var spectatorBypassButton = CyclingButtonWidget
        .builder((Optional<Boolean> bypass) -> {
            if (bypass.isEmpty()) {
                return Text.translatable("effect.blindme.default");
            }
            return (bypass.get())? ScreenTexts.ON : ScreenTexts.OFF;
        })
        .values(Optional.empty(), Optional.of(true), Optional.of(false))
        .initially(Optional.ofNullable(this.serverOptions.spectatorBypass()))
        .tooltip(optional -> Tooltip.of(Text.translatable(
            Networking.serverEnforced?
            K_SERVER_ENFORCED_TOOLTIP : K_WORLD_SPECIFIC_OPTION
        )))
        .build(Text.translatable(K_WORLD_SPECTATOR_BYPASS), (btn, value) -> {
            this.changed = true;
            this.serverOptions = this.serverOptions.withSpectatorBypass(value.orElse(null));
            EffectManager.setDisabledSpectator(value.orElse(Main.CONFIG.spectatorBypass));
        });
        this.worldSpectatorBypassButton = spectatorBypassButton;
        
        this.body.addWidgetEntry(worldCreativeBypassButton, spectatorBypassButton);
        this.addLockButton();
        this.toggleWorldButtons(this.serverOptions.locked());
    }

    private void addLockButton() {
        var initiallyLocked = this.serverOptions.locked();
        var lockButton = ButtonWidget.builder(Text.translatable(
            initiallyLocked ? K_UNLOCK_BUTTON : K_LOCK_BUTTON
        ), (lockBtn) -> {
            if (this.serverOptions.locked()) {
                this.serverOptions = this.serverOptions.butUnlocked();
                this.changed = true;
                this.toggleWorldButtons(false);
                return;
            }
            var scr = new ConfirmScreen(shouldLock -> {
                if (shouldLock) {
                    this.serverOptions = this.serverOptions.butLocked();
                    this.changed = true;
                    this.toggleWorldButtons(true);
                }
                this.client.setScreen(this);
            },
            Text.translatable(K_LOCK_SCREEN_TITLE),
            Text.translatable(K_LOCK_SCREEN_MESSAGE)
            );
            this.client.setScreen(scr);
            scr.disableButtons(10);
        }).build();
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
            ) && (
                !Networking.serverEnforced
                || (Networking.opsBypass && ingame && this.client.player.hasPermissionLevel(2))
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
            Main.CONFIG.setServerOptions(Networking.uniqueId, this.serverOptions);
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
        K_LOCK_BUTTON_SERVER_ENFORCED = "blindme.options.lock_world.server_enforced",
        K_WORLD_SETTINGS_SUBTITLE = "blindme.options.subtitle.world_specific",
        K_WORLD_SPECIFIC_OPTION = "blindme.options.world_specific.tooltip",
        K_WORLD_CREATIVE_BYPASS = "blindme.options.world.creative_bypass",
        K_WORLD_SPECTATOR_BYPASS = "blindme.options.world.spectator_bypass",
        K_SERVER_ENFORCED_TOOLTIP = "blindme.options.server_enforced.tooltip"
    ;

    private void toggleWorldButtons(boolean locked) {
        var active = !locked & this.ingame & !Networking.serverEnforced;
        this.effectButton.active = active;
        this.worldCreativeBypassButton.active = active;
        this.worldSpectatorBypassButton.active = active;
        this.lockButton.active = active;
        var lockButtonText = 
            (Networking.serverEnforced)?
                Text.translatable(K_LOCK_BUTTON_SERVER_ENFORCED)
            : (locked)?
                Text.translatable(K_UNLOCK_BUTTON)
            :
                Text.translatable(K_LOCK_BUTTON)
        ;
        var lockButtonTooltip = (locked && !Networking.serverEnforced)? Tooltip.of(Text.translatable(K_UNLOCK_BUTTON_TOOLTIP)) : null;
        this.lockButton.setMessage(lockButtonText);
        this.lockButton.setTooltip(lockButtonTooltip);
    }
}
