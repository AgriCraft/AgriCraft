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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemAgriCraftSeed extends ItemBase {
	
	private static final ItemAgriCraftSeed instance = new ItemAgriCraftSeed();

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * constructor will create the seed for you
	 */
	private ItemAgriCraftSeed() {
		super("agri_seed", true);
	}

	public static ItemAgriCraftSeed getInstance() {
		return instance;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (IAgriCraftPlant plant : CropPlantHandler.getPlants()) {
			list.add(CropPlantHandler.getSeed(plant));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final IAgriCraftPlant plant = CropPlantHandler.getPlantFromStack(stack);
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
}
