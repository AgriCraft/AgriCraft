package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
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

import java.util.List;

public class ItemJournal extends ModItem {
    public ItemJournal() {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setMaxStackSize(1);
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
}
