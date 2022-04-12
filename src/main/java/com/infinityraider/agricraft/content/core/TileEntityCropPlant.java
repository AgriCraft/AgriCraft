package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class TileEntityCropPlant extends TileEntityCropBase {
    public TileEntityCropPlant(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.crop_plant, pos, state);
    }

    @Override
    public boolean hasCropSticks() {
        return false;
    }

    @Override
    public boolean isCrossCrop() {
        return false;
    }

    @Override
    public boolean setCrossCrop(boolean status) {
        return false;
    }

    @Nonnull
    @Override
    public Stream<IAgriCrop> streamNeighbours() {
        return Stream.empty();
    }

    @Override
    public boolean removeGenome() {
        boolean result = super.removeGenome();
        if(result && this.getLevel() != null && !this.getLevel().isClientSide()) {
            this.getLevel().setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        }
        return result;
    }
}
