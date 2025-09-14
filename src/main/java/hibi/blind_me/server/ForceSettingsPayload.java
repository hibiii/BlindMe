package hibi.blind_me.server;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ForceSettingsPayload(
    @Nullable ServerEffect effect,
    @Nullable Boolean opsBypass,
    @Nullable Boolean creativeBypass,
    @Nullable Boolean spectatorBypass
) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, ForceSettingsPayload> CODEC = CustomPayload.codecOf(ForceSettingsPayload::write, ForceSettingsPayload::decode);
    public static final Id<ForceSettingsPayload> ID = new Id<ForceSettingsPayload>(Identifier.of("blindme","force_effect"));

    private static ForceSettingsPayload decode(PacketByteBuf buf) {
        var nbt = buf.readNbt();
        var effect = ServerEffect.valueOf(nbt.getString("effect").orElseGet(null));
        var opsBypass = nbt.getBoolean("opsBypass").orElseGet(null);
        var creativeBypass = nbt.getBoolean("creativeBypass").orElseGet(null);
        var spectatorBypass = nbt.getBoolean("spectatorBypass").orElseGet(null);
        return new ForceSettingsPayload(effect, opsBypass, creativeBypass, spectatorBypass);
    }

    private void write(PacketByteBuf buf) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("effect", Main.CONFIG.effect.toString());
        nbt.putBoolean("opsBypass", Main.CONFIG.opsBypass);
        nbt.putBoolean("creativeBypass", Main.CONFIG.creativeBypass);
        nbt.putBoolean("spectatorBypass", Main.CONFIG.spectatorBypass);
        buf.writeNbt(nbt);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
}
