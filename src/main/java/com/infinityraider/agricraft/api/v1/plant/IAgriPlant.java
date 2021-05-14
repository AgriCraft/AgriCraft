package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;

import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api. Meaning if you query
 * the ICropPlant object you registered, it will return a different object.
 */
public interface IAgriPlant extends IAgriRegisterable<IAgriPlant>, IAgriGrowable, IAgriRenderable, IAllele<IAgriPlant> {
    /**
     * Creates a new Ingredient object for this plant with default genes
     * @return the ingredient
     */
    default AgriPlantIngredient toIngredient() {
        return new AgriPlantIngredient(this);
    }

    /**
     * Creates a new ItemStack object holding one seed item for this plant with default genes
     * @return the ItemStack
     */
    default ItemStack toItemStack() {
        return this.toItemStack(1);
    }

    /**
     * Creates a new ItemStack object holding the specified amount of seed items for this plant with default genes
     * @param amount the desired stack size
     * @return the ItemStack
     */
    default ItemStack toItemStack(int amount) {
        return AgriApi.plantToSeedStack(this, amount);
    }

    /**
     * @return a text component representing the name of this plant for use in tooltips, agricultural journal, genome, etc.
     */
    @Nonnull
    IFormattableTextComponent getPlantName();

    /**
     * @return a text component representing the name of the seed of this plant for use in tooltips, agricultural journal, etc.
     */
    @Nonnull
    IFormattableTextComponent getSeedName();

    /**
     * @return the tier of the plant, the higher the number, the higher the tier
     */
    int getTier();

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
    IFormattableTextComponent getInformation();

    /**
     * Fetches the user-friendly plant description for use in tooltips. Notice, any
     * localization of this information is left for the implementer to handle.
     *
     * @param consumer consumer accepting information about the plant to be displayed in tooltips.
     */
    void addTooltip(Consumer<ITextComponent> consumer);

    /**
     * Fetches a list of all the items that are considered seeds for this specific plant.
     *
     * @return A list of all the seeds for this plant.
     */
    @Nonnull
    Collection<ItemStack> getSeedItems();

    /**
     * Gets the growth requirements for this plant, this is used to check if the plant can be planted
     * or grow in certain locations.
     * <p>
     * If you don't want to create your own class for this, you can use
     * APIv2.getGrowthRequirementBuilder() to get a Builder object to build IGrowthRequirements If
     * you just want to have vanilla crop behaviour, you can use APIv2.getDefaultGrowthRequirement()
     * to get a growth requirement with default behaviour
     *
     * @return a set containing all growth conditions for the given stage
     */
    @Nonnull
    IAgriGrowthRequirement getGrowthRequirement(IAgriGrowthStage stage);

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
    void getAllPossibleProducts(@Nonnull Consumer<ItemStack> products);

    /**
     * Retrieves the products that would be produced upon harvesting the given plant, with the given stats.
     * This method will always be called on harvest events, including any time that the containing crop is broken.
     * As such, it is important as to check the actual passed growth value of the plant,
     * given that the plant is not guaranteed to be mature when this method is called.
     *
     * Unless the seed is directly a fruit, it should not be added to the products here,
     * as AgriCraft will take care of this internally
     *
     * @param products a consumer for collecting all the possible plant harvest products that should be dropped.
     * @param growthStage the growth stage
     * @param stats the stats associated with this instance of the plant.
     * @param rand a random for use in rng.
     */
    void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand);

    /**
     * Retrieves all possible products of clipping this plant. Note, this function is to be used
     * for informational purposes only, i.e. for display in the seed journal and JEI. As such, this
     * method is not required as to provide the complete set of all items that this plant could
     * actually drop on a clip event, but rather is required to list all the products that should
     * show up in the journal. Consequently, if you wish for your plant to produce a hidden
     * easter-egg style product, it should not be listed here!
     *
     * @param products a consumer for collecting all the possible clip products that should be listed.
     */
    void getAllPossibleClipProducts(@Nonnull Consumer<ItemStack> products);

    /**
     * Retrieves the products that would be produced upon clipping the given plant, with the given stats.
     * This method will always be called on clip events.
     * As such, it is important as to check the actual passed growth value of the plant,
     * given that the plant is not guaranteed to be mature when this method is called.
     *
     * @param products a consumer for collecting all the possible plant clip products that should be dropped.
     * @param clipper the ItemStack holding the clipper
     * @param growthStage the growth stage
     * @param stats the stats associated with this instance of the plant.
     * @param rand a random for use in rng.
     */
    void getClipProducts(@Nonnull Consumer<ItemStack> products, @Nonnull ItemStack clipper, @Nonnull IAgriGrowthStage growthStage,
                         @Nonnull IAgriStatsMap stats, @Nonnull Random rand);

    /**
     * Checks if this plant allows to be cloned (spreading from a single parent)
     * @param stage the growth stage of the plant
     * @return true if this plant can single spread
     */
    boolean allowsCloning(IAgriGrowthStage stage);

    /**
     * @return The resource location for the texture of the seed
     */
    @Nonnull
    ResourceLocation getSeedTexture();

    /**
     * @return The resource location for the model of the seed
     */
    @Nonnull
    ResourceLocation getSeedModel();

    /**
     * Checks if a plant can be harvested at the given growth stage
     * @param stage the growth stage
     * @param entity the entity who wants to harvest the plant (can be null in case it isn't harvested by an entity)
     * @return true if the plant can be harvested
     */
    default boolean allowsHarvest(@Nonnull IAgriGrowthStage stage, @Nullable LivingEntity entity) {
        return stage.isMature();
    }

    /**
     * Checks if a plant can be clipped at the given growth stage
     * @param stage the growth stage
     * @param clipper the ItemStack holding the clipper
     * @param entity the entity who wants to clip the plant (can be null in case it isn't harvested by an entity)
     * @return true if the plant can be harvested
     */
    default boolean allowsClipping(@Nonnull IAgriGrowthStage stage, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {
        return stage.isMature();
    }

    /**
     * Returns the redstone power this plant outputs at the given crop
     * In vanilla Agricraft this is only used by the redstone resource crop
     * @param crop the crop on which this is planted
     * @return the redstone power emitted (both strong and weak)
     */
    default int getRedstonePower(@Nonnull IAgriCrop crop) {
        return 0;
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
     * Callback for custom actions right after a successful clipping of this plant,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     * @param clipper the ItemStack holding the clipper item
     * @param entity the entity who clipped the plant (can be null in case it wasn't clipped by an entity)
     */
    default void onClipped(@Nonnull IAgriCrop crop, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after crop sticks holding this plant have been broken,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who broke the crop sticks (can be null in case it wasn't broken by an entity)
     */
    default void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions when an entity collides with a crop where this plant is planted
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity which collided
     */
    default void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {}

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



    /* ------------------------------------------------------------------- */
    /* Default IAllele<IAgriPlant> method implementations, do not override */
    /* ------------------------------------------------------------------- */

    @Override
    default IAgriGene<IAgriPlant> gene() {
        return AgriApi.getGeneRegistry().getPlantGene();
    }

    @Override
    default IAgriPlant trait() {
        return this;
    }

    @Override
    default boolean isDominant(IAllele<IAgriPlant> other) {
        // If the plants are equal, it doesn't matter which one is dominant and we can simply return true
        if(!this.isPlant()) {
            return false;
        }
        if(!other.trait().isPlant()) {
            return true;
        }
        if(this.equals(other)) {
            return true;
        }
        // Fetch complexity of both plants
        int a = AgriApi.getMutationRegistry().complexity(this);
        int b = AgriApi.getMutationRegistry().complexity(other.trait());
        if(a == b) {
            // Equal complexity, therefore we use an arbitrary definition for dominance, which we will base on the plant id
            return this.getId().compareTo(other.trait().getId()) < 0;
        }
        // Having more difficult obtain plants be dominant will be more challenging to deal with than having them recessive
        return a > b;
    }

    @Override
    default int comparatorValue() {
        // We don't care about species when comparing genomes
        return 0;
    }


    @Nonnull
    @Override
    default CompoundNBT writeToNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("agri_plant", this.getId());
        return tag;
    }

    @Override
    default IFormattableTextComponent getTooltip() {
        return this.getPlantName();
    }
}
