package com.infinityraider.agricraft.api.plant;

import com.infinityraider.agricraft.api.crop.IAdditionalCropData;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.render.RenderMethod;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well
 * as coding your own. If you register your own ICropPlant object, it will be
 * wrapped by the api. Meaning if you query the ICropPlant object you
 * registered, it will return a different object.
 */
public interface IAgriPlant extends Comparable<IAgriPlant> {

    /**
     * Determines the unique id of the plant. The id should be lowercase, with
     * no special characters, uses underscores instead of whitespace, and ends
     * in '_plant'.
     *
     * @return The unique id of the plant.
     */
    String getId();

    /**
     * Determines the user-friendly name of the plant. This does not have to be
     * unique (although it might be confusing to players) and has no special
     * restrictions on contained characters. It is up to the implementer to
     * localize the plant name prior to passing here.
     *
     * @return The user-friendly plant name.
     */
    String getPlantName();

    /**
     * Determines the name of seeds that are auto-generated for the plant. Only
     * used when no other valid seed items are provided.
     *
     * @return The default seed name for the plant's seeds.
     */
    String getSeedName();

    /**
     * Fetches a list of all the items that are considered seeds for this
     * specific plant.
     *
     * @return A list of all the seeds for this plant.
     */
    Collection<FuzzyStack> getSeedItems();

    /**
     * Determines if the plant is to be considered a weed. Specifically, weed
     * plants are plants that cannot be harvested without de-weeding tools.
     *
     * @return If the plant is considered to be a weed.
     */
    boolean isWeed();

    /**
     * Determines if the plant is aggressive. A plant is considered aggressive
     * if it has the propensity to overtake any neighboring plant that is not of
     * the same type. In this manner, weeds are traditionally considered
     * aggressive.
     *
     * @return If the plant is aggressive.
     */
    default boolean isAggressive() {
        return false;
    }

    /**
     * Determines if the plant is affected by fertilizers. If false, this
     * setting will prevent any fertilizer from being used on the plant.
     *
     * @return If the plant can be fertilized.
     */
    boolean isFertilizable();

    /**
     * Retrieves the spread chance for a given plant. The spread chance is a
     * normalized p-value that represents the chance of the plant to overtake a
     * neighboring crop each tick.
     *
     * @return The spread chance of the plant.
     */
    double getSpreadChance();

    /**
     * Retrieves the spawn chance for a given plant. The spawn chance is a
     * normalized p-value. Should return 0 if the plant has no chance of
     * spawning naturally.
     *
     * @return The spawn chance of the plant.
     */
    double getSpawnChance();

    /**
     * Retrieves the base growth chance of the given plant. The growth bonus
     * (from {@link #getGrowthChanceBonus()}) is then multiplied by the plant's
     * growth stat (from {@link IAgriStat#getGrowth()}) and added to this value
     * as to get the actual growth chance of the plant.
     *
     * @return The base growth chance of the plant.
     */
    double getGrowthChanceBase();

    /**
     * Retrieves the growth bonus, or the added p-value for the plant to grow
     * per growth stat level. The growth bonus is multiplied by the plant's
     * growth stat (from {@link IAgriStat#getGrowth()}) and added to the plant's
     * base growth chance (from {@link #getGrowthChanceBase()}) as to get the
     * actual growth chance of the plant.
     *
     * @return The growth bonus of the plant.
     */
    double getGrowthChanceBonus();

    /**
     * Retrieves the base seed drop chance of the given plant. The seed drop
     * chance bonus (from {@link #getSeedDropChanceBonus()}) is then multiplied
     * by the plant's growth stage and added to this value as to get the actual
     * growth chance of the plant.
     *
     * @return The base seed drop chance of the plant.
     */
    double getSeedDropChanceBase();

    /**
     * Retrieves the seed drop chance bonus, or the added p-value for the plant
     * to drop its seed per growth stage. The seed drop bonus is multiplied by
     * the plant's growth stage and added to the plant's base seed drop chance
     * (from {@link #getSeedDropChanceBase()}) as to get the actual seed drop
     * chance of the plant.
     *
     * @return The seed drop bonus of the plant.
     */
    double getSeedDropChanceBonus();

    /**
     * Determines the normalized p-value representing the chance this plant has
     * to drop from harvested grasses. Should return {@literal 0.0} if the plant
     * has no chance of dropping from harvested grass in this manner.
     *
     * @return The chance of this plant being dropped.
     */
    double getGrassDropChance();

    /**
     * Determines the total number of growth stages that the plant has. Notice,
     * that the number of growth stages that a plant may have is traditionally
     * less than 16, as the max meta-value of a block is 15. For AgriCraft
     * specifically, the conventional number of growth stages is 8.
     *
     * @return the total number of growth stages that the plant has.
     */
    int getGrowthStages();

    /**
     * Fetches the user-friendly plant description for use in the Seed Journal.
     * Notice, any localization of this information is left for the implementer
     * to handle.
     *
     * @return Information about the plant to be displayed in the Seed Journal.
     */
    String getInformation();

    /**
     * Creates a stack of the plant's primary seed item. The plant's primary
     * seed item is the seed item that was registered first for the plant.
     *
     * @return A stack of the plant's seeds.
     */
    ItemStack getSeed();

    /**
     * Links a plant to a physical block. Currently not used, as all AgriCraft
     * plants have to grow in crops at the moment.
     * <p>
     * The default implementation returns null.
     *
     * @return The block representation of the plant.
     */
    default Block getBlock() {
        return null;
    }

    /**
     * Gets the growth requirement for this plant, this is used to check if the
     * plant can be planted or grow in certain locations.
     * <p>
     * If you don't want to create your own class for this, you can use
     * APIv2.getGrowthRequirementBuilder() to get a Builder object to build
     * IGrowthRequirements If you just want to have vanilla crop behaviour, you
     * can use APIv2.getDefaultGrowthRequirement() to get a growth requirement
     * with default behaviour
     *
     * @return
     */
    IGrowthRequirement getGrowthRequirement();

    /**
     * Retrieves a list of all possible drops from the mature plant (ie fruits).
     * Theoretically this might be a unmodifiable view of a constant list, but
     * for the current state of the internals of AgriCraft it is best that this
     * is a modifiable ArrayList that doesn't have to be reused elsewhere.
     * Notice that this is used primarily for the Seed Journal and JEI
     * integration, so that if you wish to include a rare hidden easter-egg
     * fruit, it would be best not to list it here.
     *
     * @return A list containing all the possible fruits of the plant.
     */
    List<ItemStack> getAllFruits();

    /**
     * Returns a randomly-chosen fruit of the plant. This method can be
     * implemented in any way that the modder likes, using arbitrary
     * distributions (including
     * <a href="http://dilbert.com/strip/2001-10-25">Dilbert's Accounting
     * RNG</a>).
     * <p>
     * For JSON crops, AgriCraft maps all possible drops to sub-ranges of the
     * 0.0 to 1.0 range, using the drop p-value to determine the size of the
     * sub-range. The passed random number generator is then used to generate a
     * double value in the same 0.0 to 1.0 range as to select a single random
     * fruit.
     *
     * @param rand A random to select a fruit with.
     * @return A randomly chosen fruit for the plant.
     */
    ItemStack getRandomFruit(Random rand);

    /**
     * Generates an assortment of the fruits to be dropped when the plant is
     * harvested and passes them to the provided consumer. This is traditionally
     * implemented by calling the {@link getRandomFruit()} function with the
     * given random some arbitrary number of times based off of the value of the
     * plant's gain stat. The default implementation obeys this logic, using the
     * function {@code f(x) = ceil(x / 3)} for the number of times to call the
     * {@link getRandomFruit()} function.
     *
     * @param stats The plant's stats, when it is harvested.
     * @param consumer The consumer to take the dropped fruits.
     * @param rand A random for use in selecting the fruits.
     */
    default void getFruitsOnHarvest(IAgriStat stats, Consumer<ItemStack> consumer, Random rand) {
        for (int amount = (stats.getGain() + 3) / 3; amount > 0; amount--) {
            consumer.accept(getRandomFruit(rand));
        }
    }

    /**
     * Called whenever the plant is harvested. This occurs only when the plant
     * is mature when broken (unlike {@link onPlantRemoved()}). Note, the player
     * instance may be null, in the case of harvesting by some automaton.
     * <p>
     * The default implementation does nothing.
     *
     * @param world The world the plant is harvested in.
     * @param pos The position of the plant in the world.
     * @param state The state of the block container, this will likely be
     * removed.
     * @param player The player harvesting the plant, or possibly null if being
     * harvested by an automaton.
     */
    default void onHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        // Nothing to do here.
    }

    /**
     * Called whenever a plant is removed from the world (not harvested). This
     * only occurs when a plant is immature when broken (unlike
     * {@link onPlantHarvested()}).
     * <p>
     * The default implementation does nothing.
     *
     * @param world The world from which the plant was removed.
     * @param pos The position in the world where the plant was removed.
     */
    default void onRemove(World world, BlockPos pos) {
        // Nothing to do here.
    }

    /**
     * Called whenever the plant is planted be it either through mutation,
     * spawning, spreading, or manual planting.
     * <p>
     * The default implementation does nothing.
     *
     * @param world The world in which the plant was planted.
     * @param pos The position at which the plant was planted.
     */
    default void onPlanted(World world, BlockPos pos) {
        // Nothing to do here.
    }

    /**
     * Called when the TileEntity with this plant has its validate() method
     * called.
     * <p>
     * The default implementation does nothing. In the future this should likely
     * return a boolean, for determining if the plant is actually valid.
     * <p>
     * The default implementation does nothing.
     * <p>
     * TODO: Investigate return type.
     *
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as
     * the TileEntity, but is for convenience)
     */
    default void onValidate(World world, BlockPos pos, IAgriCrop crop) {
        // Nothing to do here.
    }

    /**
     * Called when the TileEntity with this plant has its invalidate() method
     * called.
     * <p>
     * The default implementation does nothing.
     *
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as
     * the TileEntity, but is for convenience)
     */
    default void onInvalidate(World world, BlockPos pos, IAgriCrop crop) {
        // Nothing to do here.
    }

    /**
     * Called when the TileEntity with this plant has its onChunkUnload() method
     * called.
     * <p>
     * The default implementation does nothing.
     *
     * @param world the World object for the TileEntity
     * @param pos the block position
     * @param crop the ICrop instance of the TileEntity (is the same object as
     * the TileEntity, but is for convenience)
     */
    default void onChunkUnload(World world, BlockPos pos, IAgriCrop crop) {
        // Nothing to do here.
    }

    /**
     * Called whenever a growth tick for the plant occurs.
     * <p>
     * The default implementation does nothing.
     *
     * @param world
     * @param pos
     * @param crop
     * @param oldGrowthStage
     */
    default void onAllowedGrowthTick(World world, BlockPos pos, IAgriCrop crop, int oldGrowthStage) {
        // Nothing to do here.
    }

    /**
     * If you want your crop to have additional data, this is called when the
     * plant is first applied to crop sticks, either through planting, spreading
     * or mutation.
     * <p>
     * The default implementation returns {@literal null}.
     *
     * @param world the world object for the crop
     * @param pos the block position
     * @param crop the crop where this plant is planted on
     * @return initial IAdditionalCropData object (can be null if you don't need
     * additional data)
     */
    default IAdditionalCropData getInitialCropData(World world, BlockPos pos, IAgriCrop crop) {
        return null;
    }

    /**
     * If this CropPlant should track additional data, this method will be
     * called when the crop containing such a CropPlant is reading from NBT.
     * <p>
     * The default implementation returns {@literal null}.
     *
     * @param tag the same tag returned from the
     * IAdditionalCropData.writeToNBT() method
     * @return an object holding the data
     */
    default IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return null;
    }

    /**
     * Gets the texture that should be used for the AgriCraft auto-generated
     * seed, in the case that no other seed item is already set.
     *
     * @return The texture for use in rendering the auto-generated seed.
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getSeedTexture();

    /**
     * Gets the rendering height of the plant. This will likely be moved to a
     * separate interface in a future release.
     *
     * @param meta The growth stage of the crop.
     * @return The height of the crop.
     */
    float getHeight(int meta);

    /**
     * Determines the method by which the plant should be rendered. This will
     * likely be moved to a separate interface in a future release.
     *
     * @return The method to use to render the plant.
     */
    @SideOnly(Side.CLIENT)
    RenderMethod getRenderMethod();

    /**
     * Gets the primary texture to render this plant with as a ResourceLocation,
     * note that this texture must be stitched on the locationBlocksTextureMap
     * See {@link RenderMethod} for what primary texture should be returned.
     * This will likely be moved to a separate interface in a future release.
     *
     * @param meta The growth stage of the crop.
     * @return The plant's primary texture.
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getPrimaryPlantTexture(int meta);

    /**
     * Gets the secondary texture to render this plant with as a
     * TextureAtlasSprite, note that texture this must stitched be on the
     * locationBlocksTextureMap See {@link RenderMethod} for what secondary
     * texture should be returned. This will likely be moved to a separate
     * interface in a future release.
     *
     * @param meta The growth stage of the crop.
     * @return The plant's secondary texture.
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getSecondaryPlantTexture(int meta);

    /**
     * This is called when the plant is rendered, this is never called if
     * returned false on overrideRendering Should return a non null list of
     * BakedQuads representing the plant to be rendered on the crop model. This
     * will likely be moved to a separate interface in a future release.
     *
     * @param state
     * @param growthStage
     * @param direction
     * @param textureToIcon
     * @return
     */
    @SideOnly(Side.CLIENT)
    List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon);

    @Override
    default int compareTo(IAgriPlant plant) {
        return this.getId().compareTo(plant.getId());
    }

}
