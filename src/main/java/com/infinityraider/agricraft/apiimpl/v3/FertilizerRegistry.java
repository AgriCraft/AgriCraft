/*
 */
package com.infinityraider.agricraft.apiimpl.v3;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v3.fertiliser.IAgriFertiliser;
import com.infinityraider.agricraft.api.v3.fertiliser.IFertiliserRegistry;

/**
 *
 * @author RlonRyan
 */
public class FertilizerRegistry implements IFertiliserRegistry {
	
	private static Random rand = new Random();

	@Override
    public boolean isSupportedFertiliser(ItemStack fertiliser) {
        if (fertiliser == null || fertiliser.getItem() == null) {
            return false;
        }
        if (fertiliser.getItem() == net.minecraft.init.Items.dye && fertiliser.getItemDamage() == 15) {
            return true;
        }
        if (fertiliser.getItem() instanceof IAgriFertiliser) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidFertiliser(World world, BlockPos pos, ItemStack fertiliser) {
        if (fertiliser == null || fertiliser.getItem() == null) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (fertiliser.getItem() == net.minecraft.init.Items.dye && fertiliser.getItemDamage() == 15) {
                return crop.canBonemeal();
            } else if (fertiliser.getItem() instanceof IAgriFertiliser) {
                return crop.acceptsFertiliser((IAgriFertiliser) fertiliser.getItem());
            }
        }
        return false;
    }

    @Override
    public boolean applyFertiliser(World world, BlockPos pos, IBlockState state, ItemStack fertiliser) {
        if (world.isRemote || !isValidFertiliser(world, pos, fertiliser)) {
            return false;
        }
        if (fertiliser.getItem() == net.minecraft.init.Items.dye && fertiliser.getItemDamage() == 15) {
            ((BlockCrop) AgriCraftBlocks.blockCrop).grow(world, rand, pos, state);
            fertiliser.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        } else if (fertiliser.getItem() instanceof IAgriFertiliser) {
            ((TileEntityCrop) world.getTileEntity(pos)).applyFertiliser((IAgriFertiliser) fertiliser.getItem(), world.rand);
            fertiliser.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        }
        return false;
    }
	
}
