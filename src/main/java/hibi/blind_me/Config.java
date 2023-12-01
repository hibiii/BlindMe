package hibi.blind_me;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueMap;

import hibi.blind_me.ConfigEnums.ServerEffect;

public class Config extends ReflectiveConfig {

    @Comment("If set to true, creative mode won't have the blindness effect applied")
    public final TrackedValue<Boolean> creativeBypass = this.value(false);

    @Comment("If set to true, spectator mode won't have the blindness effect applied")
    public final TrackedValue<Boolean> spectatorBypass = this.value(true);

    @Comment("The list of servers/worlds and configuration for each individually")
    public final TrackedValue<ValueMap<ServerEffect>> servers = this.value(
        ValueMap.builder(ServerEffect.BLINDNESS).build());

    public ServerEffect getEffectForServer(String uniqueId) {
        return this.servers.getRealValue().getOrDefault(uniqueId, ServerEffect.BLINDNESS);
    }
}
