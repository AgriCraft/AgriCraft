package com.infinityraider.agricraft.api.v1.content;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AgriTags {
    public static final class Blocks extends AgriTags {
        public static final TagKey<Block> GREENHOUSE_GLASS
                = BlockTags.create(new ResourceLocation("agricraft", "greenhouse_glass"));
    }

    private AgriTags() {
        throw new IllegalStateException("THOU SHALL NOT INIT");
    }
}
