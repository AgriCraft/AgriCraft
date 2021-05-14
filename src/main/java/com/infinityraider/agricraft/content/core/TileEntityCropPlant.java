package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class TileEntityCropPlant extends TileEntityCropBase {
    public TileEntityCropPlant() {
        super(AgriCraft.instance.getModTileRegistry().crop_plant);
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
        if(result && this.getWorld() != null && !this.getWorld().isRemote()) {
            this.getWorld().setBlockState(this.getPos(), Blocks.AIR.getDefaultState());
        }
        return result;
    }
}
