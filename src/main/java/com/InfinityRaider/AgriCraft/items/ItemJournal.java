package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.api.v2.IJournal;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemJournal extends ItemAgricraft implements IJournal {
    public ItemJournal() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.journal;
    }

    //this has to return true to make it so the getContainerItem method gets called when this item is used in a recipe
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    //when this item is used in a crafting grid, it stays in the grid
    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack)
    {
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
            NBTTagCompound tag = journal.stackTagCompound;
            if(tag.hasKey(Names.NBT.discoveredSeeds)){
                NBTTagList list = tag.getTagList(Names.NBT.discoveredSeeds, 10);
                NBTHelper.clearEmptyStacksFromNBT(list);
                tag.setTag(Names.NBT.discoveredSeeds, list);
            }
        }
        if(world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.journalID, world, player.serverPosX, player.serverPosY, player.serverPosZ);
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        int nr = 0;
        if(stack.hasTagCompound() && stack.stackTagCompound.hasKey(Names.NBT.discoveredSeeds)) {
            nr = stack.stackTagCompound.getTagList(Names.NBT.discoveredSeeds, 10).tagCount();
        }
        list.add(StatCollector.translateToLocal("agricraft_tooltip.discoveredSeeds")+": "+nr);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }

    private NBTTagList getDiscoveredSeedsTaglist(ItemStack journal) {
        //check if the journal has NBT and if it doesn't, create a new one
        if(!journal.hasTagCompound()) {
            journal.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = journal.stackTagCompound;
        //check if the NBT tag has a list of discovered seeds and if it doesn't, create a new one
        NBTTagList list;
        if(tag.hasKey(Names.NBT.discoveredSeeds)) {
            list = tag.getTagList(Names.NBT.discoveredSeeds, 10);
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
        NBTTagCompound tag = journal.stackTagCompound;
        //add the analyzed seed to the NBT tag list if it doesn't already have it
        if(!isSeedDiscovered(journal, newEntry)) {
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack write = newEntry.copy();
            write.stackSize = 1;
            write.stackTagCompound = null;
            write.writeToNBT(seedTag);
            list.appendTag(seedTag);
        }
        NBTHelper.sortStacks(list);
        //add the NBT tag to the journal
        tag.setTag(Names.NBT.discoveredSeeds, list);
    }

    public boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
        if(journal==null || journal.getItem()==null || !CropPlantHandler.isValidSeed(seed)) {
            return false;
        }
        return NBTHelper.listContainsStack(getDiscoveredSeedsTaglist(journal), seed);
    }

    public ArrayList<ItemStack> getDiscoveredSeeds(ItemStack journal) {
        ArrayList<ItemStack> seeds = new ArrayList<ItemStack>();
        NBTTagCompound tag = null;
        if (journal != null && journal.stackSize > 0 && journal.getItem() instanceof ItemJournal && journal.hasTagCompound()) {
            tag = journal.getTagCompound();
        }
        if(tag != null) {
            if (tag.hasKey(Names.NBT.discoveredSeeds)) {
                NBTTagList tagList = tag.getTagList(Names.NBT.discoveredSeeds, 10);      //10 for tagCompound
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
