package com.infinityraider.agricraft.impl.v1.fertilizer;

import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class NoFertilizer implements IAgriFertilizer {
    private static final IAgriFertilizer INSTANCE = new NoFertilizer();

    public static IAgriFertilizer getInstance() {
        return INSTANCE;
    }

    private final String id;

    private NoFertilizer() {
        this.id = "none";
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return AgriToolTips.UNKNOWN;
    }

    @Override
    public boolean canTriggerMutation() {
        return false;
    }

    @Override
    public boolean canTriggerWeeds() {
        return false;
    }

    @Override
    public int getPotency() {
        return 0;
    }

    @Override
    public boolean isFertilizer() {
        return false;
    }

    @Override
    public ActionResultType applyFertilizer(World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity) {
        return ActionResultType.FAIL;
    }

    @Nonnull
    @Override
    public Collection<Item> getVariants() {
        return Collections.emptySet();
    }

}
