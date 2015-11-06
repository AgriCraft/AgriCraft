package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockGrate;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockGrate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockGrate extends BlockCustomWood {
	
    @Override
    protected String getInternalName() {
        return Names.Objects.grate;
    }
	
    @Override
    protected String getTileEntityName() {
        return Names.Objects.grate;
    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
        return ItemBlockGrate.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderBlockGrate();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGrate();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return true;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        boolean front = grate.isPlayerInFront(player);
        if(player.isSneaking()) {
            if(grate.removeVines(front)) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.vine, 1));
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
     public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = super.getDrops(world, x, y, z, metadata, fortune);
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && (te instanceof TileEntityGrate)) {
                TileEntityGrate grate = (TileEntityGrate) te;
                int stackSize = 0;
                stackSize = grate.hasVines(true)?stackSize+1:stackSize;
                stackSize = grate.hasVines(false)?stackSize+1:stackSize;
                if(stackSize>0) {
                    items.add(new ItemStack(Blocks.vine, stackSize));
                }
            }
        }
        return items;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return getBoundingBox(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getBoundingBox(world, x, y, z);
    }

    public AxisAlignedBB getBoundingBox(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        AxisAlignedBB box;
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            box = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        } else {
            box = ((TileEntityGrate) tile).getBoundingBox();
        }
        return box;
    }

    /** Copied from the Block class, but changed the calls to isVecInside**Bounds methods */
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec0, Vec3 vec1) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        vec0 = vec0.addVector((double)(-x), (double)(-y), (double)(-z));
        vec1 = vec1.addVector((double) (-x), (double) (-y), (double) (-z));
        Vec3 vec2 = vec0.getIntermediateWithXValue(vec1, this.minX);
        Vec3 vec3 = vec0.getIntermediateWithXValue(vec1, this.maxX);
        Vec3 vec4 = vec0.getIntermediateWithYValue(vec1, this.minY);
        Vec3 vec5 = vec0.getIntermediateWithYValue(vec1, this.maxY);
        Vec3 vec6 = vec0.getIntermediateWithZValue(vec1, this.minZ);
        Vec3 vec7 = vec0.getIntermediateWithZValue(vec1, this.maxZ);
        Vec3 vec8 = null;
        if (!this.isVecInsideYZBounds(world, x, y, z, vec2)) {
            vec2 = null;
        }
        if (!this.isVecInsideYZBounds(world, x, y, z, vec3)) {
            vec3 = null;
        }
        if (!this.isVecInsideXZBounds(world, x, y, z, vec4)) {
            vec4 = null;
        }
        if (!this.isVecInsideXZBounds(world, x, y, z, vec5)) {
            vec5 = null;
        }
        if (!this.isVecInsideXYBounds(world, x, y, z, vec6)) {
            vec6 = null;
        }
        if (!this.isVecInsideXYBounds(world, x, y, z, vec7)) {
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
        } else {
            byte b0 = -1;
            if (vec8 == vec2)  {
                b0 = 4;
            }
            if (vec8 == vec3) {
                b0 = 5;
            }
            if (vec8 == vec4) {
                b0 = 0;
            }
            if (vec8 == vec5) {
                b0 = 1;
            }
            if (vec8 == vec6) {
                b0 = 2;
            }
            if (vec8 == vec7) {
                b0 = 3;
            }
            return new MovingObjectPosition(x, y, z, b0, vec8.addVector((double)x, (double)y, (double) z));
        }
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    private boolean isVecInsideYZBounds(World world, int x, int y, int z, Vec3 vec) {
        double[] bounds = getBlockBounds(world, x, y, z);
        return bounds!=null && vec!=null && vec.yCoord>=bounds[1]&& vec.yCoord<=bounds[4] &&vec.zCoord>=bounds[2] && vec.zCoord<=bounds[5];
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    private boolean isVecInsideXZBounds(World world, int x, int y, int z, Vec3 vec) {
        double[] bounds = getBlockBounds(world, x, y, z);
        return bounds!=null && vec!=null && vec.xCoord>=bounds[0] && vec.xCoord<=bounds[3] && vec.zCoord>=bounds[2] && vec.zCoord<=bounds[5];
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    private boolean isVecInsideXYBounds(World world, int x, int y, int z, Vec3 vec) {
        double[] bounds = getBlockBounds(world, x, y, z);
        return bounds!=null && vec!=null && vec.xCoord>=bounds[0] && vec.xCoord<=bounds[3] && vec.yCoord>=bounds[1] && vec.yCoord<=bounds[4];
    }

    private double[] getBlockBounds(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile==null || !(tile instanceof TileEntityGrate)) {
            //something is wrong
            return null;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        return grate.getBlockBounds();
    }
    
}
