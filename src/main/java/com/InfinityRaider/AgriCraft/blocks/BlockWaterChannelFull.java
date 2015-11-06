package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderChannelFull;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannelFull;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlockWaterChannelFull extends BlockWaterChannel {
    public BlockWaterChannelFull() {
        super();
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannelFull();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB boundingBox, List list, Entity entity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisalignedbb != null && boundingBox.intersectsWith(axisalignedbb)) {
            list.add(axisalignedbb);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderChannelFull();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.channelFull;
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.channelFull;
    }
}
