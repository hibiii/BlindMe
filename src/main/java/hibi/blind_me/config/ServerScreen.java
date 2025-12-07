package hibi.blind_me.config;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ServerScreen extends ConfigScreen {
    
    private SaveCallback callback;

    public ServerScreen(Screen parent, String uniqueId, SaveCallback saveCallback) {
        super(parent, uniqueId, Component.translatable(K_BLINDME_BUTTON));
        this.callback = saveCallback;
    }

    @Override
    protected void addOptions() {
        this.ingame = true;
        this.addButtonsForCurrentServer();
    }

    @Override
    public void onClose() {
        callback.execute(this.serverOptions);
        super.onClose();
    }

    public static final String
        K_BLINDME_BUTTON = "blindme.server_options.title",
        K_BLINDME_BUTTON_TOOLTIP_MULTIPLAYER = "blindme.server_options.button.tooltip.multiplayer",
        K_BLINDME_BUTTON_TOOLTIP_SINGLEPLAYER = "blindme.server_options.button.tooltip.singleplayer"
    ;

    public static Button getButton(OnPress callback, String tooltip) {
        return Button.builder(Component.translatable(ServerScreen.K_BLINDME_BUTTON), callback)
            .bounds(5, 5, 155, 20)
            .tooltip(Tooltip.create(Component.translatable(tooltip)))
            .build();
    }

    // null options means no change
    public interface SaveCallback {
        void execute (@Nullable ServerOptions options);
    }
}
