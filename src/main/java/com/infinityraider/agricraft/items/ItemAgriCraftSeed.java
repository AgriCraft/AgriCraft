package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.init.AgriBlocks;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.infinityraider.agricraft.farming.PlantStats;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.ISeedHandler;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.v1.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.utility.StackHelper;

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
		for (IAgriPlant plant : PlantRegistry.getInstance().getPlants()) {
			ItemStack stack = new ItemStack(item);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(AgriNBT.SEED, plant.getId());
			stack.setTagCompound(tag);
			list.add(stack);
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final AgriSeed seed = SeedRegistry.getInstance().getSeed(stack);
		return (seed == null ? "Generic Seeds" : seed.getPlant().getSeedName());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == AgriBlocks.blockCrop) {
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
		tags.add(PlantStats.NBT_META);
		return tags;
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemAgriCraftSeed;
	}

	@Override
	public AgriSeed getSeed(ItemStack stack) {
		if (stack != null && stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			IAgriPlant plant = PlantRegistry.getInstance().getPlant(tag.getString(AgriNBT.SEED));
			IAgriStat stat = new PlantStats(tag);
			if (plant != null) {
				return new AgriSeed(plant, stat);
			}
		}
		return null;
	}

}
