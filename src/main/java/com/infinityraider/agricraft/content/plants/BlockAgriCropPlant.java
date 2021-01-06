package com.infinityraider.agricraft.content.plants;

import com.infinityraider.infinitylib.block.IInfinityBlock;
import net.minecraft.block.CropsBlock;

import javax.annotation.Nonnull;

public class BlockAgriCropPlant extends CropsBlock implements IInfinityBlock {
    public BlockAgriCropPlant(Properties builder) {
        super(builder);
    }

    @Nonnull
    @Override
    public String getInternalName() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
