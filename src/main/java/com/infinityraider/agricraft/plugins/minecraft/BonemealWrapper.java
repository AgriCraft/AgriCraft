package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class BonemealWrapper implements IAgriFertilizer, IAgriAdapter<IAgriFertilizer> {

    public static final BonemealWrapper INSTANCE = new BonemealWrapper();

    private static final ItemStack BONE_MEAL = new ItemStack(Items.BONE_MEAL);

    private BonemealWrapper() {}

    @Override
    public boolean canTriggerMutation() {
        return true;
    }

    @Override
    public boolean canTriggerWeeds() {
        return true;
    }

    @Override
    public ActionResultType applyFertilizer(World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity) {
        target.applyGrowthTick();
        if((entity instanceof PlayerEntity) && !(((PlayerEntity) entity).isCreative())) {
            stack.shrink(1);
        }
        // to spawn the bone meal particles
        world.playEvent(2005, pos, 0);
        return ActionResultType.CONSUME;
    }

    @Override
    public boolean accepts(Object obj) {
        return obj instanceof ItemStack && BONE_MEAL.isItemEqual((ItemStack) obj);
    }

    @Override
    public Optional<IAgriFertilizer> valueOf(Object obj) {
        if (obj instanceof ItemStack && BONE_MEAL.isItemEqual((ItemStack) obj)) {
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public String getId() {
        return BONE_MEAL.getItem().getRegistryName().toString();
    }
}
