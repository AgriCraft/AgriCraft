package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.fluid.AgriTankWaterRenderer;
import com.infinityraider.infinitylib.fluid.FluidBase;
import com.infinityraider.infinitylib.render.fluid.IFluidRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This entire class and existence of the fluid is an ugly hack
 * This fluid allows the player to swim in the tank, while having no other behaviour (including rendering)
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FluidTankWater extends FluidBase {
    private static final Vec3 FLOW = new Vec3(0, 0, 0);

    public FluidTankWater() {
        super(Names.Fluids.TANK_WATER);
    }

    @Override
    public Item getBucket() {
        return Items.WATER_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    protected Vec3 getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState) {
        return FLOW;
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState state, BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityIrrigationTank) {
            return ((TileEntityIrrigationTank) tile).getWaterLevel() - pos.getY();
        }
        return this.getOwnHeight(state);
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return 1;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return AgriCraft.instance.getConfig().tankSpawnWaterBlockOnBreak()
                ? Blocks.WATER.defaultBlockState()
                : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return false;
    }

    @Override
    public int getAmount(FluidState state) {
        return 1;
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockGetter world, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected FluidAttributes createAttributes() {
        return net.minecraftforge.fluids.FluidAttributes.builder(
                Fluids.WATER.getAttributes().getStillTexture(),
                Fluids.WATER.getAttributes().getFlowingTexture())
                .overlay(Fluids.WATER.getAttributes().getOverlayTexture())
                .translationKey("block.minecraft.water")
                .color(0xFF3F76E4)
                .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                .build(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IFluidRenderer getRenderer() {
        return AgriTankWaterRenderer.getInstance();
    }
}
