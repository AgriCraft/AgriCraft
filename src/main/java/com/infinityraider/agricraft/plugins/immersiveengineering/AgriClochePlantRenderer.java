package com.infinityraider.agricraft.plugins.immersiveengineering;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.render.models.AgriPlantModelBridge;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AgriClochePlantRenderer {
    private static final AgriClochePlantRenderer INSTANCE = new AgriClochePlantRenderer();

    public static AgriClochePlantRenderer getInstance() {
        return INSTANCE;
    }

    private AgriClochePlantRenderer() {}

    public List<BakedQuad> getQuads(IAgriPlant plant, IAgriGrowthStage stage) {
        return AgriPlantModelBridge.getOrBakeQuads(plant, stage, null);
    }
}
