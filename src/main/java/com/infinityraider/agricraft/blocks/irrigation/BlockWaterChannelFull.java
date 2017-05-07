package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.crafting.CustomWoodRecipeHelper;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.renderers.blocks.RenderChannelFull;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockWaterChannelFull extends AbstractBlockWaterChannel<TileEntityChannelFull> implements IRecipeRegister {
    public BlockWaterChannelFull() {
        super("full");
    }

    @Override
    public TileEntityChannelFull createNewTileEntity(World world, int meta) {
        return new TileEntityChannelFull();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        AxisAlignedBB box = this.getSelectedBoundingBox(state, world, pos);
        addCollisionBoxToList(pos, mask, list, box);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getSelectedBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1, pos.getZ()+1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannelFull getRenderer() {
        return new RenderChannelFull(this);
    }

    @Override
    public void registerRecipes() {
        // Creation Recipe
        CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().CHANNEL_FULL, 1, false, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL, AgriBlocks.getInstance().CHANNEL);
        // Deconstruction Recipe
        CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().CHANNEL, 4, false, AgriBlocks.getInstance().CHANNEL_FULL);
    }
    
}
