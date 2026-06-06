package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.OptionEventListener.Event;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import hibi.blind_me.EffectManager;
import hibi.blind_me.Main;
import hibi.blind_me.Networking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class ConfigScreenFactory {

    public static Screen create(Screen parent, boolean hasGlobalSettings, @Nullable String uniqueId) {
        return create(parent, hasGlobalSettings, uniqueId, () -> {
            if (uniqueId != null) {
                Main.CONFIG.setServerOptions(uniqueId, options);
            }
            ConfigFile.save(Main.CONFIG);
            Main.CONFIG.configureInstance();
        });
    }

    public static Screen create(Screen parent, boolean hasGlobalSettings, @Nullable String uniqueId, Runnable saveCallback) {
        if (uniqueId != null) {
            options = Main.CONFIG.getServerOptions(uniqueId);
        }
        var builder = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable(K_TITLE));
        if (hasGlobalSettings) {
            builder.category(globalSettings());
        }
        if (uniqueId != null) {
            builder.category(worldSettings(uniqueId));
        }
        return builder
        .save(saveCallback)
        .build()
        .generateScreen(parent);
    }

    public static Button.Builder getButton(OnPress callback, String tooltip) {
        return Button.builder(Component.translatable(ConfigScreenFactory.K_BLINDME_BUTTON), callback)
            .bounds(5, 5, 155, 20)
            .tooltip(Tooltip.create(Component.translatable(tooltip)));
    }

    public static final String
        K_BLINDME_BUTTON = "blindme.server_options.title",
        K_BLINDME_BUTTON_TOOLTIP_MULTIPLAYER = "blindme.server_options.button.tooltip.multiplayer",
        K_BLINDME_BUTTON_TOOLTIP_SINGLEPLAYER = "blindme.server_options.button.tooltip.singleplayer"
    ;

    // Required for some screens which work independently of world
    public static ServerOptions getLastServerOptions() {
        return options;
    }
    private static ServerOptions options;

    private static ConfigCategory globalSettings() {
        // Linked effect start-end fields prevent inverted range (start > end)
        var defEfStart = Option.<Float>createBuilder()
            .name(Component.translatable(K_EFFECT_START))
            .description(OptionDescription.of(Component.translatable(K_EFFECT_START_DESCRIPTION)))
            .binding(1.25f, () -> Main.CONFIG.defaultServerEffect.start(), (start) -> {
                var effect = Main.CONFIG.defaultServerEffect;
                Main.CONFIG.defaultServerEffect = effect.setStart(start);
            })
            .controller(opt -> FloatFieldControllerBuilder.create(opt)
                .range(0f, 50f)
                .formatValue(DOUBLE_DIGIT_FORMATTER)
            )
            .build();
        
        var defEfEnd = Option.<Float>createBuilder()
            .name(Component.translatable(K_EFFECT_END))
            .description(OptionDescription.of(Component.translatable(K_EFFECT_END_DESCRIPTION)))
            .binding(5f, () -> Main.CONFIG.defaultServerEffect.end(), (end) -> {
                var effect = Main.CONFIG.defaultServerEffect;
                Main.CONFIG.defaultServerEffect = effect.setEnd(end);
            })
            .controller(opt -> FloatFieldControllerBuilder.create(opt)
                .range(0f, 50f)
                .formatValue(DOUBLE_DIGIT_FORMATTER)
            )
            .addListener((opt, ev) -> {
                if(ev != Event.STATE_CHANGE) {
                    return;
                }
                if(defEfStart.pendingValue() > opt.pendingValue()) {
                    defEfStart.requestSet(opt.pendingValue());
                }
            })
            .build();

        defEfStart.addEventListener((opt, ev) -> {
            if(ev != Event.STATE_CHANGE) {
                return;
            }
            if(defEfEnd.pendingValue() < opt.pendingValue()) {
                defEfEnd.requestSet(opt.pendingValue());
            }
        });

        // Effect enable must disable fog control buttons
        var defEfEnable = Option.<Boolean>createBuilder()
            .name(Component.translatable(K_EFFECT_ENABLED))
            .description(OptionDescription.of(Component.translatable(K_EFFECT_ENABLED_DESCRIPTION)))
            .binding(false, () -> Main.CONFIG.defaultServerEffect.enabled(), (enabled) -> {
                var effect = Main.CONFIG.defaultServerEffect;
                Main.CONFIG.defaultServerEffect = effect.setEnabled(enabled);
            })
            .addListener((opt, ev) -> {
                defEfStart.setAvailable(opt.pendingValue());
                defEfEnd.setAvailable(opt.pendingValue());
            })
            .controller(BooleanControllerBuilder::create)
            .build();
        
        // Other one-off buttons
        var defCreative = Option.<Boolean>createBuilder()
            .name(Component.translatable(K_CREATIVE_BYPASS))
            .description(OptionDescription.of(Component.translatable(K_CREATIVE_BYPASS_DESCRIPTION)))
            .binding(false, () -> Main.CONFIG.creativeBypass, (bypass) -> {
                EffectManager.setDisabledCreative(Networking.getServerOptions().creativeBypass() instanceof Boolean b? b : bypass);
                Main.CONFIG.creativeBypass = bypass;
            })
            .controller(BooleanControllerBuilder::create)
            .build();
        
        var defSpectator = Option.<Boolean>createBuilder()
            .name(Component.translatable(K_SPECTATOR_BYPASS))
            .description(OptionDescription.of(Component.translatable(K_SPECTATOR_BYPASS_DESCRIPTION)))
            .binding(true, () -> Main.CONFIG.spectatorBypass, (bypass) -> {
                EffectManager.setDisabledSpectator(Networking.getServerOptions().spectatorBypass() instanceof Boolean b? b : bypass);
                Main.CONFIG.spectatorBypass = bypass;
            })
            .controller(BooleanControllerBuilder::create)
            .build();
        
        return ConfigCategory.createBuilder()
            .name(Component.translatable(K_GLOBAL_SETTINGS_SUBTITLE))
            .option(defCreative)
            .option(defSpectator)
            .group(OptionGroup.createBuilder()
                .name(Component.translatable(K_DEFAULT_EFFECT_SETTINGS))
                .description(OptionDescription.of(Component.translatable(K_DEFAULT_EFFECT_SETTINGS_DESCRIPITION)))
                .option(defEfEnable)
                .option(defEfStart)
                .option(defEfEnd)
                .build()
            )
            .group(OptionGroup.createBuilder()
                .name(Component.translatable(K_PRESETS))
                .description(OptionDescription.of(Component.translatable(K_PRESETS_DESCRIPTION)))
                .option(effectPresetButton(ServerEffectPresets.BLINDNESS, defEfStart, defEfEnd, defEfEnable, true))
                .option(effectPresetButton(ServerEffectPresets.DARKNESS, defEfStart, defEfEnd, defEfEnable, true))
                .build()
            )
            .build();
    }

    private static ConfigCategory worldSettings(String uniqueId) {
        // Required precedence for future buttons, all others must need to set availability when constructed
        var worldLock = Option.<Boolean>createBuilder()
            .name(Component.translatable(K_LOCK_BUTTON))
            .description(OptionDescription.of(Component.translatable(K_LOCK_BUTTON_DESCRIPTION)))
            .binding(false, () -> options.locked(), (locked) -> options = options.withLocked(locked))
            .controller(BooleanControllerBuilder::create)
            .build();
        
        // Effect enable must disable fog control buttons
        var worldEfEnable = Option.<DeferrableOnOff>createBuilder()
            .name(Component.translatable(K_WORLD_EFFECT_ENABLED))
            .description(OptionDescription.of(Component.translatable(K_WORLD_EFFECT_ENABLED_DESCRIPTION)))
            .binding(DeferrableOnOff.DEFAULT, () -> {
                var effect = options.effect();
                return effect == null? DeferrableOnOff.DEFAULT : DeferrableOnOff.wrap(effect.enabled());
            }, (wrapped) -> {
                if(wrapped == DeferrableOnOff.DEFAULT) {
                    options = options.withEffect(null);
                } else {
                    options = options.withEffect(options.effect().setEnabled(wrapped.value));
                }
            })
            .available(!worldLock.pendingValue())
            .customController(opt -> new EnumController<>(opt, DeferrableOnOff.class))
            .build();
    
        // Linked effect start-end fields prevent inverted range (start > end)
        var worldEfStart = Option.<Float>createBuilder()
            .name(Component.translatable(K_EFFECT_START))
            .description(OptionDescription.of(Component.translatable(K_EFFECT_START_DESCRIPTION)))
            .binding(1.25f, () -> options.effect().start(), (start) -> {
                if (worldEfEnable.pendingValue() == DeferrableOnOff.DEFAULT) {
                    return;
                }
                options = options.withEffect(options.effect().setStart(start));
            })
            .available(!worldLock.pendingValue())
            .controller(opt -> FloatFieldControllerBuilder.create(opt)
                .range(0f, 50f)
                .formatValue(DOUBLE_DIGIT_FORMATTER)
            )
            .build();
    
        var worldEfEnd = Option.<Float>createBuilder()
            .name(Component.translatable(K_EFFECT_END))
            .description(OptionDescription.of(Component.translatable(K_EFFECT_END_DESCRIPTION)))
            .binding(5f, () -> options.effect().end(), (end) -> {
                if (worldEfEnable.pendingValue() == DeferrableOnOff.DEFAULT) {
                    return;
                }
                options = options.withEffect(options.effect().setEnd(end));
            })
            .available(!worldLock.pendingValue())
            .controller(opt -> FloatFieldControllerBuilder.create(opt)
                .range(0f, 50f)
                .formatValue(DOUBLE_DIGIT_FORMATTER)
            )
            .addListener((opt, ev) -> {
                if(ev != Event.STATE_CHANGE) {
                    return;
                }
                if(worldEfStart.pendingValue() > opt.pendingValue()) {
                    worldEfStart.requestSet(opt.pendingValue());
                }
            })
            .build();
    
        worldEfStart.addEventListener((opt, ev) -> {
            if(ev != Event.STATE_CHANGE) {
                return;
            }
            if(worldEfEnd.pendingValue() < opt.pendingValue()) {
                worldEfEnd.requestSet(opt.pendingValue());
            }
        });

        // Other one-off buttons
        var worldCreative = Option.<DeferrableOnOff>createBuilder()
            .name(Component.translatable(K_WORLD_CREATIVE_BYPASS))
            .description(OptionDescription.of(Component.translatable(K_CREATIVE_BYPASS_DESCRIPTION)))
            .binding(DeferrableOnOff.DEFAULT, () ->
                DeferrableOnOff.wrap(options.creativeBypass()),
            (wrapped) -> {
                options = options.withCreativeBypass(wrapped.value);
            })
            .available(!worldLock.pendingValue())
            .customController(opt -> new EnumController<>(opt, DeferrableOnOff.class))
            .build();
    
        var worldSpectator = Option.<DeferrableOnOff>createBuilder()
            .name(Component.translatable(K_WORLD_SPECTATOR_BYPASS))
            .description(OptionDescription.of(Component.translatable(K_SPECTATOR_BYPASS_DESCRIPTION)))
            .binding(DeferrableOnOff.DEFAULT, () ->
                DeferrableOnOff.wrap(options.spectatorBypass()),
            (wrapped) -> {
                options = options.withSpectatorBypass(wrapped.value);
            })
            .available(!worldLock.pendingValue())
            .customController(opt -> new EnumController<>(opt, DeferrableOnOff.class))
            .build();
    
        // Preset buttons must also be affected by the lock
        var worldBlindness = effectPresetButton(ServerEffectPresets.BLINDNESS, worldEfStart, worldEfEnd, worldEfEnable, DeferrableOnOff.ON);
        worldBlindness.setAvailable(!worldLock.pendingValue());

        var worldDarkness = effectPresetButton(ServerEffectPresets.DARKNESS, worldEfStart, worldEfEnd, worldEfEnable, DeferrableOnOff.ON);
        worldDarkness.setAvailable(!worldLock.pendingValue());

        worldEfEnable.addEventListener((opt, ev) -> {
            var available = opt.pendingValue() == DeferrableOnOff.ON && !worldLock.pendingValue();
            worldEfStart.setAvailable(available);
            worldEfEnd.setAvailable(available);
        });
        worldLock.addEventListener((opt, ev) -> {
            var available = !opt.pendingValue();
            worldEfEnable.setAvailable(available);
            worldCreative.setAvailable(available);
            worldSpectator.setAvailable(available);
            worldBlindness.setAvailable(available);
            worldDarkness.setAvailable(available);
        });

        return ConfigCategory.createBuilder()
            .name(Component.translatable(K_WORLD_SETTINGS))
            .option(worldCreative)
            .option(worldSpectator)
            .option(worldLock)

            .group(OptionGroup.createBuilder()
                .name(Component.translatable(K_WORLD_EFFECT))
                .description(OptionDescription.of(Component.translatable(K_WORLD_EFFECT_DESCRIPTION)))
                .option(worldEfEnable)
                .option(worldEfStart)
                .option(worldEfEnd)
                .build()
            )

            .group(OptionGroup.createBuilder()
                .name(Component.translatable(K_PRESETS))
                .description(OptionDescription.of(Component.translatable(K_PRESETS_DESCRIPTION)))
                .option(worldBlindness)
                .option(worldDarkness)
                .build()
            )
            .build();
    }

    private static final ValueFormatter<Float> DOUBLE_DIGIT_FORMATTER = value -> Component.literal(String.format("%,.2f", value).replaceAll("[\u00a0\u202F]", " "));

    private static final String
        K_TITLE = "blindme.options.title",
        K_GLOBAL_SETTINGS_SUBTITLE = "blindme.options.subtitle.global",
        K_CREATIVE_BYPASS = "blindme.options.creative_bypass",
        K_CREATIVE_BYPASS_DESCRIPTION = "blindme.options.creative_bypass.description",
        K_SPECTATOR_BYPASS = "blindme.options.spectator_bypass",
        K_SPECTATOR_BYPASS_DESCRIPTION = "blindme.options.spectator_bypass.description",
        K_DEFAULT_EFFECT_SETTINGS = "blindme.options.default_effect_settings",
        K_DEFAULT_EFFECT_SETTINGS_DESCRIPITION = "blindme.options.default_effect_settings.description",
        K_EFFECT_ENABLED = "blindme.options.effect_enabled",
        K_EFFECT_ENABLED_DESCRIPTION = "blindme.options.effect_enabled.description",
        K_EFFECT_START = "blindme.options.effect_start",
        K_EFFECT_START_DESCRIPTION = "blindme.options.effect_start.description",
        K_EFFECT_END = "blindme.options.effect_end",
        K_EFFECT_END_DESCRIPTION = "blindme.options.effect_end.description",
        K_PRESETS = "blindme.options.effect_presets",
        K_PRESETS_DESCRIPTION = "blindme.options.effect_presets.description",
        K_WORLD_SETTINGS = "blindme.options.world_specific_settings",
        K_WORLD_EFFECT = "blindme.options.current_world_effect",
        K_WORLD_EFFECT_DESCRIPTION = "blindme.options.current_world_effect.description",
        K_WORLD_CREATIVE_BYPASS = "blindme.options.world.creative_bypass",
        K_WORLD_SPECTATOR_BYPASS = "blindme.options.world.spectator_bypass",
        K_LOCK_BUTTON = "blindme.options.lock_world",
        K_LOCK_BUTTON_DESCRIPTION = "blindme.options.lock_world.description",
        K_WORLD_EFFECT_ENABLED = "blindme.options.world_effect_enabled",
        K_WORLD_EFFECT_ENABLED_DESCRIPTION = "blindme.options.world_effect_enabled.description"
    ;

    private static <T> ButtonOption effectPresetButton(ServerEffectPresets preset, Option<Float> optStart, Option<Float> optEnd, Option<T> optEnable, T enable) {
        return ButtonOption.createBuilder()
            .name(Component.translatable(preset.nameKey))
            .description(OptionDescription.of(Component.translatable(preset.descriptionKey)))
            .text(CommonComponents.EMPTY)
            .action((_, _) -> {
                optEnable.requestSet(enable);
                optStart.requestSet(preset.start);
                optEnd.requestSet(preset.end);
            })
            .build();
    }

    private static enum DeferrableOnOff implements NameableEnum {
        ON(true, CommonComponents.OPTION_ON),
        OFF(false, CommonComponents.OPTION_OFF),
        DEFAULT(null, Component.translatable("effect.blindme.default")),
        ;

        public final @Nullable Boolean value;
        public final Component component;
        DeferrableOnOff(Boolean value, Component component) {
            this.value = value;
            this.component = component;
        }

        @Override
        public Component getDisplayName() {
            return this.component;
        }

        public static DeferrableOnOff wrap(Boolean bool) {
            if (bool == null) {
                return DEFAULT;
            }
            return bool? ON : OFF;
        }
    }

    private ConfigScreenFactory() {}
}
