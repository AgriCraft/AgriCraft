package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

import java.util.function.BiFunction;

public class BlockSeedAnalyzer extends BlockBaseTile<TileEntitySeedAnalyzer> implements IWaterLoggable {
    // Properties
    public static final InfProperty<Direction> ORIENTATION = InfProperty.Creators.createHorizontals("orientation", Direction.NORTH);
    public static final InfProperty<Boolean> JOURNAL = InfProperty.Creators.create("journal", false);
    public static final InfProperty<Boolean> SEED = InfProperty.Creators.create("seed", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(ORIENTATION)
            .add(JOURNAL)
            .add(SEED)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntitySeedAnalyzer> TILE_FACTORY = (s, w) -> new TileEntitySeedAnalyzer();

    public BlockSeedAnalyzer() {
        super(Names.Blocks.SEED_ANALYZER, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Override
    public Item asItem() {
        return AgriCraft.instance.getModItemRegistry().seed_analyzer;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntitySeedAnalyzer> getTileEntityFactory() {
        return TILE_FACTORY;
    }
}
