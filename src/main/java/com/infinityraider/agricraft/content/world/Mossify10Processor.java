package com.infinityraider.agricraft.content.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

public class Mossify10Processor {
    private static final StructureProcessor INSTANCE = new RuleProcessor(ImmutableList.of(
            new ProcessorRule(
                    new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState())
    ));

    public static StructureProcessor getInstance() {
        return INSTANCE;
    }
}
