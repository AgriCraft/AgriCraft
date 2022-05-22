package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
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
public class GreenHouseProcessor extends StructureProcessor {
    private static final GreenHouseProcessor INSTANCE = new GreenHouseProcessor();

    public static GreenHouseProcessor getInstance() {
        return INSTANCE;
    }

    public static final Codec<GreenHouseProcessor> CODEC = Codec.unit(GreenHouseProcessor::getInstance);

    @Override
    protected StructureProcessorType<?> getType() {
        return AgriCraft.instance.getStructureRegistry().greenHouseProcessor;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos structureOrigin, BlockPos parentOrigin, StructureTemplate.StructureBlockInfo templateInfo, StructureTemplate.StructureBlockInfo worldInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        if(worldInfo.state.getBlock() == AgriBlockRegistry.getInstance().getGreenHouseMonitorBlock()) {
            //TODO
        }
        return worldInfo;
    }
}
