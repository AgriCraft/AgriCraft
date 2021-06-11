package com.infinityraider.agricraft.plugins.botanypots;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.render.models.AgriPlantModelBridge;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BotanyPotsPlantRenderer implements IRenderUtilities {
    private static final BotanyPotsPlantRenderer INSTANCE = new BotanyPotsPlantRenderer();

    public static BotanyPotsPlantRenderer getInstance() {
        return INSTANCE;
    }

    private final Map<IRenderTypeBuffer.Impl, ThreadLocal<ITessellator>> tessellators;

    private BotanyPotsPlantRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    protected ITessellator getTessellator(IRenderTypeBuffer.Impl buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    public void renderPlant(IAgriPlant plant, IAgriGrowthStage stage, IAgriWeed weed, IAgriGrowthStage weedStage,
                            MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay, Direction... preferredSides) {
        if(buffer instanceof IRenderTypeBuffer.Impl) {
            ITessellator tessellator = this.getTessellator((IRenderTypeBuffer.Impl) buffer);
            tessellator.pushMatrix();

            tessellator.setBrightness(light);
            tessellator.setOverlay(overlay);
            tessellator.applyTransformation(matrix.getLast().getMatrix());

            tessellator.startDrawingQuads();
            tessellator.addQuads(this.fetchQuads(plant, stage, preferredSides));
            tessellator.addQuads(this.fetchQuads(weed, weedStage, preferredSides));
            tessellator.draw();

            tessellator.popMatrix();
        }
    }

    protected List<BakedQuad> fetchQuads(IAgriPlant plant, IAgriGrowthStage stage, Direction... sides) {
        List<BakedQuad> quads = Lists.newArrayList();
        for(Direction dir : sides) {
            quads.addAll(AgriPlantModelBridge.getOrBakeQuads(plant, stage, dir));
        }
        quads.addAll(AgriPlantModelBridge.getOrBakeQuads(plant, stage, null));
        return quads;
    }

    protected List<BakedQuad> fetchQuads(IAgriWeed weed, IAgriGrowthStage stage, Direction... sides) {
        List<BakedQuad> quads = Lists.newArrayList();
        for(Direction dir : sides) {
            quads.addAll(AgriPlantModelBridge.getOrBakeQuads(weed, stage, dir));
        }
        quads.addAll(AgriPlantModelBridge.getOrBakeQuads(weed, stage, null));
        return quads;
    }

    protected RenderType getRenderType() {
        return RenderType.getCutout();
    }
}
