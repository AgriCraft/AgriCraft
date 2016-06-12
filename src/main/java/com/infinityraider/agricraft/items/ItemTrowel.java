package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IAgriCraftStats;

public class ItemTrowel extends ItemBase implements ITrowel {

	public ItemTrowel() {
		super("trowel", true, "", "full");
		this.maxStackSize = 1;
	}

	//I'm overriding this just to be sure
	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	//this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.PASS;   //return PASS or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
	}

	@Override
	public boolean hasSeed(ItemStack trowel) {
		if (trowel == null || trowel.getItem() == null || trowel.getTagCompound() == null) {
			return false;
		}
		return CropPlantHandler.readPlantFromNBT(trowel.getTagCompound().getCompoundTag(AgriCraftNBT.SEED)) != null;
	}

	@Override
	public boolean isSeedAnalysed(ItemStack trowel) {
		return trowel != null && new PlantStats(trowel).isAnalyzed();
	}

	@Override
	public void analyze(ItemStack trowel) {
		if (this.hasSeed(trowel)) {
			NBTTagCompound tag;
			PlantStats stats;
			if (trowel.hasTagCompound()) {
				tag = trowel.getTagCompound();
				stats = new PlantStats(tag);
			} else {
				tag = new NBTTagCompound();
				stats = new PlantStats();
			}
			stats.analyze();
			stats.writeToNBT(tag);
			trowel.setTagCompound(tag);
		}
	}

	@Override
	public ItemStack getSeed(ItemStack trowel) {
		if (!this.hasSeed(trowel)) {
			return null;
		}
		NBTTagCompound tag = trowel.getTagCompound();
		IAgriCraftPlant plant = CropPlantHandler.readPlantFromNBT(tag.getCompoundTag(AgriCraftNBT.SEED));
		if (plant == null) {
			return null;
		}
		PlantStats stats = new PlantStats(tag);
		NBTTagCompound seedTag = new NBTTagCompound();
		stats.writeToNBT(seedTag);
		ItemStack seed = plant.getSeed();
		seed.setTagCompound(seedTag);
		return seed;
	}

	@Override
	public int getGrowthStage(ItemStack trowel) {
		if (!this.hasSeed(trowel)) {
			return -1;
		}
		return trowel.getTagCompound().getShort(AgriCraftNBT.MATERIAL_META);
	}

	@Override
	public boolean setSeed(ItemStack trowel, ItemStack seed, int growthStage) {
		if (this.hasSeed(trowel)) {
			return false;
		}
		IAgriCraftPlant plant = CropPlantHandler.getPlantFromStack(seed);
		if (plant == null) {
			return false;
		}
		PlantStats stats = new PlantStats(seed);
		NBTTagCompound tag = new NBTTagCompound();
		stats.writeToNBT(tag);
		tag.setTag(AgriCraftNBT.SEED, CropPlantHandler.writePlantToNBT(plant));
		tag.setShort(AgriCraftNBT.MATERIAL_META, (short) growthStage);
		trowel.setTagCompound(tag);
		trowel.setItemDamage(1);
		return true;
	}

	@Override
	public void clearSeed(ItemStack trowel) {
		trowel.setTagCompound(null);
		trowel.setItemDamage(0);
	}

	@Override
	public IAgriCraftStats getStats(ItemStack trowel) {
		return new PlantStats(getSeed(trowel));
	}

}
