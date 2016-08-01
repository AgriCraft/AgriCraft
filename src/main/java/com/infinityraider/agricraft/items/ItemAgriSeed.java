package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.init.AgriBlocks;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.infinitylib.render.item.IAutoRenderedItem;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.utility.NBTHelper;
import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAgriSeed extends ItemBase implements IAgriAdapter<AgriSeed>, IAutoRenderedItem {

	/**
	 * This constructor shouldn't be called from anywhere except from the
	 * BlockModPlant public constructor, if you create a new BlockModPlant, its
	 * constructor will create the seed for you
	 */
	public ItemAgriSeed() {
		super("agri_seed", false);
		this.setCreativeTab(AgriTabs.TAB_AGRICRAFT_SEED);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		final PlantStats baseStat = new PlantStats();
		for (IAgriPlant plant : PlantRegistry.getInstance().getPlants()) {
			ItemStack stack = new ItemStack(item);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(AgriNBT.SEED, plant.getId());
			baseStat.writeToNBT(tag);
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
		final AgriSeed seed = SeedRegistry.getInstance().getValue(stack);
		return (seed == null ? "Generic Seeds" : seed.getPlant().getSeedName());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getBlockState(pos).getBlock() == AgriBlocks.getInstance().CROP) {
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
	public boolean accepts(Object obj) {
		NBTTagCompound tag = NBTHelper.asTag(obj);
		return tag != null && tag.hasKey(AgriNBT.SEED) && StatRegistry.getInstance().hasAdapter(tag);
	}

	@Override
	public AgriSeed getValue(Object obj) {
		NBTTagCompound tag = NBTHelper.asTag(obj);
		if (tag == null) {
			return null;
		}
		IAgriPlant plant = PlantRegistry.getInstance().getPlant(tag.getString(AgriNBT.SEED));
		IAgriStat stat = StatRegistry.getInstance().getValue(tag);
		if (plant != null && stat != null) {
			return new AgriSeed(plant, stat);
		} else {
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelId(ItemStack stack) {
		AgriSeed seed = SeedRegistry.getInstance().getValue(stack);
		return seed == null ? "" : seed.getPlant().getId();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getBaseTexture(ItemStack stack) {
		AgriSeed seed = SeedRegistry.getInstance().getValue(stack);
		return seed == null ? "agricraft:items/seed_unknown" : seed.getPlant().getSeedTexture().toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<ResourceLocation> getAllTextures() {
		final List<IAgriPlant> plants = PlantRegistry.getInstance().getPlants();
		final List<ResourceLocation> textures = new ArrayList<>(plants.size());
		textures.add(new ResourceLocation("agricraft:items/seed_unknown"));
		for (IAgriPlant p : PlantRegistry.getInstance().getPlants()) {
			textures.add(p.getSeedTexture());
		}
		return textures;
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

}
