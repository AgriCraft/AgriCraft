package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api. Meaning if you query
 * the ICropPlant object you registered, it will return a different object.
 */
public interface IAgriPlant extends IAgriRegisterable<IAgriPlant>, IAllel<IAgriPlant> {
    /**
     * Determines the unique id of the plant. The id should be lowercase, with no special
     * characters, uses underscores instead of whitespace, and ends in '_plant'.
     *
     * @return The unique id of the plant.
     */
    @Override
    @Nonnull
    String getId();

    /**
     * Determines the user-friendly name of the plant. This does not have to be unique (although it
     * might be confusing to players) and has no special restrictions on contained characters. It is
     * up to the implementer to localize the plant name prior to passing here.
     *
     * @return The user-friendly plant name.
     */
    @Nonnull
    String getPlantName();

    /**
     * Determines the name of seeds that are auto-generated for the plant. Only used when no other
     * valid seed items are provided.
     *
     * @return The default seed name for the plant's seeds.
     */
    @Nonnull
    String getSeedName();

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
     * to this value as to get the actual growth chance of the plant.
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
     * Determines the initial Growth Stage of the plant when first planted from a seed
     *
     * @return the IAgriGrowthStage for the initial growth stage
     */
    @Nonnull
    IAgriGrowthStage getInitialGrowthStage();

    /**
     * Determines the Growth Stage to which the plant returns after being harvested
     *
     * @return the IAgriGrowthStage after harvesting
     */
    @Nonnull
    IAgriGrowthStage getGrowthStageAfterHarvest();

    /**
     * Determines the total number of growth stages that the plant has. Notice, that the number of
     * growth stages that a plant may have is traditionally less than 16, as the max meta-value of a
     * block is 15. For AgriCraft specifically, the conventional number of growth stages is 8.
     *
     * @return the total number of growth stages that the plant has.
     */
    @Nonnull
    Set<IAgriGrowthStage> getGrowthStages();

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
     * check the actual passed growth value of the plant, given that the plant is not garnteed as to
     * be mature when this method is called.
     *
     * @param products a consumer for collecting all the possible plant harvest products that should
     * be dropped.
     * @param growthStage the growth stage
     * @param stat the stats associated with this instance of the plant.
     * @param rand a random for use in rng.
     */
    void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStat stat, @Nonnull Random rand);

    /**
     * Checks if this plant allows to be cloned (spreading from a single parent)
     * @param stage the growth stage of the plant
     * @return true if this plant can single spread
     */
    boolean allowsCloning(IAgriGrowthStage stage);

    /**
     * Checks if a plant can be harvested at the given growth stage
     * @param stage the growth stage
     * @return true if the plant can be harvested
     */
    default boolean allowsHarvest(IAgriGrowthStage stage) {
        return stage.isMature();
    }

    /**
     * Internal, do not override, used by AgriCraft to determine the default, no plant implementation
     * @return true
     */
    default boolean isPlant() {
        return true;
    }
}
