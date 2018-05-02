package com.infinityraider.agricraft.compat.industrialforegoing;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class CropsPlantRecollectable extends PlantRecollectable
{
    public CropsPlantRecollectable()
    {
        super("blockagricraftcrops");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState)
    {
        return blockState.getBlock() instanceof BlockCrop && ((BlockCrop)blockState.getBlock()).isMature(world, pos);
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState)
    {
        NonNullList<ItemStack> stacks = NonNullList.create();

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop)
        {
            TileEntityCrop cropTile = (TileEntityCrop)te;
            if (cropTile.isMature())
                cropTile.onHarvest(stacks::add, null);
        }

        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState)
    {
        return true;
    }

    @Override
    public List<String> getRecollectablesNames()
    {
        return Arrays.asList("item.agricraft:crop_sticks.compat.name");
    }
}
