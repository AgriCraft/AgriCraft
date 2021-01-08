package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;

import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api. Meaning if you query
 * the ICropPlant object you registered, it will return a different object.
 */
public interface IAgriPlant extends IAgriRegisterable<IAgriPlant>, IAgriGrowable, IAgriRenderable, IAllel<IAgriPlant> {
    /**
     * Fetches a list of all the items that are considered seeds for this specific plant.
     *
     * @return A list of all the seeds for this plant.
     */
    @Nonnull
    Collection<ItemStack> getSeeds();

    /**
     * Determines if the plant is affected by fertilizers. If false, this setting will prevent any
     * fertilizer from being used on the plant.
     *
     * @return If the plant can be fertilized.
     */
    boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer);

    /**
     * Retrieves the spread chance for a given plant. The spread chance is a normalized p-value that
     * represents the chance of the plant to overtake a neighboring crop each tick.
     *
     * @return The spread chance of the plant.
     */
    double getSpreadChance(IAgriGrowthStage growthStage);

    /**
     * Retrieves the base growth chance of the given plant. The growth bonus (from
     * {@link #getGrowthChanceBonus(IAgriGrowthStage)}) is then multiplied by the plant's growth stat
     * and added to this value as to get the actual growth chance of
     * the plant.
     *
     * @return The base growth chance of the plant.
     */
    double getGrowthChanceBase(IAgriGrowthStage growthStage);

    /**
     * Retrieves the growth bonus, or the added p-value for the plant to grow per growth stat level.
     * The growth bonus is multiplied by the plant's growth stat and added to the plant's base growth chance
     * (from {@link #getGrowthChanceBase(IAgriGrowthStage)}) as to get the actual growth chance of the plant.
     *
     * @return The growth bonus of the plant.
     */
    double getGrowthChanceBonus(IAgriGrowthStage growthStage);

    /**
     * Retrieves the base seed drop chance of the given plant. The seed drop chance bonus (from
     * {@link #getSeedDropChanceBonus(IAgriGrowthStage)}) is then multiplied by the plant's growth stage and added
     * to this value as to get the actual seed drop chance of the plant.
     *
     * @return The base seed drop chance of the plant.
     */
    double getSeedDropChanceBase(IAgriGrowthStage growthStage);

    /**
     * Retrieves the seed drop chance bonus, or the added p-value for the plant to drop its seed per
     * growth stage. The seed drop bonus is multiplied by the plant's growth stage and added to the
     * plant's base seed drop chance (from {@link #getSeedDropChanceBase(IAgriGrowthStage)}) as to get the actual
     * seed drop chance of the plant.
     *
     * @return The seed drop bonus of the plant.
     */
    double getSeedDropChanceBonus(IAgriGrowthStage growthStage);

    /**
     * Determines the Growth Stage to which the plant returns after being harvested
     *
     * @return the IAgriGrowthStage after harvesting
     */
    @Nonnull
    IAgriGrowthStage getGrowthStageAfterHarvest();

    /**
     * Determines the original BlockState corresponding to a the GrowthStage of this plant on Crop Sticks,
     * if the plant had been planted in the world instead.
     * Can return an empty Optional in case this is a crop sticks only plant.
     *
     * @param stage the growth stage of the plant
     * @return Optional containing the corresponding block state, or empty.
     */
    Optional<BlockState> asBlockState(IAgriGrowthStage stage);

    /**
     * Fetches the user-friendly plant description for use in the Seed Journal. Notice, any
     * localization of this information is left for the implementer to handle.
     *
     * @return Information about the plant to be displayed in the Seed Journal.
     */
    @Nonnull
    String getInformation(IAgriGrowthStage stage);

    /**
     * Creates a stack of the plant's primary seed item. The plant's primary seed item is the seed
     * item that was registered first for the plant.
     *
     * @return A stack of the plant's seeds.
     */
    @Nonnull
    ItemStack getSeed();

    /**
     * Gets the growth requirements for this plant, this is used to check if the plant can be planted
     * or grow in certain locations.
     * <p>
     * If you don't want to create your own class for this, you can use
     * APIv2.getGrowthRequirementBuilder() to get a Builder object to build IGrowthRequirements If
     * you just want to have vanilla crop behaviour, you can use APIv2.getDefaultGrowthRequirement()
     * to get a growth requirement with default behaviour
     *
     * @return
     */
    @Nonnull
    Set<IGrowCondition> getGrowConditions(IAgriGrowthStage stage);

    /**
     * Retrieves all possible products of harvesting this plant. Note, this function is to be used
     * for informational purposes only, i.e. for display in the seed journal and JEI. As such, this
     * method is not required as to provide the complete set of all items that this plant could
     * actually drop on a harvest event, but rather is required to list all the products that should
     * show up in the journal. Consequently, if you wish for your plant to produce a hidden
     * easter-egg style product, it should not be listed here!
     *
     * @param products a consumer for collecting all the possible plant products that should be
     * listed.
     */
    void getAllPossibleProducts(IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> products);

    /**
     * Retrieves the products that would be produced upon harvesting the given plant, with the given
     * stat, at the given position in the given world. This method will always be called on harvest
     * events, including any time that the containing crop is broken. As such, it is important as to
     * check the actual passed growth value of the plant, given that the plant is not guaranteed to
     * be mature when this method is called.
     *
     * Unless the seed is directly a fruit, it should not be added to the products here,
     * as AgriCraft will take care of this internally
     *
     * @param products a consumer for collecting all the possible plant harvest products that should
     * be dropped.
     * @param growthStage the growth stage
     * @param stats the stats associated with this instance of the plant.
     * @param rand a random for use in rng.
     */
    void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand);

    /**
     * Checks if this plant allows to be cloned (spreading from a single parent)
     * @param stage the growth stage of the plant
     * @return true if this plant can single spread
     */
    boolean allowsCloning(IAgriGrowthStage stage);

    /**
     * Checks if a plant can be harvested at the given growth stage
     * @param stage the growth stage
     * @param entity the entity who wants to harvest the plant (can be null in case it wasn't planted by an entity)
     * @return true if the plant can be harvested
     */
    default boolean allowsHarvest(@Nonnull IAgriGrowthStage stage, @Nullable LivingEntity entity) {
        return stage.isMature();
    }

    /**
     * Callback for custom actions right after this plant has been planted on crop sticks,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who planted the plant (can be null in case it wasn't planted by an entity)
     */
    default void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after this plant has been spawned on crop sticks due to a mutation or spread,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant spawned
     */
    default void onSpawned(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a successful growth increment,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     */
    default void onGrowth(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a plant is removed (for instance by being killed by weeds),
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     */
    default void onRemoved(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a successful harvest of this plant,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     * @param entity the entity who harvested the plant (can be null in case it wasn't planted by an entity)
     */
    default void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after crop sticks holding this plant have been broken,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who broke the crop sticks (can be null in case it wasn't broken by an entity)
     */
    default void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Method to check if this is an actual plant, or the default, non-null no_plant object
     *
     * Internal, do not override, used by AgriCraft to determine the default, no plant implementation
     *
     * @return true
     */
    default boolean isPlant() {
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    List<BakedQuad> bakeQuads(IAgriGrowthStage stage);

    @Nonnull
    List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage);
}
