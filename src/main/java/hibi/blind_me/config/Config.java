package hibi.blind_me.config;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueMap;

import hibi.blind_me.config.Enums.ServerEffect;

@Processor("setSerializer")
public class Config extends ReflectiveConfig {

    @Comment("If set to true, creative mode won't have the blindness effect applied")
    public final TrackedValue<Boolean> creativeBypass = this.value(false);

    @Comment("If set to true, spectator mode won't have the blindness effect applied")
    public final TrackedValue<Boolean> spectatorBypass = this.value(true);

    @Comment("The list of servers/worlds and configuration for each individually")
    public final TrackedValue<ValueMap<ServerEffect>> servers = this.value(
        ValueMap.builder(ServerEffect.BLINDNESS).build());

    @Comment("If set to false, your player character won't swing their arm when using the touch mechanic")
    public final TrackedValue<Boolean> swingHandOnTouch = this.value(true);

    public ServerEffect getEffectForServer(String uniqueId) {
        return this.servers.getRealValue().getOrDefault(uniqueId, ServerEffect.BLINDNESS);
    }

    public void setEffectForServer(String uniqueId, ServerEffect ef) {
        this.servers.getRealValue().put(uniqueId, ef);
    }

    public void setSerializer(Builder builder) {
        builder.format("json5");
    }
}
