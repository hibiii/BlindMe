package hibi.blind_me.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class AcknowledgeForcePayload implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, AcknowledgeForcePayload> CODEC = CustomPayload.codecOf(AcknowledgeForcePayload::write, AcknowledgeForcePayload::new);
    public static final Id<AcknowledgeForcePayload> ID = new Id<AcknowledgeForcePayload>(Identifier.of("blindme","acknowledge_force"));

    private AcknowledgeForcePayload(PacketByteBuf buf) {
        // Empty constructor body
    }

    private void write(PacketByteBuf buf) {
        // Empty function body
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
}
