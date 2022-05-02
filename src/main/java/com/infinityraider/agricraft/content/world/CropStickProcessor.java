package com.infinityraider.agricraft.content.world;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CropStickProcessor extends StructureProcessor {
    private static final Type TYPE = new Type();
    private static final CropStickProcessor INSTANCE = new CropStickProcessor();

    public static CropStickProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos pos1, BlockPos pos2, StructureTemplate.StructureBlockInfo blockInfo1, StructureTemplate.StructureBlockInfo blockInfo2, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        return super.process(world, pos1, pos2, blockInfo1, blockInfo2, settings, template);
    }

    public static class Type implements StructureProcessorType<CropStickProcessor> {
        @Override
        public Codec<CropStickProcessor> codec() {
            return Codec.unit(CropStickProcessor::getInstance);
        }
    }
}
