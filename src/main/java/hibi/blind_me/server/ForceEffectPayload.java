package hibi.blind_me.server;

import hibi.blind_me.config.ServerEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ForceEffectPayload(ServerEffect effect) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, ForceEffectPayload> CODEC = CustomPayload.codecOf(ForceEffectPayload::write, ForceEffectPayload::new);
    public static final Id<ForceEffectPayload> ID = new Id<ForceEffectPayload>(Identifier.of("blindme","force_effect"));

    private ForceEffectPayload(PacketByteBuf buf) {
        this(ServerEffect.valueOf(buf.readString()));
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.effect.toString());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
}
