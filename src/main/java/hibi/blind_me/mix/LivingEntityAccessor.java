package hibi.blind_me.mix;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {    
    @Accessor
    public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();
}
