package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
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
    public static final Codec<CropStickProcessor> CODEC = Codec.unit(CropStickProcessor::getInstance);

    private static final CropStickProcessor INSTANCE = new CropStickProcessor();

    public static CropStickProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return AgriCraft.instance.getStructureRegistry().cropStickProcessor;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos pos1, BlockPos pos2, StructureTemplate.StructureBlockInfo blockInfo1, StructureTemplate.StructureBlockInfo blockInfo2, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        return super.process(world, pos1, pos2, blockInfo1, blockInfo2, settings, template);
    }
}
