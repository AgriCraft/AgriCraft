package com.infinityraider.agricraft.api.v1.event;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public abstract class JournalRenderEvent extends Event {

    private final JournalViewPointHandler.IPageRenderer pageRenderer;
    private final MatrixStack matrixStack;
    private final IAgriPlant plant;

    protected JournalRenderEvent(JournalViewPointHandler.IPageRenderer pageRenderer, MatrixStack matrixStack, IAgriPlant plant) {
        this.pageRenderer = pageRenderer;
        this.matrixStack = matrixStack;
        this.plant = plant;
    }

    public JournalViewPointHandler.IPageRenderer getPageRenderer() {
        return pageRenderer;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public IAgriPlant getPlant() {
        return plant;
    }

    public static class PostDrawGrothStages extends JournalRenderEvent {

        private final List<TextureAtlasSprite> stages;

        public PostDrawGrothStages(JournalViewPointHandler.IPageRenderer pageRenderer, MatrixStack matrixStack, IAgriPlant plant, List<TextureAtlasSprite> stages) {
            super(pageRenderer, matrixStack, plant);
            this.stages = stages;
        }

        public List<TextureAtlasSprite> getStages() {
            return stages;
        }
    }
}
