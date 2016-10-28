package com.infinityraider.agricraft.blocks.irrigation;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannel;

public class BlockWaterChannel extends AbstractBlockWaterChannel<TileEntityChannel> {

    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;

    public BlockWaterChannel() {
        super("normal");
    }

    @Override
    public TileEntityChannel createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    @Override
    public void onNeighborChange(IBlockAccess iba, BlockPos pos, BlockPos neighbor) {
        TileEntity te = iba.getTileEntity(pos);
        if (te != null && te instanceof TileEntityChannel) {
            ((TileEntityChannel) te).findNeighbours();
        }
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity entity) {
        //adjacent boxes
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) te;
            if (channel.hasNeighbourCheck(EnumFacing.EAST)) {
                AxisAlignedBB box = new AxisAlignedBB(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX);
                addCollisionBoxToList(pos, mask, list, box);
            }
            if (channel.hasNeighbourCheck(EnumFacing.WEST)) {
                AxisAlignedBB box = new AxisAlignedBB(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX);
                addCollisionBoxToList(pos, mask, list, box);
            }
            if (channel.hasNeighbourCheck(EnumFacing.SOUTH)) {
                AxisAlignedBB box = new AxisAlignedBB(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE);
                addCollisionBoxToList(pos, mask, list, box);
            }
            if (channel.hasNeighbourCheck(EnumFacing.NORTH)) {
                AxisAlignedBB box = new AxisAlignedBB(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT);
                addCollisionBoxToList(pos, mask, list, box);
            }
            //central box
            AxisAlignedBB box = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX);
            addCollisionBoxToList(pos, mask, list, box);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getSelectedBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        AxisAlignedBB minBB = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) te;
            if (channel.hasNeighbourCheck(EnumFacing.EAST)) {
                minBB.addCoord(1, MAX, minBB.maxZ);
            }
            if (channel.hasNeighbourCheck(EnumFacing.WEST)) {
                minBB.addCoord(0, MIN, minBB.minZ);
            }
            if (channel.hasNeighbourCheck(EnumFacing.SOUTH)) {
                minBB.addCoord(minBB.maxX, MAX, 1);
            }
            if (channel.hasNeighbourCheck(EnumFacing.NORTH)) {
                minBB.addCoord(minBB.minX, MIN, 0);
            }
        }
        return minBB.offset(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));    //wooden channel
        list.add(new ItemStack(item, 1, 1));    //iron pipe
    }

    //render methods
    //--------------
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public RenderChannel getRenderer() {
        return new RenderChannel(this, new TileEntityChannel());
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !AgriCraftConfig.disableIrrigation;
    }

}
