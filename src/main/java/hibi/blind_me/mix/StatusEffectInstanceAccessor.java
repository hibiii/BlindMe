package hibi.blind_me.mix;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(StatusEffectInstance.class)
public interface StatusEffectInstanceAccessor {

    @Accessor @Nullable
    public StatusEffectInstance getHiddenEffect();

    @Accessor
    public void setHiddenEffect(@Nullable StatusEffectInstance effect);
}
