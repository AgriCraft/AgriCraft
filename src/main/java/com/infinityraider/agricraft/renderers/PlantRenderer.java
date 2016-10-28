package com.infinityraider.agricraft.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;

@SideOnly(Side.CLIENT)
public abstract class PlantRenderer {

    public static void renderPlant(IAgriPlant plant, int growthStage) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance();
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        renderPlant(tessellator, plant, growthStage);
        tessellator.draw();
    }

    public static void renderPlant(ITessellator tessellator, IAgriPlant plant, int growthStage) {
        TextureAtlasSprite iconA = tessellator.getIcon(plant.getPrimaryPlantTexture(growthStage));
        TextureAtlasSprite iconB = tessellator.getIcon(plant.getSecondaryPlantTexture(growthStage));

        if (iconA != null) {
            switch (plant.getRenderMethod()) {
                case CROSSED:
                    renderCrossPattern(tessellator, iconA, 0);
                    break;
                case HASHTAG:
                    renderHashTagPattern(tessellator, iconA, 0);
                    break;
                case STEM:
                    renderStemPlant(tessellator, iconA, iconB, growthStage, plant.getBlock());
                    break;
                case TALL_CROSSED:
                    renderCrossPattern(tessellator, iconA, 0);
                    if (iconB != null) {
                        renderCrossPattern(tessellator, iconA, 1);
                    }
                    break;
                case TALL_HASHTAG:
                    renderHashTagPattern(tessellator, iconA, 0);
                    if (iconB != null) {
                        renderHashTagPattern(tessellator, iconA, 1);
                    }
                    break;
            }
        }
    }

    public static void renderHashTagPattern(ITessellator tessellator, TextureAtlasSprite icon, int layer) {
        int minY = 16 * layer;
        int maxY = 16 * (layer + 1);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.EAST, icon, 12);
    }

    public static void renderCrossPattern(ITessellator tessellator, TextureAtlasSprite icon, int layer) {
        int minY = 12 * layer;
        int maxY = 12 * (layer + 1);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 3.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 4.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 3.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 4.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 11.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 12.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 11.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 12.001F);
    }

    public static void renderStemPlant(ITessellator tessellator, TextureAtlasSprite vineIcon, TextureAtlasSprite fruitIcon, int stage, Block vine) {
        int translation = stage >= 6 ? 0 : 5 - stage;
        tessellator.pushMatrix();
        tessellator.translate(0, -Constants.UNIT * 2 * translation, 0);
        if (stage >= Constants.MATURE) {
            //render the vines
            renderStemPattern(tessellator, vineIcon);
            //render the block
            if (fruitIcon != null) {
                tessellator.drawScaledPrism(7, 0, 2, 11, 4, 6, fruitIcon);
                tessellator.drawScaledPrism(10, 0, 7, 14, 4, 11, fruitIcon);
                tessellator.drawScaledPrism(5, 0, 10, 9, 4, 14, fruitIcon);
                tessellator.drawScaledPrism(2, 0, 5, 6, 4, 9, fruitIcon);
            }
        } else {
            //render the vines
            renderCrossPattern(tessellator, vineIcon, 0);
        }
        tessellator.popMatrix();
    }

    public static void renderStemPattern(ITessellator tessellator, TextureAtlasSprite icon) {
        int minY = 0;
        int maxY = 12;
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 12);
    }

}
