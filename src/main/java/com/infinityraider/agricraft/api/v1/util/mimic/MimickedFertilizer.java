package com.infinityraider.agricraft.api.v1.util.mimic;

import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

/**
 * Utility class, extend to override some selected behaviour of existing fertilizers
 */
public class MimickedFertilizer implements IAgriFertilizer {
    private final IAgriFertilizer original;

    protected MimickedFertilizer(IAgriFertilizer original) {
        this.original = original;
    }

    protected final IAgriFertilizer getOriginal() {
        return this.original;
    }

    @Override
    public boolean canTriggerMutation() {
        return this.getOriginal().canTriggerMutation();
    }

    @Override
    public boolean canTriggerWeeds() {
        return this.getOriginal().canTriggerWeeds();
    }

    @Override
    public InteractionResult applyFertilizer(Level world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity) {
        return this.getOriginal().applyFertilizer(world, pos, target, stack, random, entity);
    }

    @Override
    public boolean canFertilize(IAgriFertilizable target) {
        return this.getOriginal().canFertilize(target);
    }

    @Override
    public boolean accepts(@Nullable Object obj) {
        return this.getOriginal().accepts(obj);
    }

    @Nonnull
    @Override
    public Optional<IAgriFertilizer> valueOf(@Nullable Object obj) {
        return this.getOriginal().valueOf(obj);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.getOriginal().getId();
    }
}
