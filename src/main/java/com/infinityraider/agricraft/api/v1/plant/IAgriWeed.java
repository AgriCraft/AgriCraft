package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

public interface IAgriWeed extends IAgriRegisterable<IAgriWeed>, IAgriGrowable {
    /**
     * @return a text component representing the name of this weed for use in tooltips, etc.
     */
    @Nonnull
    Component getWeedName();

    /**
     * Defines the chance of spawning on a specific crop
     * @param crop the crop where this wheat is rolled to spawn
     * @return the chance to spawn, between 0 and 1, where 0 means it will never spawn, and 1 it will certainly spawn
     */
    double spawnChance(IAgriCrop crop);

    /**
     * Retrieves the base growth chance of the weed each tick.
     *
     * @return The growth chance of the weed.
     */
    double getGrowthChance(IAgriGrowthStage growthStage);

    /**
     * Defines if a weed is aggressive, aggressive weeds will try to spread to adjacent crops when mature.
     *
     * Note that this will be globally overridden for all weeds if the end user disables aggressive weeds in the config
     *
     * @return true if this weed is aggressive
     */
    boolean isAggressive() ;

    /**
     * Defines if a weed is lethal, lethal weeds will try to kill the host plant when mature.
     *
     * Note that this will be globally overridden for all weeds if the end user disables lethal weeds in the config
     *
     * @return true if this weed is lethal
     */
    boolean isLethal();

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

    /**
     * Called when the weed is raked
     * @param stage the growth stage the weed had when raked
     * @param consumer calls to this will add items to the drops list
     * @param entity the entity who raked the weed (can be null in case it is raked through automation)
     */
    void onRake(@Nonnull IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> consumer, @Nonnull Random rand, @Nullable LivingEntity entity);

    void addTooltip(Consumer<Component> consumer);

    /**
     * Do not override, internally overridden on the no weed implementation
     * @return true if this is indeed a weed;
     */
    default boolean isWeed() {
        return true;
    }
}
