package hibi.blind_me;

import com.terraformersmc.modmenu.api.ModMenuApi;

import hibi.blind_me.config.ConfigScreenFactory;
import net.minecraft.client.Minecraft;

public class MenuInteg implements ModMenuApi {
    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreenFactory.create(parent, true, Minecraft.getInstance().level != null? Networking.uniqueId : null);
    }
}
