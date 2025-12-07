package hibi.blind_me.mix;

import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface StatusEffectInstanceAccessor {

    @Accessor @Nullable
    public MobEffectInstance getHiddenEffect();

    @Accessor
    public void setHiddenEffect(@Nullable MobEffectInstance effect);
}
