package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockGreenHouseAir extends BlockBase {
    public static InfProperty<IAgriGreenHouse.State> STATE = InfProperty.Creators.create("greenhouse_state", IAgriGreenHouse.State.class, IAgriGreenHouse.State.REMOVED);
    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder().add(STATE).build();

    public BlockGreenHouseAir() {
        super(Names.Blocks.GREENHOUSE_AIR,  Properties.of(Material.AIR)
                .noCollission()
                .noOcclusion()
                .air()
                .noDrops()
        );
    }

    public static BlockState withState(IAgriGreenHouse.State state) {
        return withState(AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState(), state);
    }

    public static BlockState withState(BlockState air, IAgriGreenHouse.State state) {
        if(air.hasProperty(STATE.getProperty())) {
            return STATE.apply(air, state);
        }
        return air;
    }

    public static IAgriGreenHouse.State getState(BlockState state) {
        return state.hasProperty(STATE.getProperty()) ? STATE.fetch(state) : IAgriGreenHouse.State.REMOVED;
    }

    @Nonnull
    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getVisualShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

}
