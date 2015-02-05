package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.farming.SoilWhitelist;
import com.InfinityRaider.AgriCraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemCrop extends ModItem {
    private static Block[] soils = {net.minecraft.init.Blocks.sand, net.minecraft.init.Blocks.soul_sand, net.minecraft.init.Blocks.mycelium};
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
            if (isSoilValid(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)) && world.getBlock(x, y + 1, z).getMaterial()== Material.air && side == 1) {
                world.setBlock(x, y + 1, z, Blocks.blockCrop);
                stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;
                return false;
            }
        }
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }

    public static boolean isSoilValid(Block soil, int soilMeta) {
        return Arrays.asList(soils).contains(soil) || SoilWhitelist.isSoilFertile(soil, soilMeta);
    }

    public static void addBlockToSoils(Block block) {
        ArrayList<Block> list = new ArrayList<Block>();
        list.addAll(Arrays.asList(soils));
        list.add(block);
        soils = list.toArray(new Block[soils.length]);
    }
}
