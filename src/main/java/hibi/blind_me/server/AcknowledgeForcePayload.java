package hibi.blind_me.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * The packet representing a positive acknowledgement from a client. Clients sending this packet MUST meet the
 * following requirements: <ul>
 * <li> The client has understood the server's intentions on gameplay in their entirety;
 * <li> The client must be willing to enforce the restrictions. </ul>
 * 
 * Clients must <b>NOT</b> send this packet if those points are not satisfied, even if partially.
 */
public class AcknowledgeForcePayload implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, AcknowledgeForcePayload> CODEC = CustomPayload.codecOf(AcknowledgeForcePayload::write, AcknowledgeForcePayload::new);
    public static final Id<AcknowledgeForcePayload> ID = new Id<AcknowledgeForcePayload>(Identifier.of("blindme","acknowledge_force"));

    public AcknowledgeForcePayload() {
        // Empty constructor body
    }

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
