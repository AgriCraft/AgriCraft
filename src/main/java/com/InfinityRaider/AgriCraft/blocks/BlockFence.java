package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFence;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFence;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockFence extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.fence;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockFence();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.fence;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFence();
    }

    //Allow leads to be connected to these fences
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int meta, float hitX, float hitY, float hitZ) {
        return world.isRemote || ItemLead.func_150909_a(p, world, x, y, z);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity e) {
        boolean flag = this.canConnect(world, x, y, z, ForgeDirection.NORTH);
        boolean flag1 = this.canConnect(world, x, y, z, ForgeDirection.SOUTH);
        boolean flag2 = this.canConnect(world, x, y, z, ForgeDirection.WEST);
        boolean flag3 = this.canConnect(world, x, y, z, ForgeDirection.EAST);
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = flag?0.0F:0.375F;
        float f3 = flag1?1.0F:0.625F;
        if (flag || flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
            super.addCollisionBoxesToList(world, x, y, z, box, list, e);
        }
        f2 = 0.375F;
        f3 = 0.625F;
        if (flag2) {
            f = 0.0F;
        }
        if (flag3) {
            f1 = 1.0F;
        }
        if (flag2 || flag3 || !flag && !flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
            super.addCollisionBoxesToList(world, x, y, z, box, list, e);
        }
        if (flag) {
            f2 = 0.0F;
        }
        if (flag1) {
            f3 = 1.0F;
        }
        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        boolean flag = this.canConnect(world, x, y, z, ForgeDirection.NORTH);
        boolean flag1 = this.canConnect(world, x, y, z, ForgeDirection.SOUTH);
        boolean flag2 = this.canConnect(world, x, y, z, ForgeDirection.WEST);
        boolean flag3 = this.canConnect(world, x, y, z, ForgeDirection.EAST);
        float f = flag2?0.0F:0.375F;
        float f1 = flag3?1.0F:0.625F;
        float f2 = flag?0.0F:0.375F;
        float f3 = flag1?1.0F:0.625F;
        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public boolean canConnect(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile==null || !(tile instanceof TileEntityFence)) {
            Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (block == null) {
                return false;
            }
            if (block.isAir(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                return false;
            }
            return true;
        }
        return ((TileEntityFence) tile).canConnect(dir);
    }
}
