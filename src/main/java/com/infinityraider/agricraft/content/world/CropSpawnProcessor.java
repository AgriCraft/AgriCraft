package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.infinityraider.agricraft.content.core.TileEntityCrop;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CropSpawnProcessor extends StructureProcessor {
    private static final String FIELD_GENOME = "IL_FIELD_0";
    private static final String FIELD_GROWTH = "IL_FIELD_1";

    public static final Codec<CropSpawnProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("structure", new ResourceLocation("empty")).forGetter(CropSpawnProcessor::getStructure))
            .apply(instance, instance.stable(CropSpawnProcessor::new))
    );

    private final ResourceLocation structure;

    public CropSpawnProcessor(ResourceLocation structure) {
        this.structure = structure;
    }

    public ResourceLocation getStructure() {
        return this.structure;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return AgriCraft.instance.getStructureRegistry().cropSpawnProcessor;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader world, BlockPos structureOrigin, BlockPos parentOrigin, StructureTemplate.StructureBlockInfo templateInfo, StructureTemplate.StructureBlockInfo worldInfo, StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        // check if the block is a crop block
        BlockState state = worldInfo.state;
        if (!(state.getBlock() instanceof BlockCrop)) {
            return worldInfo;
        }
        // check if the crop block is just single crop sticks
        BlockCrop blockCrop = (BlockCrop) state.getBlock();
        if (!blockCrop.hasCropSticks(state) || blockCrop.hasCrossSticks(state) || blockCrop.hasPlantOrWeeds(state)) {
            return worldInfo;
        }
        // set the nbt to load to the tile entity
        if (worldInfo.nbt.contains(FIELD_GENOME, Tag.TAG_COMPOUND) && worldInfo.nbt.contains(FIELD_GROWTH, Tag.TAG_COMPOUND)) {
            boolean planted = AgriApi.getWorldGenPlantManager().generateGenomeFor(this.getStructure(), settings.getRandom(worldInfo.pos)).map(genome -> {
                TileEntityCrop.GENOME_WRITER.accept(Optional.of(genome), worldInfo.nbt.getCompound(FIELD_GENOME));
                TileEntityCrop.GROWTH_WRITER.accept(genome.getPlant().getInitialGrowthStage(), worldInfo.nbt.getCompound(FIELD_GROWTH));
                return true;
            }).orElse(false);
            // if a plant has been set, we must also set the block state to indicate that there is a plant
            if(planted) {
                return new StructureTemplate.StructureBlockInfo(worldInfo.pos, blockCrop.applyPlant(state), worldInfo.nbt.copy());
            } else {
                AgriCraft.instance.getLogger().debug("Skipped generation of random plant as none are registered for " + this.getStructure());
            }
        }
        // return the world info
        return worldInfo;
    }
}
