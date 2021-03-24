package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockIrrigationChannelNormal extends BlockIrrigationChannelAbstract {
    public BlockIrrigationChannelNormal() {
        super(Names.Blocks.CHANNEL, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Nonnull
    @Override
    public ItemIrrigationChannel asItem() {
        return AgriCraft.instance.getModItemRegistry().channel;
    }
}
