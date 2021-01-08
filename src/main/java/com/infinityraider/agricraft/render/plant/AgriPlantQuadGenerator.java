package com.infinityraider.agricraft.render.plant;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AgriPlantQuadGenerator implements IAgriPlantQuadGenerator {
    private static final AgriPlantQuadGenerator INSTANCE = new AgriPlantQuadGenerator();

    public static AgriPlantQuadGenerator getInstance() {
        return INSTANCE;
    }

    public List<BakedQuad> bakeQuadsForCrossPattern(ResourceLocation sprite) {
        //TODO
        return ImmutableList.of();
    }

    public List<BakedQuad> bakeQuadsForHashPattern(ResourceLocation sprite) {
        //TODO
        return ImmutableList.of();
    }
}
