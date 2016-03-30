package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.renderers.blocks.RenderChannelFull;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelFull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockWaterChannelFull extends AbstractBlockWaterChannel {
	
    public BlockWaterChannelFull() {
        super("full");
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannelFull();
    }

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox(state, world, pos);
        if (axisalignedbb != null && mask.intersectsWith(axisalignedbb)) {
            list.add(axisalignedbb);
        }
    }

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return  new AxisAlignedBB((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) pos.getY() + this.maxY, (double) pos.getZ() + this.maxZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannelFull getRenderer() {
        return new RenderChannelFull();
    }

}
