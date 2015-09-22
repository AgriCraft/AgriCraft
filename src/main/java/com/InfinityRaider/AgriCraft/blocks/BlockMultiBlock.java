package com.InfinityRaider.AgriCraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityMultiBlock;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

public abstract class BlockMultiBlock extends BlockCustomWood{

    //when the block is broken
    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
        if(!world.isRemote) {
            LogHelper.debug("Breaking multiblock.");
			if (world.getTileEntity(x, y, z) instanceof TileEntityMultiBlock) {
				LogHelper.debug("Deconstructing multiblock.");
				TileEntityMultiBlock block = (TileEntityMultiBlock) world.getTileEntity(x, y, z);
				block.breakMultiBlock();
			} else {
				LogHelper.debug("Where did the TileEntity go? Now the multiblock can't be broken!");
			}
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }
    
    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
    	super.onPostBlockPlaced(world, x, y, z, meta);
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (te instanceof TileEntityMultiBlock) {
    		LogHelper.debug("Checking if block completed multiblock.");
    		((TileEntityMultiBlock)te).checkForMultiBlock();
    	} else {
    		LogHelper.debug("Multiblock place failure.");
    	}
    }
    
}
