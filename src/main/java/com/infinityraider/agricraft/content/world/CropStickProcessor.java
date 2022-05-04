package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos structureOrigin, BlockPos parentOrigin, StructureTemplate.StructureBlockInfo templateInfo, StructureTemplate.StructureBlockInfo worldInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        // check if the block is a crop block
        BlockState state = worldInfo.state;
        if(!(state.getBlock() instanceof BlockCrop)) {
            return worldInfo;
        }
        // check if the crop block is just single crop sticks
        BlockCrop blockCrop = (BlockCrop) state.getBlock();
        if(!blockCrop.hasCropSticks(state) || blockCrop.hasCrossSticks(state) || blockCrop.hasPlantOrWeeds(state)) {
            return worldInfo;
        }
        // check if there is a tile entity and that it is a crop tile entity
        BlockEntity tile = world.getBlockEntity(worldInfo.pos);
        if(!(tile instanceof IAgriCrop)) {
            return worldInfo;
        }
        IAgriCrop crop = (IAgriCrop) tile;
        return super.process(world, structureOrigin, parentOrigin, templateInfo, worldInfo, settings, template);
    }
}
