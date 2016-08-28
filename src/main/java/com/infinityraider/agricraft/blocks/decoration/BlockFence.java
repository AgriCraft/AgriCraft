package com.infinityraider.agricraft.blocks.decoration;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.entity.EntityLeashKnotAgricraft;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockFence;
import com.infinityraider.agricraft.tiles.decoration.TileEntityFence;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockFence extends BlockCustomWood<TileEntityFence> {

	public BlockFence() {
		super("fence");
	}

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockFence getRenderer() {
        return new RenderBlockFence(this);
    }

    @Override
    public TileEntityFence createNewTileEntity(World world, int meta) {
        return new TileEntityFence();
    }

    //Allow leads to be connected to these fences
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
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

    /*
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, pos, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        addCollisionBoxToList(pos, mask, list, net.minecraft.block.BlockFence.PILLAR_AABB);
        if (this.canConnect(world, pos, EnumFacing.NORTH)) {
            addCollisionBoxToList(pos, mask, list, net.minecraft.block.BlockFence.NORTH_AABB);
        }
        if (this.canConnect(world, pos, EnumFacing.EAST)) {
            addCollisionBoxToList(pos, mask, list, net.minecraft.block.BlockFence.EAST_AABB);
        }
        if (this.canConnect(world, pos, EnumFacing.SOUTH)) {
            addCollisionBoxToList(pos, mask, list, net.minecraft.block.BlockFence.SOUTH_AABB);
        }
        if (this.canConnect(world, pos, EnumFacing.WEST)) {
            addCollisionBoxToList(pos, mask, list, net.minecraft.block.BlockFence.WEST_AABB);
        }
    }

    public boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        IBlockState state = world.getBlockState(pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()));
        Block block = state.getBlock();
        if (block == null) {
            return false;
        }
        if (block.isAir(state, world, pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()))) {
            return false;
        }
        if (block.isOpaqueCube(state)) {
            return true;
        }
        if ((block instanceof BlockFence) || (block instanceof BlockFenceGate) || (block instanceof BlockWaterTank)) {
            return true;
        }
        return (block instanceof net.minecraft.block.BlockFence) || (block instanceof net.minecraft.block.BlockFenceGate);
    }

    @Override
    public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP || side == EnumFacing.DOWN;
    }
	
	@Override
	public boolean isEnabled() {
		return AgriCraftConfig.enableFences;
	}

}
