package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockGreenHouseMonitor extends BlockBase {
    public static InfProperty<IAgriGreenHouse.State> STATE = BlockGreenHouseAir.STATE;
    public static InfProperty<Direction> ORIENTATION = BlockSeedAnalyzer.ORIENTATION;

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder().add(STATE).add(ORIENTATION).build();

    public BlockGreenHouseMonitor() {
        super(Names.Blocks.GREENHOUSE_MONITOR, Properties.of(Material.GLASS)
                .noCollission()
                .noCollission()
                .instabreak()
        );
    }

    public static BlockState withState(BlockState monitor, IAgriGreenHouse.State state) {
        if(monitor.hasProperty(STATE.getProperty())) {
            return STATE.apply(monitor, state);
        }
        return monitor;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public Item asItem() {
        return AgriApi.getAgriContent().getItems().getGreenHouseMonitorItem();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getClickedFace().getAxis() == Direction.Axis.Y) {
            // can not place vertically
            return null;
        }
        // fetch default state
        BlockState state = this.defaultBlockState();
        if(state.canSurvive(context.getLevel(), context.getClickedPos())) {
            // apply greenhouse state
            state = STATE.apply(state, AgriApi.getGreenHouse(context.getLevel(), context.getClickedPos())
                    .map(IAgriGreenHouse::getState)
                    .orElse(IAgriGreenHouse.State.REMOVED));
            // apply orientation
            state = ORIENTATION.apply(
                    this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                    context.getHorizontalDirection());
            // return the state
            return state;
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }
}
