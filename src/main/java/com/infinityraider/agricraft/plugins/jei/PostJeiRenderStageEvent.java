package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.eventbus.api.Event;

public class PostJeiRenderStageEvent extends Event {

    private final PoseStack matrixStack;
    private final IAgriPlant plant;
    private final IAgriGrowthStage stage;
    private final AgriIngredientPlant.Renderer renderer;
    private final int x;
    private final int y;

    public PostJeiRenderStageEvent(PoseStack matrixStack, int x, int y, IAgriPlant plant, IAgriGrowthStage stage, AgriIngredientPlant.Renderer renderer) {
        this.matrixStack = matrixStack;
        this.x = x;
        this.y = y;
        this.plant = plant;
        this.stage = stage;
        this.renderer = renderer;
    }

    public PoseStack getMatrixStack() {
        return matrixStack;
    }

    public IAgriPlant getPlant() {
        return plant;
    }

    public IAgriGrowthStage getStage() {
        return stage;
    }

    public AgriIngredientPlant.Renderer getRenderer() {
        return renderer;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
