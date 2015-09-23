package com.InfinityRaider.AgriCraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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
				block.breakupMultiBlock(true);
			} else {
				LogHelper.error("The tile entity at: (" + x + "," + y + "," + z + ") is not a multiblock, like it should be." );
			}
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
    	super.onBlockPlacedBy(world, x, y, z, entity, stack);
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (te instanceof TileEntityMultiBlock) {
    		LogHelper.debug("Checking if block completed multiblock.");
    		((TileEntityMultiBlock)te).formMultiBlock();
    	} else {
    		LogHelper.debug("Multiblock place failure. Unformed? " + (te == null) + " At: (" + x + "," + y + "," + z + ").");
    	}
    }
    
}
