package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.templates.AgriPlant;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import com.infinityraider.agricraft.render.plant.gui.JsonPlantGuiRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class JsonPlantClient extends JsonPlant {
    private final JsonPlantGuiRenderer guiRenderer;

    public JsonPlantClient(AgriPlant plant) {
        super(plant);
        this.guiRenderer = new JsonPlantGuiRenderer(plant);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(this.plant.getTexture().useModels()) {
            ResourceLocation rl = new ResourceLocation(this.plant.getTexture().getPlantModel(index));
            return AgriPlantQuadGenerator.getInstance().fetchQuads(rl);
        } else {
            List<ResourceLocation> textures = Arrays.stream(plant.getTexture().getPlantTextures(index))
                    .map(ResourceLocation::new)
                    .collect(Collectors.toList());
            return AgriPlantQuadGenerator.getInstance().bakeQuads(this, stage, this.plant.getTexture().getRenderType(),
                    face, textures);
        }
    }

    @Nonnull
    @Override
    public JsonPlantGuiRenderer getGuiRenderer() {
        return this.guiRenderer;
    }
}
