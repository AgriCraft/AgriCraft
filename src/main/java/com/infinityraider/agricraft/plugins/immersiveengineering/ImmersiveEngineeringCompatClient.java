package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRenderFunction;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;

public class ImmersiveEngineeringCompatClient {
    public static void registerRenderFunction() {
        ClocheRenderFunction.RENDER_FUNCTION_FACTORIES.put(
                AgriCraft.instance.getModId(),
                (block) -> {
                    if (block == AgriCraft.instance.getModBlockRegistry().crop_plant) {
                        return AgriRenderFunction.INSTANCE;
                    } else {
                        throw new IllegalArgumentException("Block " + block.getTranslationKey() + " is not an agricraft crop");
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public static void injectPlantQuads(IAgriPlant plant, IAgriGrowthStage growth, Consumer<?> quadConsumer) {
        if(AgriCraft.instance.proxy().getPhysicalSide().isClient()) {
            bakeQuads(plant, growth).forEach((Consumer<Object>) quadConsumer);
        }
    }

    public static List<?> bakeQuads(IAgriPlant plant, IAgriGrowthStage growth) {
        return getAgriClochePlantRenderer().getQuads(plant, growth);
    }

    @OnlyIn(Dist.CLIENT)
    public static AgriClochePlantRenderer getAgriClochePlantRenderer() {
        return AgriClochePlantRenderer.getInstance();
    }
}
