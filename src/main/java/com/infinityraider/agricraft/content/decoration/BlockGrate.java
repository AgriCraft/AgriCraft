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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class BlockGrate extends BlockDynamicTexture<TileEntityGrate> {
    // Properties
    public static final InfProperty<Direction.Axis> AXIS = InfProperty.Creators.create("direction", Direction.Axis.X);
    public static final InfProperty<Offset> OFFSET = InfProperty.Creators.create("offset", Offset.class, Offset.MID);
    public static final InfProperty<Vines> VINES = InfProperty.Creators.create("vines", Vines.class, Vines.NONE);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(AXIS)
            .add(OFFSET)
            .add(VINES)
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
                                @Nullable LivingEntity placer, @Nonnull ItemStack stack, @Nullable TileEntity tile) {
    }

    public enum Offset implements IStringSerializable {
        NEAR,
        MID,
        FAR;

        @Override
        public String getString() {
            return this.name().toLowerCase();
        }
    }

    public enum Vines implements IStringSerializable {
        NONE,
        FRONT,
        BACK,
        BOTH;

        @Override
        public String getString() {
            return this.name().toLowerCase();
        }
    }
}
