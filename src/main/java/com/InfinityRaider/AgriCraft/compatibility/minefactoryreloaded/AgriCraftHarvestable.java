package com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AgriCraftHarvestable implements IFactoryHarvestable {
    private BlockCrop crop;
    public AgriCraftHarvestable() {
        this.crop = (BlockCrop) Blocks.blockCrop;
    }

    @Override
    public Block getPlant () {
        return this.crop;
    }

    @Override
    public HarvestType getHarvestType () {
        return HarvestType.Normal;
    }

    @Override
    public boolean breakBlock () {
        return false;
    }

    @Override
    public boolean canBeHarvested (World world, Map<String, Boolean> harvesterSettings, int x, int y, int z) {
        return this.crop.isMature(world, x, y, z);
    }

    @Override
    public List<ItemStack> getDrops (World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if (crop.hasPlant() && crop.isMature()) {
                items.addAll(crop.getFruits());
            }
        }
        return items;
    }

    @Override
    public void preHarvest (World world, int x, int y, int z) {
    }

    @Override
    public void postHarvest (World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if(block==null || !(block instanceof BlockCrop)) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return;
        }
        world.setBlockMetadataWithNotify(x, y, z, 2, 3);
        ((TileEntityCrop) te).markForUpdate();
    }
}
