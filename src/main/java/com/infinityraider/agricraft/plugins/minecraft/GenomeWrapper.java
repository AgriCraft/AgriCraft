package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenomeProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class GenomeWrapper implements IAgriAdapter<IAgriGenome> {
    @Override
    public boolean accepts(@Nullable Object obj) {
        return obj instanceof IAgriGenomeProvider || obj instanceof BlockEntity;
    }

    @Nonnull
    @Override
    public Optional<IAgriGenome> valueOf(@Nullable Object obj) {
        if(obj instanceof IAgriGenomeProvider) {
            return ((IAgriGenomeProvider) obj).getGenome();
        }
        if(obj instanceof BlockEntity) {
            BlockEntity tile = (BlockEntity) obj;
            return tile.getCapability(CropCapability.getCapability())
                    .map(crop -> crop)
                    .flatMap(IAgriGenomeProvider::getGenome);
        }
        return Optional.empty();
    }
}
