package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

public interface IAgriWeed extends IAgriRegisterable<IAgriWeed> {
    double spawnChance(IAgriCrop crop);

    /**
     * Determines the initial Growth Stage of the weed when first planted from a seed
     *
     * @return the IAgriGrowthStage for the initial growth stage
     */
    @Nonnull
    IAgriGrowthStage getInitialGrowthStage();

    /**
     * Determines all the growth stages that the weed can have.
     * For AgriCraft specifically, the conventional number of growth stages is 8.
     *
     * @return a set containing all the possible growth stages of the weed.
     */
    @Nonnull
    Set<IAgriGrowthStage> getGrowthStages();

    /**
     * Callback for custom actions right after this weed has been spawned on crop sticks,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this weed spawned
     */
    default void onSpawned(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a successful growth tick,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this weed is growing
     */
    default void onGrowthTick(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after crop sticks where this weed is growing, have been broken,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who broke the crop sticks (can be null in case it wasn't broken by an entity)
     */
    default void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    void onRake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity);

    default boolean isWeed() {
        return true;
    }
}
