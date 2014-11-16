package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedAnalyzerID, world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }
}
