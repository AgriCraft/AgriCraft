package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.init.Blocks;
import mods.railcraft.common.blocks.hidden.BlockHidden;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCrop extends ModItem {
    public ItemCrop() {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (world.getBlock(x, y, z)==net.minecraft.init.Blocks.farmland && (world.getBlock(x, y + 1, z)==net.minecraft.init.Blocks.air || (LoadedMods.railcraft && world.getBlock(x, y + 1, z)instanceof BlockHidden)) && side == 1) {
                world.setBlock(x, y + 1, z, Blocks.blockCrop);
                stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;
                return false;
            }
        }
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }
}
