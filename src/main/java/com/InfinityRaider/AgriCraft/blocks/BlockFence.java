package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.entity.EntityLeashKnotAgricraft;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFence;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFence;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
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
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ) {
        return world.isRemote || applyLead(player, world, x, y, z);
    }

    public boolean applyLead(EntityPlayer player, World world, int x, int y, int z) {
        EntityLeashKnotAgricraft entityleashknot = EntityLeashKnotAgricraft.getKnotForBlock(world, x, y, z);
        boolean flag = false;
        double d0 = 7.0D;
        List list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox((double)x - d0, (double) y - d0, (double) z - d0, (double)x + d0, (double)y + d0, (double)z + d0));
        if (list != null) {
            for (Object obj : list) {
                EntityLiving entityliving = (EntityLiving) obj;
                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
                    if (entityleashknot == null) {
                        entityleashknot = EntityLeashKnotAgricraft.func_110129_a(world, x, y, z);
                    }
                    entityliving.setLeashedToEntity(entityleashknot, true);
                    flag = true;
                }
            }
        }
        return flag;
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
        Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
        if (block == null) {
            return false;
        }
        if (block.isAir(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
            return false;
        }
        if (block.isOpaqueCube()) {
            return true;
        }
        if ((block instanceof BlockFence) || (block instanceof BlockFenceGate) || (block instanceof BlockWaterTank)) {
            return true;
        }
        if ((block instanceof net.minecraft.block.BlockFence) || (block instanceof net.minecraft.block.BlockFenceGate)) {
            return true;
        }
        return false;
    }
    
}
