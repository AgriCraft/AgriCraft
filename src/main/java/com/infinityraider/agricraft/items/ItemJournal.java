package com.infinityraider.agricraft.items;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.IJournal;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemJournal extends ItemBase implements IJournal {

	public ItemJournal() {
		super("journal", true);
		this.setMaxStackSize(1);
	}

	//this has to return true to make it so the getContainerItem method gets called when this item is used in a recipe
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	//when this item is used in a crafting recipe it is replaced by the item return by this method
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return itemStack.copy();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		ItemStack journal = player.getHeldItem(hand);
		if (journal.hasTagCompound()) {
			NBTTagCompound tag = journal.getTagCompound();
			if (tag.hasKey(AgriCraftNBT.DISCOVERED_SEEDS)) {
				NBTTagList list = tag.getTagList(AgriCraftNBT.DISCOVERED_SEEDS, 10);
				NBTHelper.clearEmptyStacksFromNBT(list);
				tag.setTag(AgriCraftNBT.DISCOVERED_SEEDS, list);
			}
		}
		if (world.isRemote) {
			player.openGui(AgriCraft.instance, GuiHandler.journalID, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		list.add(I18n.translateToLocal("agricraft_tooltip.discoveredSeeds") + ": " + getDiscoveredSeedIds(stack).size());
	}

	private List<String> getDiscoveredSeedIds(ItemStack journal) {
		if (journal == null) {
			return new ArrayList<>();
		}
		//check if the journal has AgriCraftNBT and if it doesn't, create a new one
		if (!journal.hasTagCompound()) {
			journal.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = journal.getTagCompound();
		return Arrays.asList(tag.getString(AgriCraftNBT.DISCOVERED_SEEDS).split(";"));
	}

	@Override
	public void addEntry(ItemStack journal, ItemStack newEntry) {
		if (journal == null || journal.getItem() == null || !CropPlantHandler.isValidSeed(newEntry)) {
			return;
		}
		List<String> seeds = getDiscoveredSeedIds(journal);
		String id = newEntry.getTagCompound().getString(AgriCraftNBT.ID);
		if (!seeds.contains(id)) {
			NBTTagCompound tag = journal.getTagCompound();
			String old = tag.getString(AgriCraftNBT.DISCOVERED_SEEDS);
			tag.setString(AgriCraftNBT.DISCOVERED_SEEDS, old + id + ";");
			//AgriCore.getLogger("AgriCraft").debug("Discovered Plants: {0}", getDiscoveredSeedIds(journal));
			//AgriCore.getLogger("AgriCraft").debug("Plants: {0}", CropPlantHandler.getPlantIds());
			journal.setTagCompound(tag);
		}
	}

	@Override
	public boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
		if (journal == null || !CropPlantHandler.isValidSeed(seed)) {
			return false;
		}
		final String id = seed.getTagCompound().getString(AgriCraftNBT.ID);
		return getDiscoveredSeedIds(journal).contains(id);
	}

	@Override
	public List<ItemStack> getDiscoveredSeeds(ItemStack journal) {
		List<ItemStack> list = new ArrayList<>();
		if (journal != null && journal.hasTagCompound()) {
			for (String id : getDiscoveredSeedIds(journal)) {
				ItemStack seed = CropPlantHandler.getSeed(CropPlantHandler.getPlant(id));
				if (seed != null) {
					list.add(seed);
				}
			}
		}
		return list;
	}

}
