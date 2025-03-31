package hibi.blind_me.config;

import java.util.ArrayList;
import java.util.List;

import hibi.blind_me.EffectManager;
import hibi.blind_me.config.Enums.ServerEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    private boolean changed = false;
    private List<ClickableWidget> widgets = new ArrayList<ClickableWidget>();

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable(K_TITLE));
    }

    // protected void init() {
    //     this.initHeader();
    //     this.initBody();
    //     this.initFooter();
    //     this.initOptionButtons();
    //     this.refreshWidgetPositions();
    // }

    protected void initOptionButtons() {
        var creativeBypassButton = CyclingButtonWidget.onOffBuilder(Manager.CONFIG.creativeBypass)
        .build(Text.translatable(K_CREATIVE_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.creativeBypass = set;
            }
        );
        this.widgets.add(creativeBypassButton);
        var spectatorBypassButton = CyclingButtonWidget.onOffBuilder(Manager.CONFIG.spectatorBypass)
        .build(Text.translatable(K_SPECTATOR_BYPASS),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.spectatorBypass = set;
            }
        );
        this.widgets.add(spectatorBypassButton);

        var darknessPulseButton = CyclingButtonWidget
        .onOffBuilder(Manager.CONFIG.disableDarknessPulse)
        .tooltip(((bool) -> Tooltip.of(Text.translatable(K_DISABLE_PULSE_TOOLTIP))))
        .build(Text.translatable(K_DISABLE_PULSE),
            (button, set) -> {
                this.changed = true;
                Manager.CONFIG.disableDarknessPulse = set;
            }
        );
        darknessPulseButton.setWidth(310);
        this.widgets.add(darknessPulseButton);

        boolean ingame = this.client.world != null;
        ServerEffect initial = ingame
            ? Manager.CONFIG.getEffectForServer(EffectManager.getUniqueId())
            : ServerEffect.OFF;
        var serverEffectButton = CyclingButtonWidget
            .builder((ServerEffect ef) -> {
                return switch(ef) {
                    case BLINDNESS -> Text.literal("blid");
                    case DARKNESS -> Text.literal("dork");
                    case OFF -> ScreenTexts.OFF;
                };
            })
            .values(ServerEffect.values())
            .initially(initial)
            .tooltip(effect -> Tooltip.of(Text.translatable(K_SERVER_EFFECT_TOOLTIP + effect.toString())))
            .build(Text.translatable(K_CURRENT_SERVER), (button, value) -> {
                this.changed = true;
                Manager.CONFIG.setEffectForServer(EffectManager.getUniqueId(), value);
            });
        serverEffectButton.active = ingame;
        serverEffectButton.setWidth(310);
        this.widgets.add(serverEffectButton);
    }

    @Override
    protected void addOptions() {
        this.body.addAll(this.widgets);
    }

    @Override
    public void close() {
        this.save();
        super.close();
    }

    // public void render2(GuiGraphics graphics, int mX, int mY, float delta) {
    //     super.render(graphics, mX, mY, delta);
    //     graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    // }

    protected void save() {
        if (this.changed) {
            Manager.save();
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
