package com.infinityraider.agricraft.render.plant.gui;

import com.agricraft.agricore.templates.AgriJournalTexture;
import com.agricraft.agricore.templates.AgriPlant;
import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class JsonPlantGuiRenderer implements IAgriGrowableGuiRenderer.WithSeed {
    private final Map<Integer, ResourceLocation> overrides;

    public JsonPlantGuiRenderer(AgriPlant plant) {
        this.overrides = plant.getTexture().getJournalOverrides().stream().collect(Collectors.toMap(
                AgriJournalTexture::getStage,
                texture -> new ResourceLocation(texture.getTexture())
        ));
    }

    @Override
    public void drawGrowthStage(IAgriGrowable plant, IAgriGrowthStage stage, RenderContext context, PoseStack transforms, float x, float y, float w, float h) {
        // If there are no overrides, go back to default
        if(this.overrides.isEmpty()) {
            IAgriGrowableGuiRenderer.WithSeed.super.drawGrowthStage(plant, stage, context, transforms, x, y, w, h);
            return;
        }
        // Get the growth index
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        // If the growth stage does not follow incremental growth logic, go back to default
        if(index < 0) {
            IAgriGrowableGuiRenderer.WithSeed.super.drawGrowthStage(plant, stage, context, transforms, x, y, w, h);
            return;
        }
        // Fetch the override texture
        ResourceLocation override = this.overrides.get(index);
        // If there is no override for this growth stage, go back to default
        if(override == null) {
            IAgriGrowableGuiRenderer.WithSeed.super.drawGrowthStage(plant, stage, context, transforms, x, y, w, h);
            return;
        }
        // Render the override
        context.draw(transforms, context.getSprite(override), x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
