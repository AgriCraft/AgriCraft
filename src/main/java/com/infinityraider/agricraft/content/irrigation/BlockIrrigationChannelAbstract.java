package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockIrrigationChannelAbstract extends BlockDynamicTexture<TileEntityIrrigationChannel> {
    // Properties
    public static final InfProperty<Boolean> NORTH = InfProperty.Creators.create("north", false);
    public static final InfProperty<Boolean> EAST = InfProperty.Creators.create("east", false);
    public static final InfProperty<Boolean> SOUTH = InfProperty.Creators.create("south", false);
    public static final InfProperty<Boolean> WEST = InfProperty.Creators.create("west", false);

    public static final InfProperty<Valve> VALVE = InfProperty.Creators.create("valve", Valve.class, Valve.NONE);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(NORTH).add(EAST).add(SOUTH).add(WEST)
            .add(VALVE)
            .build();

    public static Optional<InfProperty<Boolean>> getConnection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Optional.of(NORTH);
            case SOUTH:
                return Optional.of(SOUTH);
            case EAST:
                return Optional.of(EAST);
            case WEST:
                return Optional.of(WEST);
        }
        return Optional.empty();
    }

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityIrrigationChannel> TILE_FACTORY = (s, w) -> new TileEntityIrrigationChannel();

    public BlockIrrigationChannelAbstract(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityIrrigationChannel tile, LootContext.Builder context) {
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = this.getDefaultState();
        for(Direction dir : Direction.values()) {
            Optional<InfProperty<Boolean>> prop = getConnection(dir);
            if(prop.isPresent()) {
                TileEntity tile = world.getTileEntity(pos.offset(dir));
                if(tile instanceof TileEntityIrrigationComponent) {
                    TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
                    if(component.isSameMaterial(context.getItem())) {
                        state = prop.get().apply(state, true);
                    }
                }
            }
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState ownState, Direction dir, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos) {
        return getConnection(dir).map(prop -> {
            TileEntity otherTile = world.getTileEntity(otherPos);
            if(otherTile instanceof TileEntityIrrigationComponent) {
                TileEntityIrrigationComponent other = (TileEntityIrrigationComponent) otherTile;
                if (other.isSameMaterial(world.getTileEntity(pos))) {
                    return prop.apply(ownState, true);
                }
            }
            return prop.apply(ownState, false);
        }).orElse(super.updatePostPlacement(ownState, dir, otherState, world, pos, otherPos));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable TileEntity tile) {
        
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityIrrigationChannel> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    public enum Valve implements IStringSerializable {
        NONE,
        OPEN,
        CLOSED;

        @Override
        public String getString() {
            return this.name().toLowerCase();
        }
    }
}

