package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
public class BlockIrrigationTank extends BlockDynamicTexture<TileEntityTank> {
    // Properties
    public static final InfProperty<Connection> NORTH = InfProperty.Creators.create("north", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> EAST = InfProperty.Creators.create("east", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> SOUTH = InfProperty.Creators.create("south", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> WEST = InfProperty.Creators.create("west", Connection.class, Connection.NONE);
    public static final InfProperty<Boolean> DOWN = InfProperty.Creators.create("down", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(NORTH).add(EAST).add(SOUTH).add(WEST).add(DOWN)
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityTank> TILE_FACTORY = (s, w) -> new TileEntityTank();

    public BlockIrrigationTank() {
        super(Names.Blocks.TANK, Properties.create(Material.WOOD)
                .notSolid()
        );
    }
    @Override
    public ItemIrrigationTank asItem() {
        return AgriCraft.instance.getModItemRegistry().tank;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityTank> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityTank tile, LootContext.Builder context) {}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                                ItemStack stack, @Nullable TileEntity tile) {}

    public enum Connection implements IStringSerializable {
        NONE,
        TANK,
        CHANNEL;

        @Override
        public String getString() {
            return this.name().toLowerCase();
        }
    }
}
