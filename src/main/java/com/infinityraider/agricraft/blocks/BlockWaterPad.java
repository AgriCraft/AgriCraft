/*
 * An intermediate for the water pad.
 */
package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.renderers.blocks.RenderWaterPad;
import com.infinityraider.agricraft.utility.FluidHandlerBlockWrapper;
import com.infinityraider.agricraft.utility.IFluidHandlerBlock;
import com.infinityraider.infinitylib.block.BlockCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.block.blockstate.SidedConnection;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterPad extends BlockCustomRenderedBase implements IFluidHandlerBlock {

    public static final AxisAlignedBB WATER_PAD_BOUNDS = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

    public BlockWaterPad() {
        super("water_pad", Material.GROUND);
        this.setHardness(0.5F);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{
            AgriProperties.POWERED
        };
    }

    @Override
    protected IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[]{
            AgriProperties.CONNECTIONS
        };
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return AgriProperties.POWERED.applyToBlockState(this.getDefaultState(), meta != 0);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        SidedConnection connection = new SidedConnection();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            IBlockState stateAt = world.getBlockState(pos.offset(facing));
            connection.setConnected(facing, stateAt.getBlock() == state.getBlock());
        }
        return ((IExtendedBlockState) state).withProperty(AgriProperties.CONNECTIONS, connection);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return AgriProperties.POWERED.getValue(state) ? 0 : 1;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return Blocks.DIRT.getDrops(world, pos, Blocks.DIRT.getDefaultState(), fortune);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return WATER_PAD_BOUNDS;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return FluidUtil.interactWithFluidHandler(player, hand, new FluidHandlerBlockWrapper(this, world, pos));
    }

    @Override
    public IFluidTankProperties[] getTankProperties(World world, BlockPos pos, IBlockState state) {
        return new IFluidTankProperties[]{};
    }

    @Override
    public int fill(World world, BlockPos pos, IBlockState state, FluidStack fluid, boolean doFill) {
        if (!AgriProperties.POWERED.getValue(state) && (fluid != null) && (fluid.amount == 1000) && (fluid.getFluid().equals(FluidRegistry.WATER))) {
            if (doFill) {
                world.setBlockState(pos, AgriProperties.POWERED.applyToBlockState(state, true));
            }
            return 1000;
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(World world, BlockPos pos, IBlockState state, FluidStack fluid, boolean doDrain) {
        if ((AgriProperties.POWERED.getValue(state)) && (fluid != null) && (fluid.amount >= 1000) && (fluid.getFluid().equals(FluidRegistry.WATER))) {
            if (doDrain) {
                world.setBlockState(pos, AgriProperties.POWERED.applyToBlockState(state, false));
            }
            return new FluidStack(FluidRegistry.WATER, 1000);
        } else {
            return null;
        }
    }

    @Override
    public FluidStack drain(World world, BlockPos pos, IBlockState state, int amount, boolean doDrain) {
        if ((AgriProperties.POWERED.getValue(state)) && (amount >= 1000)) {
            if (doDrain) {
                world.setBlockState(pos, AgriProperties.POWERED.applyToBlockState(state, false));
            }
            return new FluidStack(FluidRegistry.WATER, 1000);
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderWaterPad getRenderer() {
        return new RenderWaterPad(this);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

}
