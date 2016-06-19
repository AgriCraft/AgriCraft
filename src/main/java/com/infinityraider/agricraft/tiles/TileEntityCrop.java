package com.infinityraider.agricraft.tiles;

import com.infinityraider.agricraft.api.v3.misc.IDebuggable;
import com.infinityraider.agricraft.api.v3.crop.IAdditionalCropData;
import com.infinityraider.agricraft.api.v3.items.ITrowel;
import com.infinityraider.agricraft.compat.CompatibilityHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.CrossOverResult;
import com.infinityraider.agricraft.farming.mutation.MutationEngine;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;
import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.v3.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v3.stat.IAgriStat;
import com.infinityraider.agricraft.api.v3.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v3.fertiliser.IAgriFertiliser;

public class TileEntityCrop extends TileEntityBase implements IAgriCrop, IDebuggable {

	public static final String NAME = "crops";

	private boolean crossCrop = false;
	private boolean weed = false;

	private @Nonnull
	IAgriStat stats = new PlantStats();
	private IAgriPlant plant;
	private IAdditionalCropData data;

	private final MutationEngine mutationEngine;

	public TileEntityCrop() {
		this.mutationEngine = new MutationEngine(this);
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

	// =========================================================================
	// IPlantProvider Methods
	// =========================================================================
	@Override
	public boolean hasPlant() {
		return this.plant != null;
	}

	@Override
	public IAgriPlant getPlant() {
		return this.plant;
	}

	// =========================================================================
	// IStatProvider Methods
	// =========================================================================
	@Override
	public boolean hasStat() {
		return true;//this.stats != null;
	}

	@Nonnull
	@Override
	public IAgriStat getStat() {
		return this.stats;
	}

	// =========================================================================
	// Misc.
	// =========================================================================
	@Override
	public boolean isCrossCrop() {
		return crossCrop;
	}

	@Override
	public void setCrossCrop(boolean status) {
		if (status != this.crossCrop) {
			this.crossCrop = status;
			if (!worldObj.isRemote && crossCrop) {
				SoundType type = Blocks.planks.getStepSound();
				worldObj.playSound(null, (double) ((float) xCoord() + 0.5F), (double) ((float) yCoord() + 0.5F), (double) ((float) zCoord() + 0.5F), type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
			}
			this.markDirty();
			this.markForUpdate();
		}
	}

	/*
	 * get growthrate
	 */
	public int getGrowthRate() {
		// TODO: update!
		return AgriCraftConfig.weedGrowthRate;
	}

	@Override
	public int getGrowthStage() {
		return worldObj.getBlockState(getPos()).getValue(AgriCraftProperties.GROWTHSTAGE);
	}

	@Override
	public void setGrowthStage(int stage) {
		if (this.hasPlant() || this.hasWeed()) {
			stage &= Constants.MATURE;
			IBlockState state = worldObj.getBlockState(pos);
			state.withProperty(AgriCraftProperties.GROWTHSTAGE, stage);
			this.worldObj.setBlockState(pos, state, 3);
		}
	}

	/*
	 * check to see if a SEED can be planted
	 */
	@Override
	public boolean canPlant() {
		return !this.hasPlant() && !this.hasWeed() && !this.isCrossCrop();
	}

	/*
	 * sets the plant in the crop
	 */
	public void setPlant(@Nonnull IAgriStat stats, IAgriPlant plant) {
		if (setPlant(plant)) {
			this.stats = stats;
		}
	}

	// =========================================================================
	// IPlantAcceptor Methods
	// =========================================================================
	@Override
	public boolean acceptsPlant(IAgriPlant plant) {
		return !this.hasPlant();
	}

	@Override
	public boolean setPlant(IAgriPlant plant) {
		if ((!this.crossCrop) && (!this.hasPlant()) && (plant != null)) {
			this.plant = plant;
			this.setGrowthStage(0);
			plant.onSeedPlanted(worldObj, pos);
			IAdditionalCropData new_data = plant.getInitialCropData(worldObj, getPos(), this);
			if (new_data != null) {
				this.data = new_data;
			}
			this.markForUpdate();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IAgriPlant removePlant() {
		IAgriPlant oldPlant = getPlant();
		this.setGrowthStage(0);
		this.stats = new PlantStats();
		this.plant = null;
		this.markForUpdate();
		if (oldPlant != null) {
			oldPlant.onPlantRemoved(worldObj, pos);
		}
		this.data = null;
		return oldPlant;
	}
	
	// =========================================================================
	// IStatAcceptor Methods
	// =========================================================================
	@Override
	public boolean acceptsStat(IAgriStat stat) {
		return true;
	}

	@Override
	public boolean setStat(IAgriStat stat) {
		if (stat != null) {
			this.stats = stat;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IAgriStat removeStat() {
		IAgriStat old = this.stats;
		this.stats = new PlantStats();
		return old;
	}

	/*
	 * check if the crop is fertile
	 */
	@Override
	public boolean isFertile() {
		return this.weed || worldObj.isAirBlock(this.getPos().add(0, 1, 0)) && plant.getGrowthRequirement().canGrow(this.worldObj, pos);
	}

	/*
	 * gets the height of the crop
	 */
	@SideOnly(Side.CLIENT)
	public float getCropHeight() {
		return hasPlant() ? plant.getHeight(getBlockMetadata()) : Constants.UNIT * 13;
	}

	/*
	 * check if bonemeal can be applied
	 */
	@Override
	public boolean canBonemeal() {
		if (this.crossCrop) {
			return AgriCraftConfig.bonemealMutation;
		} else if (this.hasPlant() && !this.isMature()) {
			return this.isFertile() && plant.canBonemeal();
		} else {
			return false;
		}
	}

	/**
	 * check the block if the plant is mature
	 */
	@Override
	public boolean isMature() {
		return getGrowthStage() >= Constants.MATURE;
	}

	/*
	 * gets the fruits for this plant
	 */
	public List<ItemStack> getFruits() {
		return plant.getFruitsOnHarvest(stats.getGain(), worldObj.rand);
	}

	/*
	 * allow harvesting
	 */
	public boolean allowHarvest(EntityPlayer player) {
		return hasPlant() && isMature() && plant.onHarvest(worldObj, pos, worldObj.getBlockState(getPos()), player);
	}

	// =========================================================================
	// IWeedable Methods
	// =========================================================================
	@Override
	public double getWeedSpawnChance() {
		if (this.hasPlant()) {
			return AgriCraftConfig.weedsWipePlants ? ((double) (10 - stats.getStrength())) / 10 : 0;
		} else {
			return this.weed ? 0 : 1;
		}
	}

	@Override
	public boolean hasWeed() {
		return weed;
	}

	@Override
	public boolean spawnWeed(Random rand) {
		if (!weed && rand.nextDouble() < getWeedSpawnChance()) {
			this.crossCrop = false;
			this.weed = true;
			this.removePlant();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean spreadWeed(Random rand) {
		List<IAgriCrop> neighbours = this.getNeighbours();
		for (IAgriCrop crop : neighbours) {
			if (crop != null && crop.spawnWeed(rand)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean clearWeed() {
		if (this.hasWeed()) {
			this.setGrowthStage(0);
			this.weed = false;
			return true;
		} else {
			return false;
		}
	}

	// =========================================================================
	// Trowel Code
	// =========================================================================
	//trowel usage
	public void onTrowelUsed(ITrowel trowel, ItemStack trowelStack) {
		if (this.hasPlant()) {
			if (!trowel.hasSeed(trowelStack)) {
				trowel.setSeed(trowelStack, this.getSeed(), getGrowthStage());
				this.removePlant();
			}
		} else if (!this.hasWeed() && !this.crossCrop) {
			if (trowel.hasSeed(trowelStack)) {
				ItemStack seed = trowel.getSeed(trowelStack);
				int growthStage = trowel.getGrowthStage(trowelStack);
				NBTTagCompound tag = seed.getTagCompound();

				this.setPlant(
						stats,
						CropPlantHandler.getPlantFromStack(seed)
				);

				this.setGrowthStage(growthStage);
				trowel.clearSeed(trowelStack);
			}
		}
	}

	// =========================================================================
	// IFertilizable Methods
	// =========================================================================
	@Override
	public boolean acceptsFertiliser(IAgriFertiliser fertiliser) {
		if (this.crossCrop) {
			return AgriCraftConfig.bonemealMutation && fertiliser.canTriggerMutation();
		}
		if (this.hasWeed()) {
			return true;
		}
		if (this.hasPlant()) {
			return fertiliser.isFertiliserAllowed(plant.getTier());
		}
		return false;
	}

	//when fertiliser is applied
	@Override
	public boolean applyFertiliser(IAgriFertiliser fertiliser, Random rand) {
		if (fertiliser.hasSpecialBehaviour()) {
			fertiliser.onFertiliserApplied(getWorld(), getPos(), rand);
			return true;
		}
		if (this.hasPlant() || this.hasWeed()) {
			((BlockCrop) AgriCraftBlocks.blockCrop).grow(getWorld(), rand, getPos(), getWorld().getBlockState(getPos()));
			return true;
		} else if (this.isCrossCrop() && AgriCraftConfig.bonemealMutation) {
			this.crossOver();
			return true;
		}
		return false;
	}

	@Override
	public boolean harvest(@Nullable EntityPlayer player) {
		return ((BlockCrop) getWorld().getBlockState(pos).getBlock()).harvest(getWorld(), getPos(), getWorld().getBlockState(getPos()), player, this);
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
	}

	@Override
	public IAdditionalCropData getAdditionalCropData() {
		return this.data;
	}

	@Override
	public void validate() {
		super.validate();
		if (this.hasPlant()) {
			plant.onValidate(worldObj, pos, this);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (this.hasPlant()) {
			plant.onInvalidate(worldObj, pos, this);
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (this.hasPlant()) {
			plant.onChunkUnload(worldObj, pos, this);
		}
	}

	//this saves the data on the tile entity
	@Override
	public void writeTileNBT(NBTTagCompound tag) {
		stats.writeToNBT(tag);
		tag.setBoolean(AgriCraftNBT.CROSS_CROP, crossCrop);
		tag.setBoolean(AgriCraftNBT.WEED, weed);
		CropPlantHandler.writePlantToNBT(tag, plant);
		if (getAdditionalCropData() != null) {
			tag.setTag(AgriCraftNBT.INVENTORY, getAdditionalCropData().writeToNBT());
		}
		//AgriCore.getLogger("Plant-Tag").debug("Write Tag: {0}", tag);
	}

	//this loads the saved data for the tile entity
	@Override
	public void readTileNBT(NBTTagCompound tag) {
		this.stats = new PlantStats(tag);
		this.crossCrop = tag.getBoolean(AgriCraftNBT.CROSS_CROP);
		this.weed = tag.getBoolean(AgriCraftNBT.WEED);
		this.plant = CropPlantHandler.readPlantFromNBT(tag);
		if (tag.hasKey(AgriCraftNBT.INVENTORY) && this.plant != null) {
			this.data = plant.readCropDataFromNBT(tag.getCompoundTag(AgriCraftNBT.INVENTORY));
		}
		//AgriCore.getLogger("Plant-Tag").debug("Read Tag: {0}", tag);
	}

	/**
	 * Apply a GROWTH increment
	 */
	public void applyGrowthTick() {
		int meta = getGrowthStage();
		if (hasPlant()) {
			plant.onAllowedGrowthTick(worldObj, pos, meta);
		}
		IBlockState state = getWorld().getBlockState(getPos());
		if (hasWeed() || !plant.isMature(getWorld(), pos, state)) {
			setGrowthStage(meta + 1);
			CompatibilityHandler.getInstance().announceGrowthTick(getWorld(), getPos(), state);
		}
	}

	/**
	 * the code that makes the crop cross with neighboring crops
	 */
	public void crossOver() {
		mutationEngine.executeCrossOver();
	}

	/*
	 * Called by the mutation engine to apply the result of a cross over
	 */
	public void applyCrossOverResult(CrossOverResult result) {
		crossCrop = false;
		setPlant(result.getStats(), result.getPlant());
	}

	/**
	 * @return a list with all neighbours of type <code>TileEntityCrop</code> in
	 * the NORTH, SOUTH, EAST and WEST DIRECTION
	 */
	@Override
	public List<IAgriCrop> getNeighbours() {
		List<IAgriCrop> neighbours = new ArrayList<>();
		addNeighbour(neighbours, AgriForgeDirection.NORTH);
		addNeighbour(neighbours, AgriForgeDirection.SOUTH);
		addNeighbour(neighbours, AgriForgeDirection.EAST);
		addNeighbour(neighbours, AgriForgeDirection.WEST);
		return neighbours;
	}

	private void addNeighbour(List<IAgriCrop> neighbours, AgriForgeDirection direction) {
		TileEntity te = worldObj.getTileEntity(getPos().add(direction.offsetX, direction.offsetY, direction.offsetZ));
		if (te == null || !(te instanceof TileEntityCrop)) {
			return;
		}
		neighbours.add((TileEntityCrop) te);
	}

	/**
	 * @return a list with only mature neighbours of type
	 * <code>TileEntityCrop</code>
	 */
	@Override
	public List<IAgriCrop> getMatureNeighbours() {
		List<IAgriCrop> neighbours = getNeighbours();
		for (Iterator<IAgriCrop> iterator = neighbours.iterator(); iterator.hasNext();) {
			IAgriCrop crop = iterator.next();
			if (!crop.hasPlant() || !crop.isMature()) {
				iterator.remove();
			}
		}
		return neighbours;
	}

	@Override
	public void addDebugInfo(List<String> list) {
		list.add("CROP:");
		if (this.crossCrop) {
			list.add(" - This is a crosscrop");
		} else if (this.hasPlant()) {
			list.add(" - This crop has a plant");
			list.add(" - Plant: " + this.plant.getPlantName());
			list.add(" - Id: " + this.plant.getId());
			list.add(" - Tier: " + plant.getTier());
			list.add(" - Meta: " + this.getBlockMetadata());
			list.add(" - Growth: " + this.stats.getGrowth());
			list.add(" - Gain: " + this.stats.getGain());
			list.add(" - Strength: " + this.stats.getStrength());
			list.add(" - Fertile: " + this.isFertile());
			list.add(" - Mature: " + this.isMature());
		} else if (this.weed) {
			list.add(" - This crop has weeds");
			list.add(" - Meta: " + this.getBlockMetadata());
		} else {
			list.add(" - This crop has no plant");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addWailaInformation(List information) {
		if (this.hasPlant()) {
			//Add the SEED name.
			information.add(I18n.translateToLocal("agricraft_tooltip.seed") + ": " + this.plant.getSeedName());
			//Add the GROWTH.
			if (this.isMature()) {
				information.add(I18n.translateToLocal("agricraft_tooltip.growthStage") + ": " + I18n.translateToLocal("agricraft_tooltip.mature"));
			} else {
				information.add(I18n.translateToLocal("agricraft_tooltip.growthStage") + ": " + ((int) (100.0 * this.getBlockMetadata() / Constants.MATURE) + "%"));
			}
			//Add the ANALYZED data.
			if (this.stats.isAnalyzed()) {
				this.stats.addStats(information);
			} else {
				information.add(I18n.translateToLocal("agricraft_tooltip.analyzed"));
			}
			//Add the fertility information.
			information.add(I18n.translateToLocal(this.isFertile() ? "agricraft_tooltip.fertile" : "agricraft_tooltip.notFertile"));
		} else if (this.hasWeed()) {
			information.add(I18n.translateToLocal("agricraft_tooltip.weeds"));
		} else {
			information.add(I18n.translateToLocal("agricraft_tooltip.empty"));
		}
	}
}
