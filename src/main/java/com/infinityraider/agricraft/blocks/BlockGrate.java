package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockGrate;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockBase;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockGrate;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockGrate extends BlockCustomWood {

	public BlockGrate() {
		super("grate", false);
	}

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
        return ItemBlockGrate.class;
    }

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockGrate getRenderer() {
        return new RenderBlockGrate();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGrate();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return true;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        boolean front = grate.isPlayerInFront(player);
        if(player.isSneaking()) {
            if(grate.removeVines(front)) {
                spawnAsEntity(world, pos, new ItemStack(Blocks.vine, 1));
                return true;
            }
        }
        else if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem()== Item.getItemFromBlock(Blocks.vine)) {
            if(grate.addVines(front) && !player.capabilities.isCreativeMode) {
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize-1;
                return true;
            }
        }
        return false;
    }

    @Override
     public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> items = super.getDrops(world, pos, state, fortune);
        TileEntity te = world.getTileEntity(pos);
        if (te != null && (te instanceof TileEntityGrate)) {
            TileEntityGrate grate = (TileEntityGrate) te;
            int stackSize = 0;
            stackSize = grate.hasVines(true) ? stackSize + 1 : stackSize;
            stackSize = grate.hasVines(false) ? stackSize + 1 : stackSize;
            if (stackSize > 0) {
                items.add(new ItemStack(Blocks.vine, stackSize));
            }
        }
        return items;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return getBoundingBox(world, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return getBoundingBox(world, pos, world.getBlockState(pos));
    }

    public AxisAlignedBB getBoundingBox(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        AxisAlignedBB box;
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            box = super.getCollisionBoundingBox(world, pos, state);
        } else {
            box = ((TileEntityGrate) tile).getBoundingBox();
        }
        return box;
    }

    /** Copied from the Block class, but changed the calls to isVecInside**Bounds methods */
    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 vec0, Vec3 vec1) {
        this.setBlockBoundsBasedOnState(world, pos);
        vec0 = vec0.addVector((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
        vec1 = vec1.addVector((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()));
        Vec3 vec2 = vec0.getIntermediateWithXValue(vec1, this.minX);
        Vec3 vec3 = vec0.getIntermediateWithXValue(vec1, this.maxX);
        Vec3 vec4 = vec0.getIntermediateWithYValue(vec1, this.minY);
        Vec3 vec5 = vec0.getIntermediateWithYValue(vec1, this.maxY);
        Vec3 vec6 = vec0.getIntermediateWithZValue(vec1, this.minZ);
        Vec3 vec7 = vec0.getIntermediateWithZValue(vec1, this.maxZ);
        Vec3 vec8 = null;
        if (!this.isVecInsideYZBounds(world, pos, vec2)) {
            vec2 = null;
        }
        if (!this.isVecInsideYZBounds(world, pos, vec3)) {
            vec3 = null;
        }
        if (!this.isVecInsideXZBounds(world, pos, vec4)) {
            vec4 = null;
        }
        if (!this.isVecInsideXZBounds(world, pos, vec5)) {
            vec5 = null;
        }
        if (!this.isVecInsideXYBounds(world, pos, vec6)) {
            vec6 = null;
        }
        if (!this.isVecInsideXYBounds(world, pos, vec7)) {
            vec7 = null;
        }
        if (vec2 != null) {
            vec8 = vec2;
        }
        if (vec3 != null && (vec8 == null || vec0.squareDistanceTo(vec3) < vec0.squareDistanceTo(vec8))) {
            vec8 = vec3;
        }
        if (vec4 != null && (vec8 == null || vec0.squareDistanceTo(vec4) < vec0.squareDistanceTo(vec8))) {
            vec8 = vec4;
        }
        if (vec5 != null && (vec8 == null || vec0.squareDistanceTo(vec5) < vec0.squareDistanceTo(vec8))) {
            vec8 = vec5;
        }
        if (vec6 != null && (vec8 == null || vec0.squareDistanceTo(vec6) < vec0.squareDistanceTo(vec8))) {
            vec8 = vec6;
        }
        if (vec7 != null && (vec8 == null || vec0.squareDistanceTo(vec7) < vec0.squareDistanceTo(vec8))) {
            vec8 = vec7;
        }
        if (vec8 == null) {
            return null;
        }  else {
            EnumFacing enumfacing = null;
            if (vec8 == vec3) {
                enumfacing = EnumFacing.WEST;
            }
            if (vec8 == vec2) {
                enumfacing = EnumFacing.EAST;
            }
            if (vec8 == vec3) {
                enumfacing = EnumFacing.DOWN;
            }
            if (vec8 == vec4) {
                enumfacing = EnumFacing.UP;
            }
            if (vec8 == vec5) {
                enumfacing = EnumFacing.NORTH;
            }
            if (vec8 == vec6) {
                enumfacing = EnumFacing.SOUTH;
            }
            return new MovingObjectPosition(vec8.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), enumfacing, pos);
        }
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    private boolean isVecInsideYZBounds(World world, BlockPos pos, Vec3 vec) {
        double[] bounds = getBlockBounds(world, pos);
        return bounds!=null && vec!=null && vec.yCoord>=bounds[1]&& vec.yCoord<=bounds[4] &&vec.zCoord>=bounds[2] && vec.zCoord<=bounds[5];
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    private boolean isVecInsideXZBounds(World world, BlockPos pos, Vec3 vec) {
        double[] bounds = getBlockBounds(world, pos);
        return bounds!=null && vec!=null && vec.xCoord>=bounds[0] && vec.xCoord<=bounds[3] && vec.zCoord>=bounds[2] && vec.zCoord<=bounds[5];
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    private boolean isVecInsideXYBounds(World world, BlockPos pos, Vec3 vec) {
        double[] bounds = getBlockBounds(world, pos);
        return bounds!=null && vec!=null && vec.xCoord>=bounds[0] && vec.xCoord<=bounds[3] && vec.yCoord>=bounds[1] && vec.yCoord<=bounds[4];
    }

    private double[] getBlockBounds(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile==null || !(tile instanceof TileEntityGrate)) {
            //something is wrong
            return null;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        return grate.getBlockBounds();
    }
    
}
