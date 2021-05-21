package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.fluid.AgriTankWaterRenderer;
import com.infinityraider.infinitylib.fluid.FluidBase;
import com.infinityraider.infinitylib.render.fluid.IFluidRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This entire class and existance of the fluid is an ugly hack
 * This fluid allows the player to swim in the tank, while having no other behaviour (including rendering)
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FluidTankWater extends FluidBase {
    private static final Vector3d FLOW = new Vector3d(0, 0, 0);

    public FluidTankWater() {
        super(Names.Fluids.TANK_WATER);
    }

    @Override
    public Item getFilledBucket() {
        return Items.WATER_BUCKET;
    }

    @Override
    protected boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    protected Vector3d getFlow(IBlockReader blockReader, BlockPos pos, FluidState fluidState) {
        return FLOW;
    }

    @Override
    public int getTickRate(IWorldReader world) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getActualHeight(FluidState state, IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityIrrigationTank) {
            return ((TileEntityIrrigationTank) tile).getLevel() - pos.getY();
        }
        return this.getHeight(state);
    }

    @Override
    public float getHeight(FluidState state) {
        return 1;
    }

    @Override
    protected BlockState getBlockState(FluidState state) {
        return AgriCraft.instance.getConfig().tankSpawnWaterBlockOnBreak()
                ? Blocks.WATER.getDefaultState()
                : Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return false;
    }

    @Override
    public int getLevel(FluidState state) {
        return 1;
    }

    @Override
    public VoxelShape func_215664_b(FluidState state, IBlockReader world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected FluidAttributes createAttributes() {
        return net.minecraftforge.fluids.FluidAttributes.builder(
                Fluids.WATER.getAttributes().getStillTexture(),
                Fluids.WATER.getAttributes().getFlowingTexture())
                .overlay(Fluids.WATER.getAttributes().getOverlayTexture())
                .translationKey("block.minecraft.water")
                .color(0xFF3F76E4)
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                .build(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IFluidRenderer getRenderer() {
        return AgriTankWaterRenderer.getInstance();
    }
}
