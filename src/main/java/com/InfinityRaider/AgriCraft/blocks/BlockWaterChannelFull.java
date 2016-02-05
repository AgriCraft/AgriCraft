package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderChannelFull;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannelFull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockWaterChannelFull extends AbstractBlockWaterChannel {
	
    public BlockWaterChannelFull() {
        super(Names.Objects.channelFull);
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannelFull();
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB boundingBox, List<AxisAlignedBB> list, Entity entity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(world,pos, state);
        if (axisalignedbb != null && boundingBox.intersectsWith(axisalignedbb)) {
            list.add(axisalignedbb);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return  new AxisAlignedBB((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) pos.getY() + this.maxY, (double) pos.getZ() + this.maxZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderChannelFull();
    }

}
