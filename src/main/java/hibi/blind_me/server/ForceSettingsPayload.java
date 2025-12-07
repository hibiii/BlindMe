package hibi.blind_me.server;

import org.jetbrains.annotations.Nullable;

import hibi.blind_me.config.ServerEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ForceSettingsPayload(
    @Nullable ServerEffect effect,
    @Nullable Boolean opsBypass,
    @Nullable Boolean creativeBypass,
    @Nullable Boolean spectatorBypass
) implements CustomPacketPayload {

    public static final StreamCodec<FriendlyByteBuf, ForceSettingsPayload> CODEC = CustomPacketPayload.codec(ForceSettingsPayload::write, ForceSettingsPayload::decode);
    public static final Type<ForceSettingsPayload> ID = new Type<ForceSettingsPayload>(ResourceLocation.fromNamespaceAndPath("blindme","force_effect"));

    private static ForceSettingsPayload decode(FriendlyByteBuf buf) {
        var nbt = buf.readNbt();
        var effect = ServerEffect.valueOf(nbt.getString("effect").orElseGet(null));
        var opsBypass = nbt.getBoolean("opsBypass").orElseGet(null);
        var creativeBypass = nbt.getBoolean("creativeBypass").orElseGet(null);
        var spectatorBypass = nbt.getBoolean("spectatorBypass").orElseGet(null);
        return new ForceSettingsPayload(effect, opsBypass, creativeBypass, spectatorBypass);
    }

    private void write(FriendlyByteBuf buf) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("effect", Main.CONFIG.effect.toString());
        nbt.putBoolean("opsBypass", Main.CONFIG.opsBypass);
        nbt.putBoolean("creativeBypass", Main.CONFIG.creativeBypass);
        nbt.putBoolean("spectatorBypass", Main.CONFIG.spectatorBypass);
        buf.writeNbt(nbt);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
    
}
