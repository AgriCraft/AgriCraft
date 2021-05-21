package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.render.RenderMethod;
import com.infinityraider.agricraft.api.v1.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api. Meaning if you query
 * the ICropPlant object you registered, it will return a different object.
 */
public interface IAgriPlant extends IAgriRegisterable {

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
    Collection<FuzzyStack> getSeedItems();

    /**
     * Determines if the plant is to be considered a weed. Specifically, weed plants are plants that
     * cannot be harvested without de-weeding tools.
     *
     * @return If the plant is considered to be a weed.
     */
    boolean isWeed();

    /**
     * Determines if the plant is aggressive. A plant is considered aggressive if it has the
     * propensity to overtake any neighboring plant that is not of the same type. In this manner,
     * weeds are traditionally considered aggressive.
     *
     * @return If the plant is aggressive.
     */
    default boolean isAggressive() {
        return false;
    }

    /**
     * Determines if the plant is affected by fertilizers. If false, this setting will prevent any
     * fertilizer from being used on the plant.
     *
     * @return If the plant can be fertilized.
     */
    boolean isFertilizable();

    /**
     * Retrieves the spread chance for a given plant. The spread chance is a normalized p-value that
     * represents the chance of the plant to overtake a neighboring crop each tick.
     *
     * @return The spread chance of the plant.
     */
    double getSpreadChance();

    /**
     * Retrieves the spawn chance for a given plant. The spawn chance is a normalized p-value.
     * Should return 0 if the plant has no chance of spawning naturally.
     *
     * @return The spawn chance of the plant.
     */
    double getSpawnChance();

    /**
     * Retrieves the base growth chance of the given plant. The growth bonus (from
     * {@link #getGrowthChanceBonus()}) is then multiplied by the plant's growth stat (from
     * {@link IAgriStat#getGrowth()}) and added to this value as to get the actual growth chance of
     * the plant.
     *
     * @return The base growth chance of the plant.
     */
    double getGrowthChanceBase();

    /**
     * Retrieves the growth bonus, or the added p-value for the plant to grow per growth stat level.
     * The growth bonus is multiplied by the plant's growth stat (from
     * {@link IAgriStat#getGrowth()}) and added to the plant's base growth chance (from
     * {@link #getGrowthChanceBase()}) as to get the actual growth chance of the plant.
     *
     * @return The growth bonus of the plant.
     */
    double getGrowthChanceBonus();

    /**
     * Retrieves the base seed drop chance of the given plant. The seed drop chance bonus (from
     * {@link #getSeedDropChanceBonus()}) is then multiplied by the plant's growth stage and added
     * to this value as to get the actual growth chance of the plant.
     *
     * @return The base seed drop chance of the plant.
     */
    double getSeedDropChanceBase();

    /**
     * Retrieves the seed drop chance bonus, or the added p-value for the plant to drop its seed per
     * growth stage. The seed drop bonus is multiplied by the plant's growth stage and added to the
     * plant's base seed drop chance (from {@link #getSeedDropChanceBase()}) as to get the actual
     * seed drop chance of the plant.
     *
     * @return The seed drop bonus of the plant.
     */
    double getSeedDropChanceBonus();

    /**
     * Determines the normalized p-value representing the chance this plant has to drop from
     * harvested grasses. Should return {@literal 0.0} if the plant has no chance of dropping from
     * harvested grass in this manner.
     *
     * @return The chance of this plant being dropped.
     */
    double getGrassDropChance();

    /**
     * Determines the total number of growth stages that the plant has. Notice, that the number of
     * growth stages that a plant may have is traditionally less than 16, as the max meta-value of a
     * block is 15. For AgriCraft specifically, the conventional number of growth stages is 8.
     *
     * @return the total number of growth stages that the plant has.
     */
    int getGrowthStages();

    /**
     * @return the tier of the plant (used for generation in greenhouses
     */
    int getTier();

    /**
     * Fetches the user-friendly plant description for use in the Seed Journal. Notice, any
     * localization of this information is left for the implementer to handle.
     *
     * @return Information about the plant to be displayed in the Seed Journal.
     */
    @Nonnull
    String getInformation();

    /**
     * Creates a stack of the plant's primary seed item. The plant's primary seed item is the seed
     * item that was registered first for the plant.
     *
     * @return A stack of the plant's seeds.
     */
    @Nonnull
    ItemStack getSeed();

    /**
     * Gets the growth requirement for this plant, this is used to check if the plant can be planted
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
    IGrowthRequirement getGrowthRequirement();

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
    void getPossibleProducts(@Nonnull Consumer<ItemStack> products);

    /**
     * Retrieves the products that would be produced upon harvesting the given plant, with the given
     * stat, at the given position in the given world. This method will always be called on harvest
     * events, including any time that the containing crop is broken. As such, it is important as to
     * check the actual passed growth value of the plant, given that the plant is not garnteed as to
     * be mature when this method is called.
     *
     * @param products a consumer for collecting all the possible plant harvest products that should
     * be dropped.
     * @param crop the crop instance that contains this plant.
     * @param stat the stats associated with this instance of the plant.
     * @param rand a random for use in rng.
     */
    void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriCrop crop, @Nonnull IAgriStat stat, @Nonnull Random rand);

    // =========================================================================
    // IAgriPlant Rendering Interface
    // <editor-fold>
    // =========================================================================
    /**
     * Gets the texture that should be used for the AgriCraft auto-generated seed, in the case that
     * no other seed item is already set.
     *
     * @return The texture for use in rendering the auto-generated seed.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getSeedTexture();

    /**
     * Gets the rendering height of the plant. This will likely be moved to a separate interface in
     * a future release.
     *
     * @param meta The growth stage of the crop.
     * @return The height of the crop.
     */
    float getHeight(int meta);

    /**
     * Determines the method by which the plant should be rendered. This will likely be moved to a
     * separate interface in a future release.
     *
     * @return The method to use to render the plant.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    RenderMethod getRenderMethod();

    /**
     * Gets the primary texture to render this plant with as a ResourceLocation, note that this
     * texture must be stitched on the locationBlocksTextureMap See {@link RenderMethod} for what
     * primary texture should be returned. This will likely be moved to a separate interface in a
     * future release.
     *
     * @param meta The growth stage of the crop.
     * @return The plant's primary texture.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getPrimaryPlantTexture(int meta);

    /**
     * Gets the secondary texture to render this plant with as a TextureAtlasSprite, note that
     * texture this must stitched be on the locationBlocksTextureMap See {@link RenderMethod} for
     * what secondary texture should be returned. This will likely be moved to a separate interface
     * in a future release.
     *
     * @param meta The growth stage of the crop.
     * @return The plant's secondary texture.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getSecondaryPlantTexture(int meta);

    /**
     * This is called when the plant is rendered, this is never called if returned false on
     * overrideRendering Should return a non null list of BakedQuads representing the plant to be
     * rendered on the crop model. This will likely be moved to a separate interface in a future
     * release.
     *
     * @param state
     * @param growthStage
     * @param direction
     * @param textureToIcon
     * @return
     */
    @Nonnull
    @SideOnly(Side.CLIENT)
    List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon);

    // =========================================================================
    // </editor-fold>
    // =========================================================================
    @Override
    public boolean equals(@Nullable Object obj);

    @Override
    public int hashCode();

}
