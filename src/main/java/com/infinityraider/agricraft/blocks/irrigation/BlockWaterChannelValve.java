package com.infinityraider.agricraft.blocks.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannelValve;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelValve;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterChannelValve extends AbstractBlockWaterChannel<TileEntityChannelValve> {

    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;

    protected static final double EXPANSION = 1 / 64d;

    public static final AxisAlignedBB CENTER_BOX = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB NORTH_BOX = new AxisAlignedBB(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB EAST_BOX = new AxisAlignedBB(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB SOUTH_BOX = new AxisAlignedBB(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB WEST_BOX = new AxisAlignedBB(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);

    private final ItemBlockValve itemBlock;

    public BlockWaterChannelValve() {
        super("valve");
        this.itemBlock = new ItemBlockValve(this);
    }
    
    @Override
    public TileEntityChannelValve createNewTileEntity(World world, int meta) {
        return new TileEntityChannelValve();
    }
    
    @Override
    public Optional<ItemBlockCustomWood> getItemBlock() {
        return Optional.of(this.itemBlock);
    }
    
    /*
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, @Nullable Entity entity, boolean isActualState) {
        // Add central box.
        addCollisionBoxToList(pos, mask, list, CENTER_BOX);

        // Fetch Tile Entity
        final TileEntityChannelValve tile = WorldHelper.getTile(world, pos, TileEntityChannelValve.class).orElse(null);

        // If null stop.
        if (tile == null) {
            return;
        }

        //adjacent boxes
        if (tile.getConnections().get(EnumFacing.NORTH) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, NORTH_BOX);
        }
        if (tile.getConnections().get(EnumFacing.EAST) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, EAST_BOX);
        }
        if (tile.getConnections().get(EnumFacing.SOUTH) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, SOUTH_BOX);
        }
        if (tile.getConnections().get(EnumFacing.WEST) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, WEST_BOX);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Fetch Tile Entity
        final TileEntityChannelValve tile = WorldHelper.getTile(world, pos, TileEntityChannelValve.class).orElse(null);

        // Define Core Bounding Box
        AxisAlignedBB selection = CENTER_BOX;

        // Expand Bounding Box
        if (tile != null) {
            if (tile.getConnections().get(EnumFacing.NORTH) > 0) {
                selection = selection.union(NORTH_BOX);
            }
            if (tile.getConnections().get(EnumFacing.EAST) > 0) {
                selection = selection.union(EAST_BOX);
            }
            if (tile.getConnections().get(EnumFacing.SOUTH) > 0) {
                selection = selection.union(SOUTH_BOX);
            }
            if (tile.getConnections().get(EnumFacing.WEST) > 0) {
                selection = selection.union(WEST_BOX);
            }
        }

        return selection;
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        InfinityProperty[] properties = Arrays.copyOf(super.getPropertyArray(), super.getPropertyArray().length + 1);
        properties[properties.length - 1] = AgriProperties.POWERED;
        return properties;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityChannelValve> tile = WorldHelper.getTile(worldIn, pos, TileEntityChannelValve.class);
        return AgriProperties.POWERED.applyToBlockState(super.getActualState(state, worldIn, pos), tile.isPresent() && tile.get().isPowered());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void observedNeighborChange(IBlockState observerState, World world, BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
        super.observedNeighborChange(observerState, world, pos, changedBlock, changedBlockPos);
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityChannelValve) {
            TileEntityChannelValve valve = (TileEntityChannelValve) te;
            valve.updatePowerStatus();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityChannelValve) {
                ((TileEntityChannelValve) te).updatePowerStatus();
            }
        }
    }

    //allows levers to be attached to the block
    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannelValve getRenderer() {
        return new RenderChannelValve(this);
    }

    public static class ItemBlockValve extends ItemBlockCustomWood {

        public ItemBlockValve(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
            super.addInformation(stack, world, list, flag);
            list.add(AgriCore.getTranslator().translate("agricraft_tooltip.valve"));
        }
    }

}
