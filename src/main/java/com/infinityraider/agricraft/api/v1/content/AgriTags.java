package com.infinityraider.agricraft.api.v1.content;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class AgriTags {
    public static final class Blocks extends AgriTags {
        public static final ITag.INamedTag<Block> GREENHOUSE_GLASS
                = BlockTags.makeWrapperTag(new ResourceLocation("agricraft", "greenhouse_glass").toString());
    }

    private AgriTags() {
        throw new IllegalStateException("THOU SHALL NOT INIT");
    }
}
