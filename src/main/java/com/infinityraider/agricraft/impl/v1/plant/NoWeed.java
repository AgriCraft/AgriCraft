package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

public final class NoWeed implements IAgriWeed {
    private static final IAgriWeed INSTANCE = new NoWeed();

    public static IAgriWeed getInstance() {
        return INSTANCE;
    }

    private NoWeed() {}

    @Override
    public final boolean isWeed() {
        return false;
    }


    @Override
    public double spawnChance(IAgriCrop crop) {
        return 0;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return NoGrowthStage.getInstance();
    }

    private final Set<IAgriGrowthStage> stages = ImmutableSet.of(this.getInitialGrowthStage());

    @Nonnull
    @Override
    public Set<IAgriGrowthStage> getGrowthStages() {
        return this.stages;
    }

    @Override
    public void onRake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
        //NOPE
    }

    private final String id = "none";

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }
}
