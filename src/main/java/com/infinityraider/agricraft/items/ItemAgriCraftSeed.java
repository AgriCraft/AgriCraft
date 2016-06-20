package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.ISeedHandler;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

public class ItemAgriCraftSeed extends ItemBase implements ISeedHandler {

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * constructor will create the seed for you
	 */
	public ItemAgriCraftSeed() {
		super("agri_seed", true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (IAgriPlant plant : CropPlantHandler.getPlants()) {
			list.add(CropPlantHandler.getSeed(plant));
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final IAgriPlant plant = CropPlantHandler.getPlantFromStack(stack);
		return (plant == null ? "Generic Seeds" : plant.getSeedName());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == AgriCraftBlocks.blockCrop) {
			AgriCore.getLogger("AgriCraft").debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	public List<String> getIgnoredNBT() {
		List<String> tags = super.getIgnoredNBT();
		tags.add(PlantStats.NBT_ANALYZED);
		tags.add(PlantStats.NBT_GROWTH);
		tags.add(PlantStats.NBT_GAIN);
		tags.add(PlantStats.NBT_STRENGTH);
		return tags;
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemAgriCraftSeed;
	}

	@Override
	public AgriSeed getSeed(ItemStack stack) {
		IAgriPlant plant = CropPlantHandler.getPlantFromStack(stack);
		IAgriStat stat = new PlantStats(stack);
		if (plant != null) {
			return new AgriSeed(plant, stat);
		} else {
			return null;
		}
	}

}
