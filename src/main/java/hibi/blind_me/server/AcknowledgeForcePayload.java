package hibi.blind_me.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * The packet representing a positive acknowledgement from a client. Clients sending this packet MUST meet the
 * following requirements: <ul>
 * <li> The client has understood the server's intentions on gameplay in their entirety;
 * <li> The client must be willing to enforce the restrictions. </ul>
 * 
 * Clients must <b>NOT</b> send this packet if those points are not satisfied, even if partially.
 */
public class AcknowledgeForcePayload implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, AcknowledgeForcePayload> CODEC = CustomPacketPayload.codec(AcknowledgeForcePayload::write, AcknowledgeForcePayload::new);
    public static final Type<AcknowledgeForcePayload> ID = new Type<AcknowledgeForcePayload>(Identifier.fromNamespaceAndPath("blindme","acknowledge_force"));

    public AcknowledgeForcePayload() {
        // Empty constructor body
    }

    private AcknowledgeForcePayload(FriendlyByteBuf buf) {
        // Empty constructor body
    }

    private void write(FriendlyByteBuf buf) {
        // Empty function body
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
