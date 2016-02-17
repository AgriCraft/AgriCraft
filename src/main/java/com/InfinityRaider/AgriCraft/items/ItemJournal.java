package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.api.v1.IJournal;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.AgriCraftNBT;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemJournal extends ItemBase implements IJournal {
	
    public ItemJournal() {
        super("journal");
        this.setMaxStackSize(1);
    }

    //this has to return true to make it so the getContainerItem method gets called when this item is used in a recipe
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    //when this item is used in a crafting recipe it is replaced by the item return by this method
    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack journal = player.getCurrentEquippedItem();
        if(journal.hasTagCompound()) {
            NBTTagCompound tag = journal.getTagCompound();
            if(tag.hasKey(AgriCraftNBT.DISCOVERED_SEEDS)) {
                NBTTagList list = tag.getTagList(AgriCraftNBT.DISCOVERED_SEEDS, 10);
                NBTHelper.clearEmptyStacksFromNBT(list);
                tag.setTag(AgriCraftNBT.DISCOVERED_SEEDS, list);
            }
        }
        if(world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.journalID, world, player.serverPosX, player.serverPosY, player.serverPosZ);
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        int nr = 0;
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(AgriCraftNBT.DISCOVERED_SEEDS)) {
            nr = stack.getTagCompound().getTagList(AgriCraftNBT.DISCOVERED_SEEDS, 10).tagCount();
        }
        list.add(StatCollector.translateToLocal("agricraft_tooltip.discoveredSeeds")+": "+nr);
    }

    private NBTTagList getDiscoveredSeedsTaglist(ItemStack journal) {
        //check if the journal has AgriCraftNBT and if it doesn't, create a new one
        if(!journal.hasTagCompound()) {
            journal.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = journal.getTagCompound();
        //check if the AgriCraftNBT TAG has a list of discovered seeds and if it doesn't, create a new one
        NBTTagList list;
        if(tag.hasKey(AgriCraftNBT.DISCOVERED_SEEDS)) {
            list = tag.getTagList(AgriCraftNBT.DISCOVERED_SEEDS, 10);
            NBTHelper.clearEmptyStacksFromNBT(list);
        }
        else {
            list = new NBTTagList();
        }
        return list;
    }

    public void addEntry(ItemStack journal, ItemStack newEntry) {
        if(journal==null || journal.getItem()==null || !CropPlantHandler.isValidSeed(newEntry)) {
            return;
        }
        NBTTagList list = getDiscoveredSeedsTaglist(journal);
        NBTTagCompound tag = journal.getTagCompound();
        //add the ANALYZED SEED to the AgriCraftNBT TAG list if it doesn't already have it
        if(!isSeedDiscovered(journal, newEntry)) {
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack write = newEntry.copy();
            write.stackSize = 1;
            write.setTagCompound(null);
            write.writeToNBT(seedTag);
            list.appendTag(seedTag);
        }
        NBTHelper.sortStacks(list);
        //add the AgriCraftNBT TAG to the journal
        tag.setTag(AgriCraftNBT.DISCOVERED_SEEDS, list);
    }

    public boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
        if(journal==null || journal.getItem()==null || !CropPlantHandler.isValidSeed(seed)) {
            return false;
        }
        return NBTHelper.listContainsStack(getDiscoveredSeedsTaglist(journal), seed);
    }

    public ArrayList<ItemStack> getDiscoveredSeeds(ItemStack journal) {
        ArrayList<ItemStack> seeds = new ArrayList<>();
        NBTTagCompound tag = null;
        if (journal != null && journal.stackSize > 0 && journal.getItem() instanceof ItemJournal && journal.hasTagCompound()) {
            tag = journal.getTagCompound();
        }
        if(tag != null) {
            if (tag.hasKey(AgriCraftNBT.DISCOVERED_SEEDS)) {
                NBTTagList tagList = tag.getTagList(AgriCraftNBT.DISCOVERED_SEEDS, 10);      //10 for tagCompound
                for (int i = 0; i < tagList.tagCount(); i++) {
                    ItemStack seed = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
                    if(CropPlantHandler.isValidSeed(seed)) {
                        seeds.add(seed);
                    }
                }
            }
        }
        return seeds;
    }
	
}
