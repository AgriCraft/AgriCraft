package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockIrrigationChannelAbstract extends BlockDynamicTexture<TileEntityChannel> {
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

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityChannel> TILE_FACTORY = (s, w) -> new TileEntityChannel();

    public BlockIrrigationChannelAbstract(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityChannel tile, LootContext.Builder context) {

    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable TileEntity tile) {

    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityChannel> getTileEntityFactory() {
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

