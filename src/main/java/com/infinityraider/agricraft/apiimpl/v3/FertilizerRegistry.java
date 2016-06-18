/*
 */
package com.infinityraider.agricraft.apiimpl.v3;

import com.infinityraider.agricraft.api.v3.registry.IFertilizerRegistry;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v3.core.IAgriFertiliser;

/**
 *
 * @author RlonRyan
 */
public class FertilizerRegistry implements IFertilizerRegistry {
	
	private static Random rand = new Random();

	@Override
    public boolean isSupportedFertilizer(ItemStack fertilizer) {
        if (fertilizer == null || fertilizer.getItem() == null) {
            return false;
        }
        if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
            return true;
        }
        if (fertilizer.getItem() instanceof IAgriFertiliser) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidFertilizer(World world, BlockPos pos, ItemStack fertilizer) {
        if (fertilizer == null || fertilizer.getItem() == null) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
                return crop.canBonemeal();
            } else if (fertilizer.getItem() instanceof IAgriFertiliser) {
                return crop.allowFertiliser((IAgriFertiliser) fertilizer.getItem());
            }
        }
        return false;
    }

    @Override
    public boolean applyFertilizer(World world, BlockPos pos, IBlockState state, ItemStack fertilizer) {
        if (world.isRemote || !isValidFertilizer(world, pos, fertilizer)) {
            return false;
        }
        if (fertilizer.getItem() == net.minecraft.init.Items.dye && fertilizer.getItemDamage() == 15) {
            ((BlockCrop) AgriCraftBlocks.blockCrop).grow(world, rand, pos, state);
            fertilizer.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        } else if (fertilizer.getItem() instanceof IAgriFertiliser) {
            ((TileEntityCrop) world.getTileEntity(pos)).applyFertiliser((IAgriFertiliser) fertilizer.getItem(), world.rand);
            fertilizer.stackSize--;
            world.playAuxSFX(2005, pos, 0);
            return true;
        }
        return false;
    }
	
}
