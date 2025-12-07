package hibi.blind_me.config;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import hibi.blind_me.Networking;

public class ConfigScreen extends OptionsSubScreen {

    private boolean changed = false;
    private ServerOptions serverOptions;
    private boolean ingame = false;
    private CycleButton<Optional<ServerEffect>> effectButton = null;
    private CycleButton<Optional<Boolean>> worldCreativeBypassButton = null;
    private CycleButton<Optional<Boolean>> worldSpectatorBypassButton = null;
    private Button lockButton = null;
    private boolean serverEnforced = false;
    private ServerOptions defaults = ServerOptions.DEFAULT;

    public ConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable(K_TITLE));
        this.serverOptions = Networking.getServerOptions();
        this.serverEnforced = Networking.serverEnforced;
        this.defaults = Main.CONFIG.getDefaults();
    }

    @Override
    protected void addOptions() {
        this.ingame = this.minecraft.level != null;
        var creativeBypassButton = CycleButton.onOffBuilder(Main.CONFIG.creativeBypass)
        .create(Component.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.creativeBypass = set;
                EffectManager.setDisabledCreative(this.serverOptions.creativeBypass() instanceof Boolean b? b : set);
            }
        );
        var spectatorBypassButton = CycleButton.onOffBuilder(Main.CONFIG.spectatorBypass)
        .create(Component.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.spectatorBypass = set;
                EffectManager.setDisabledSpectator(this.serverOptions.spectatorBypass() instanceof Boolean b? b : set);
            }
        );
        this.list.addSmall(creativeBypassButton, spectatorBypassButton);

        var darknessPulseButton = CycleButton
        .onOffBuilder(Main.CONFIG.disableDarknessPulse)
        .withTooltip(((bool) -> Tooltip.create(Component.translatable(K_DISABLE_PULSE_TOOLTIP))))
        .create(Component.translatable(K_DISABLE_PULSE),
            (button, set) -> {
                this.changed = true;
                Main.CONFIG.disableDarknessPulse = set;
            }
        );
        darknessPulseButton.setWidth(310);
        this.list.addSmall(darknessPulseButton, null);
        this.addDefaultEffectButton();
        this.addButtonsForCurrentServer();
    }

    private void addDefaultEffectButton() {
        var button = CycleButton
            .builder((ServerEffect ef) -> {
                return switch(ef) {
                    case BLINDNESS -> Component.translatable("effect.minecraft.blindness");
                    case DARKNESS -> Component.translatable("effect.minecraft.darkness");
                    case OFF -> CommonComponents.OPTION_OFF;
                };
            })
            .withValues(ServerEffect.values())
            .withInitialValue(Main.CONFIG.defaultServerEffect)
            .withTooltip(effect -> Tooltip.create(Component.translatable(K_EFFECT_DETAILS_TOOLTIP + effect.toString())))
            .create(Component.translatable(K_DEFAULT_EFFECT), (_button, value) -> {
                this.changed = true;
                Main.CONFIG.defaultServerEffect = value;
                EffectManager.setDesiredEffect(Main.CONFIG.getEffectForServer(Networking.uniqueId));
            });
        button.setWidth(310);
        this.list.addSmall(button, null);
    }

    private void addButtonsForCurrentServer() {
        var worldSettings = new StringWidget(310, 27, Component.translatable(K_WORLD_SETTINGS_SUBTITLE), this.font);
        this.list.addSmall(worldSettings, null);

        var effectButton = CycleButton
            .builder((Optional<ServerEffect> ef) -> {
                if (ef.isEmpty()) {
                    return Component.translatable((serverEnforced)? "effect.blindme.server_default" : "effect.blindme.default");
                }
                return switch(ef.get()) {
                    case BLINDNESS -> Component.translatable("effect.minecraft.blindness");
                    case DARKNESS -> Component.translatable("effect.minecraft.darkness");
                    case OFF -> CommonComponents.OPTION_OFF;
                };
            })
            .withValues(Optional.empty(), Optional.of(ServerEffect.BLINDNESS), Optional.of(ServerEffect.DARKNESS), Optional.of(ServerEffect.OFF)
            )
            .withInitialValue(Optional.ofNullable(
                this.serverOptions.effect()
            ))
            .withTooltip(optional -> {
                var effect = optional.orElse(null);
                Component text;
                if (serverEnforced) {
                    text = Component.translatable((Networking.isOpForBypass)?
                        K_OP_BYPASS_ALLOWED : K_SERVER_ENFORCED_TOOLTIP);
                } else {
                    text = Component.translatable(K_EFFECT_DETAILS_TOOLTIP
                        + ((effect != null) ? effect.toString() : "default")
                    );
                }
                return Tooltip.create(text);
            })
            .create(Component.translatable(K_CURRENT_SERVER), (_button, value) -> {
                this.changed = true;
                this.serverOptions = this.serverOptions.withEffect(value.orElse(null));
                EffectManager.setDesiredEffect(value.orElse(defaults.effect()));
            });
        effectButton.setWidth(310);
        this.list.addSmall(effectButton, null);
        this.effectButton = effectButton;
        
        var creativeBypassButton = CycleButton
        .builder((Optional<Boolean> bypass) -> {
            if (bypass.isEmpty()) {
                return Component.translatable((serverEnforced)? "effect.blindme.server_default" : "effect.blindme.default");
            }
            return (bypass.get())? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
        })
        .withValues(Optional.empty(), Optional.of(true), Optional.of(false))
        .withInitialValue(Optional.ofNullable(this.serverOptions.creativeBypass()))
        .withTooltip(optional -> Tooltip.create(Component.translatable(
            serverEnforced? ((Networking.isOpForBypass)?
                K_OP_BYPASS_ALLOWED : K_SERVER_ENFORCED_TOOLTIP)
            : K_WORLD_SPECIFIC_OPTION
        )))
        .create(Component.translatable(K_WORLD_CREATIVE_BYPASS), (btn, value) -> {
            this.changed = true;
            this.serverOptions = this.serverOptions.withCreativeBypass(value.orElse(null));
            EffectManager.setDisabledCreative(value.orElse(defaults.creativeBypass()));
        });
        this.worldCreativeBypassButton = creativeBypassButton;
        
        var spectatorBypassButton = CycleButton
        .builder((Optional<Boolean> bypass) -> {
            if (bypass.isEmpty()) {
                return Component.translatable((serverEnforced)? "effect.blindme.server_default" : "effect.blindme.default");
            }
            return (bypass.get())? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
        })
        .withValues(Optional.empty(), Optional.of(true), Optional.of(false))
        .withInitialValue(Optional.ofNullable(this.serverOptions.spectatorBypass()))
        .withTooltip(optional -> Tooltip.create(Component.translatable(
            Networking.serverEnforced?((Networking.isOpForBypass)?
                K_OP_BYPASS_ALLOWED : K_SERVER_ENFORCED_TOOLTIP)
            : K_WORLD_SPECIFIC_OPTION
        )))
        .create(Component.translatable(K_WORLD_SPECTATOR_BYPASS), (btn, value) -> {
            this.changed = true;
            this.serverOptions = this.serverOptions.withSpectatorBypass(value.orElse(null));
            EffectManager.setDisabledSpectator(value.orElse(defaults.spectatorBypass()));
        });
        this.worldSpectatorBypassButton = spectatorBypassButton;
        
        this.list.addSmall(worldCreativeBypassButton, spectatorBypassButton);
        this.addLockButton();
        this.toggleWorldButtons(this.serverOptions.locked());
    }

    private void addLockButton() {
        var initiallyLocked = this.serverOptions.locked();
        var lockButton = Button.builder(Component.translatable(
            initiallyLocked ? K_UNLOCK_BUTTON : K_LOCK_BUTTON
        ), (lockBtn) -> {
            if (this.serverOptions.locked()) {
                var scr = new ConfirmScreen(
                    shouldUnlock -> {
                        if (shouldUnlock) {
                            this.serverOptions = this.serverOptions.butUnlocked();
                            this.changed = true;
                            this.toggleWorldButtons(false);
                        }
                        this.minecraft.setScreen(this);
                    },
                    Component.translatable(K_UNLOCK_SCREEN_TITLE),
                    Component.translatable(K_UNLOCK_SCREEN_MESSAGE)
                );
                this.minecraft.setScreen(scr);
                scr.setDelay(10);
            return;
            }
            var scr = new ConfirmScreen(
                shouldLock -> {
                    if (shouldLock) {
                        this.serverOptions = this.serverOptions.butLocked();
                        this.changed = true;
                        this.toggleWorldButtons(true);
                    }
                    this.minecraft.setScreen(this);
                },
                Component.translatable(K_LOCK_SCREEN_TITLE),
                Component.translatable(K_LOCK_SCREEN_MESSAGE)
            );
            this.minecraft.setScreen(scr);
            scr.setDelay(10);
        }).build();
        this.list.addSmall(lockButton, null);
        this.lockButton = lockButton;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lockButton != null) {
            this.lockButton.active = ingame && (
                !this.serverOptions.locked()
                || (this.serverOptions.locked() && this.minecraft.hasShiftDown())
            ) && (!Networking.serverEnforced);
        }
    }

    @Override
    public void onClose() {
        this.save();
        Main.CONFIG.configureInstance();
        super.onClose();
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
        K_UNLOCK_SCREEN_TITLE = "blindme.options.unlock_world.screen.title",
        K_UNLOCK_SCREEN_MESSAGE = "blindme.options.unlock_world.screen.message",
        K_UNLOCK_BUTTON = "blindme.options.unlock_world",
        K_UNLOCK_BUTTON_TOOLTIP = "blindme.options.unlock_world.tooltip",
        K_LOCK_BUTTON_SERVER_ENFORCED = "blindme.options.lock_world.server_enforced",
        K_LOCK_BUTTON_SERVER_ENFORCED_TOOLTIP = "blindme.options.lock_world.server_enforced.tooltip",
        K_WORLD_SETTINGS_SUBTITLE = "blindme.options.subtitle.world_specific",
        K_WORLD_SPECIFIC_OPTION = "blindme.options.world_specific.tooltip",
        K_WORLD_CREATIVE_BYPASS = "blindme.options.world.creative_bypass",
        K_WORLD_SPECTATOR_BYPASS = "blindme.options.world.spectator_bypass",
        K_SERVER_ENFORCED_TOOLTIP = "blindme.options.server_enforced.tooltip",
        K_OP_BYPASS_ALLOWED = "blindme.options.server_enforced.ops_bypass.tooltip"
    ;

    private void toggleWorldButtons(boolean locked) {
        var active = this.ingame && !locked && (!Networking.serverEnforced || Networking.isOpForBypass);
        this.effectButton.active = active;
        this.worldCreativeBypassButton.active = active;
        this.worldSpectatorBypassButton.active = active;
        // this.lockButton.active = active;
        var lockButtonText = 
            (Networking.serverEnforced)?
                Component.translatable(K_LOCK_BUTTON_SERVER_ENFORCED)
            : (locked)?
                Component.translatable(K_UNLOCK_BUTTON)
            :
                Component.translatable(K_LOCK_BUTTON)
        ;
        var lockButtonTooltip = 
            (Networking.serverEnforced)?
                Tooltip.create(Component.translatable(K_LOCK_BUTTON_SERVER_ENFORCED_TOOLTIP))
            : (locked) ?
                Tooltip.create(Component.translatable(K_UNLOCK_BUTTON_TOOLTIP))
            :
                null
        ;
        this.lockButton.setMessage(lockButtonText);
        this.lockButton.setTooltip(lockButtonTooltip);
    }
}
