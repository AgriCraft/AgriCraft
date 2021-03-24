package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockIrrigationChannelHollow extends BlockIrrigationChannelAbstract {
    public BlockIrrigationChannelHollow() {
        super(Names.Blocks.CHANNEL_HOLLOW, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Nonnull
    @Override
    public ItemIrrigationChannelHollow asItem() {
        return AgriCraft.instance.getModItemRegistry().channel_hollow;
    }
}
