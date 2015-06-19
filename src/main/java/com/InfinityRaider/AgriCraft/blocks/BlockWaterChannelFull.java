package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlockWaterChannelFull extends BlockWaterChannel {
    public BlockWaterChannelFull() {
        super();
        this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
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
    public int getRenderType() {return AgriCraft.proxy.getRenderId(Constants.channelFullId);}
}
