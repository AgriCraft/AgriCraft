package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class BlockGrate extends BlockDynamicTexture<TileEntityGrate> {
    // Properties
    public static final InfProperty<Direction.Axis> AXIS = InfProperty.Creators.create("direction", Direction.Axis.X);
    public static final InfProperty<Integer> OFFSET = InfProperty.Creators.create("offset", 0, -1, 1);
    public static final InfProperty<Boolean> VINES_FRONT = InfProperty.Creators.create("vines_front", false);
    public static final InfProperty<Boolean> VINES_BACK = InfProperty.Creators.create("vines_back", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(AXIS)
            .add(OFFSET)
            .add(VINES_FRONT)
            .add(VINES_BACK)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityGrate> TILE_FACTORY = (s, w) -> new TileEntityGrate();

    public BlockGrate() {
        super(Names.Blocks.GRATE, Properties.create(Material.WOOD));
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityGrate> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state,
                                @Nullable LivingEntity placer, @Nonnull ItemStack stack, @Nullable TileEntity tile) {}
}
