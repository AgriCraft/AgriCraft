package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.entity.EntityLeashKnotAgricraft;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFence;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFence;
import com.InfinityRaider.AgriCraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockFence extends BlockCustomWood {

	public BlockFence() {
		super("fence", false);
	}

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderBlockFence();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFence();
    }

    //Allow leads to be connected to these fences
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        return world.isRemote || applyLead(player, world, pos);
    }

    public boolean applyLead(EntityPlayer player, World world, BlockPos pos) {
        EntityLeashKnotAgricraft entityleashknot = EntityLeashKnotAgricraft.getKnotForPosition(world, pos);
        boolean flag = false;
        double d0 = 7.0D;
        List list = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)pos.getX() - d0, (double) pos.getY() - d0, (double) pos.getZ() - d0, (double) pos.getX() + d0, (double) pos.getY() + d0, (double) pos.getZ() + d0));
        if (list != null) {
            for (Object obj : list) {
                EntityLiving entityliving = (EntityLiving) obj;
                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
                    if (entityleashknot == null) {
                        entityleashknot = EntityLeashKnotAgricraft.createKnot(world, pos);
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
     * mask.) Parameters: World, pos, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        boolean flag = this.canConnect(world, pos, AgriForgeDirection.NORTH);
        boolean flag1 = this.canConnect(world, pos, AgriForgeDirection.SOUTH);
        boolean flag2 = this.canConnect(world, pos, AgriForgeDirection.WEST);
        boolean flag3 = this.canConnect(world, pos, AgriForgeDirection.EAST);
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = flag?0.0F:0.375F;
        float f3 = flag1?1.0F:0.625F;
        if (flag || flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.5F, f3);
            super.addCollisionBoxesToList(world, pos, state, mask, list, entity);
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
            super.addCollisionBoxesToList(world, pos, state, mask, list, entity);
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
     * Updates the blocks bounds based on its current state. Args: world, pos
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        boolean flag = this.canConnect(world, pos, AgriForgeDirection.NORTH);
        boolean flag1 = this.canConnect(world, pos, AgriForgeDirection.SOUTH);
        boolean flag2 = this.canConnect(world, pos, AgriForgeDirection.WEST);
        boolean flag3 = this.canConnect(world, pos, AgriForgeDirection.EAST);
        float f = flag2?0.0F:0.375F;
        float f1 = flag3?1.0F:0.625F;
        float f2 = flag?0.0F:0.375F;
        float f3 = flag1?1.0F:0.625F;
        this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public boolean canConnect(IBlockAccess world, BlockPos pos, AgriForgeDirection dir) {
        Block block = world.getBlockState(pos.add(dir.offsetX, dir.offsetY, dir.offsetZ)).getBlock();
        if (block == null) {
            return false;
        }
        if (block.isAir(world, pos.add(dir.offsetX, dir.offsetY, dir.offsetZ))) {
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
