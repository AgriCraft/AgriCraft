package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.api.crop.IAdditionalCropData;
import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.render.RenderMethod;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.Function;

/**
 * The main class used by TileEntityCrop.
 * Only make one object of this per seed object, and register using the plant registry.
 * ICropPlant is implemented to be able to read data from this class from the API
 */
public abstract class CropPlant implements IAgriPlant {
	
    private IGrowthRequirement growthRequirement;
    private boolean blackListed;
    private boolean ignoreVanillaPlantingRule;

    public CropPlant() {
        this.growthRequirement = initGrowthRequirement();
        growthRequirement = growthRequirement == null ? GrowthRequirementHandler.getNewBuilder().build() : growthRequirement;
        this.blackListed = false;
        this.ignoreVanillaPlantingRule = false;
    }

    /**
     * GLOBAL CROPPLANT METHODS
     */

    /**
     * Gets the growth rate for this CropPlant, used in calculations on growth tick
     * @return the growth rate
     */
    public final int getGrowthRate() {
    	int tier = getTier();
    	
    	if (tier > 0 && tier <= Constants.GROWTH_TIER.length) {
    		return Constants.GROWTH_TIER[tier];
    	} else {
    		return Constants.GROWTH_TIER[0];
    	}
    }

    /**
     * Returns the tier of the seed as represented as an integer value, or the overriding value.
     * The overriding value may be set in the configuration files.
     * 
     * Does not always have same output as {@link #getTier()}.
     * 
     * Should fall within the range of {@link Constants#GROWTH_TIER}.
     * 
     * @return the tier of the seed.
     */
    @Override
    public int getTier() {
        return 1;
    }

    /** Gets the spread chance in percent for this plant */
    @Override
    public double getSpreadChance() {
        return 1 / getTier();
    }

    /**
     * @return if the plant is blacklisted
     */
    public final boolean isBlackListed() {
        return blackListed;
    }

    /**
     * Sets the blacklist status for this plant
     * @param status true if it should be blacklisted, false if not
     */
    public final void setBlackListStatus(boolean status) {
        this.blackListed = status;
    }

    /**
     * Checks if this plant ignores the rule to disabled vanilla planting
     * true means that the seed for this plant can still be planted even though vanilla planting is disabled
     * @return if this ignores the vanilla planting rule or not
     */
    public final boolean ingoresVanillaPlantingRule() {
        return ignoreVanillaPlantingRule;
    }

    /**
     * Sets if this plant should ignore the rule to disabled vanilla planting
     * true means that the seed for this plant can still be planted even though vanilla planting is disabled
     * @param value if this ignores the vanilla planting rule or not
     */
    public final void setIgnoreVanillaPlantingRule(boolean value) {
        this.ignoreVanillaPlantingRule = value;
    }

    /**
     * ICROPPLANT METHODS
     */

    /**
     * Gets the block instance for this plant.
     *
     * @return the Block object for this plant.
     */
    @Override
    public abstract Block getBlock();

    @Override
    public Collection<Item> getSeedItems() {
        return Arrays.asList(AgriItems.getInstance().AGRI_SEED);
    }

	@Override
	public final ItemStack getSeed() {
		ItemStack stack = new ItemStack(this.getSeedItems().stream().findFirst().orElse(AgriItems.getInstance().AGRI_SEED));
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(AgriNBT.SEED, this.getId());
		new PlantStats().writeToNBT(tag);
		stack.setTagCompound(tag);
		return stack;
	}

    @Override
    public IBlockState getBlockStateForGrowthStage(int growthStage) {
        return getBlock().getStateFromMeta(growthStage);
    }

    /**
     * Gets a list of all possible fruit drops from this plant.
     * 
     * @return a list containing of all possible fruit drops.
     */
    @Override
    public abstract List<ItemStack> getAllFruits();

    /**
     * Returns a random fruit for this plant.
     *
     * @param rand a random for choosing the drop.
     * @return a random fruit dropped by the plant.
     */
    @Override
    public abstract ItemStack getRandomFruit(Random rand);

    /**
     * Returns an ArrayList with amount of random fruit stacks for this plant.
     * Gain is passed to allow for different fruits for higher gain levels
     *
     * @param gain the gain, as in number of drops.
     * @param rand a random for choosing the drop.
     * @return a list containing random fruit drops from this plant.
     */
    @Override
    public List<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while (amount > 0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    /**
     * Gets called right before a harvest attempt, player may be null if harvested by automation.
     * Should return false to prevent further processing.
     * World object and coordinates are passed in the case you would want to keep track of all planted crops and their status
     * 
     * @param world the current world of the plant being harvested.
     * @param pos the block position.
     * @param player the player attempting to harvest the plant.
     * @return true, by default.
     */
    @Override
    public boolean onHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    /**
     * Called right after the plant is added to a crop through planting, mutation, or spreading.
     * 
     * @param world the world the plant is in.
  * @param pos the block position.
     */
    @Override
    public void onSeedPlanted(World world, BlockPos pos) {}

    /**
     * Called right after the plant is removed from a crop, or a crop holding the plant is broken.
     * 
     * @param world the world the plant is in.
  * @param pos the block position.
     */
    @Override
    public void onPlantRemoved(World world, BlockPos pos) {}

    /**
     * Determines if the plant may be grown with Bonemeal.
     * 
     * @return if the plant may be bonemealed.
     */
    @Override
    public abstract boolean canBonemeal();

    @Override
    public IAdditionalCropData getInitialCropData(World world, BlockPos pos, IAgriCrop crop) {
        return null;
    }

    @Override
    public IAdditionalCropData readCropDataFromNBT(NBTTagCompound tag) {
        return null;
    }

    @Override
    public void onValidate(World world, BlockPos pos, IAgriCrop crop) {}

    @Override
    public void onInvalidate(World world, BlockPos pos, IAgriCrop crop) {}

    @Override
    public void onChunkUnload(World world, BlockPos pos, IAgriCrop crop) {}

    public final void setGrowthRequirement(IGrowthRequirement growthRequirement) {
        this.growthRequirement = growthRequirement;
    }

    @Override
    public final IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }

    protected abstract IGrowthRequirement initGrowthRequirement();

    /**
     * Attempts to apply a growth tick to the plant, if allowed.
     * Should return true to re-render the crop clientside.
     * 
     * @param world the world the plant is in.
     * @param pos the block position.
     * @param oldGrowthStage the current/old growth stage of the plant.
     */
    @Override
    public abstract void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage);

    /**
     * Determines if the plant is mature. That is, the plant's metadata matches {@link Constants#MATURE}.
     * 
     * @param world the world the plant is in.
  * @param pos the block position.
     * @return if the plant is mature.
     */
    @Override
    public boolean isMature(IBlockAccess world, BlockPos pos, IBlockState state) {
        return AgriProperties.GROWTHSTAGE.getValue(state) >= Constants.MATURE;
    }

    /**
     * Determines the height of the crop in float precision, as a function of the plant's metadata(growth stage).
     * Used to render and define the height of the selection bounding box
     * 
     * @param meta the growth stage of the plant (may range from 0 to {@link Constants#MATURE}).
     * @return the current height of the plant.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract float getHeight(int meta);

    /**
     * Determines how the plant is rendered.
     * 
     * @return false to render the plant as wheat (#), true to render as a flower (X).
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract RenderMethod getRenderMethod();

    /**
     * Gets the primary texture to render this plant with as a TextureAtlasSprite, note that this must be on the locationBlocksTextureMap
     * See RenderMethod for what primary texture should be returned
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getPrimaryPlantTexture(int growthStage);

    /**
     * Gets the secondary texture to render this plant with as a TextureAtlasSprite, note that this must be on the locationBlocksTextureMap
     * See RenderMethod for what secondary texture should be returned
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getSecondaryPlantTexture(int growthStage);

    /**
     * Retrieves information about the plant for the seed journal.
     * It's possible to pass an unlocalized String, the returned value will be localized if possible.
     * 
     * @return a string describing the plant for use by the seed journal.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public abstract String getInformation();

    /**
     * A function to render the crop. Called when the plant is rendered.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon) {
        //The quads returned from this method are added to the tessellator,
        // however the plant renderer directly adds them to the tessellator, so an empty list is returned
        if(textureToIcon instanceof ITessellator) {
            PlantRenderer.renderPlant((ITessellator) textureToIcon, this, growthStage);
        }
        return Collections.emptyList();
    }
}
