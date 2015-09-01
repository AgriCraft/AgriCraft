package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public abstract class PlantRenderer {
    public static void renderPlantLayer(int x, int y, int z, RenderBlocks renderer, int renderType, IIcon icon, int layer) {
        if(icon!=null) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.addTranslation(x, y, z);
            tessellator.setBrightness(Blocks.wheat.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            if (renderType != 6) {
                renderCrossPattern(tessellator, icon, layer);
            } else {
                renderHashTagPattern(tessellator, icon, layer);
            }
            tessellator.addTranslation(-x, -y, -z);
        }
    }

    private static void renderHashTagPattern(Tessellator tessellator, IIcon icon, int layer) {
        int minY = 16*layer;
        int maxY = 16*(layer+1);
        //plane 1 front
        RenderHelper.addScaledVertexWithUV(tessellator, 0, minY, 4, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 0, maxY, 4, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, maxY, 4, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, minY, 4, 0, 16, icon);
        //plane 1 back
        RenderHelper.addScaledVertexWithUV(tessellator, 0, minY, 4, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, minY, 4, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, maxY, 4, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 0, maxY, 4, 16, 0, icon);
        //plane 2 front
        RenderHelper.addScaledVertexWithUV(tessellator, 4, minY, 0, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, minY, 16, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, maxY, 16, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, maxY, 0, 0, 0, icon);
        //plane 2 back
        RenderHelper.addScaledVertexWithUV(tessellator, 4, minY, 0, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, maxY, 0, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, maxY, 16, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, minY, 16, 16, 16, icon);
        //plane 3 front
        RenderHelper.addScaledVertexWithUV(tessellator, 0, minY, 12, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, minY, 12, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, maxY, 12, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 0, maxY, 12, 0, 0, icon);
        //plane 3 back
        RenderHelper.addScaledVertexWithUV(tessellator, 0, minY, 12, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 0, maxY, 12, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, maxY, 12, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 16, minY, 12, 16, 16, icon);
        //plane 4 front
        RenderHelper.addScaledVertexWithUV(tessellator, 12, minY, 16, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, minY, 0, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, maxY, 0, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, maxY, 16, 0, 0, icon);
        //plane 4 back
        RenderHelper.addScaledVertexWithUV(tessellator, 12, minY, 16, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, maxY, 16, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, maxY, 0, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, minY, 0, 16, 16, icon);
    }

    private static void renderCrossPattern(Tessellator tessellator, IIcon icon, int layer) {
        int minY = 12*layer;
        int maxY = 12*(layer+1);
        //plane 1 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 0, 16, icon);
        //plane 1 front left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        //plane 1 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 16, 0, icon);
        //plane 1 back left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        //plane 2 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        //plane 2 front left
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 0, 0, icon);
        //plane 2 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        //plane 2 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 16, 16, icon);
        //plane 3 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        //plane 3 front left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 0, 0, icon);
        //plane 3 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        //plane 3 back left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 16, 16, icon);
        //plane 4 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 0, 0, icon);
        //plane 4 front left
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, -2, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        //plane 4 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 16, 16, icon);
        //plane 4 back left
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY,-2, 16, 16, icon);
    }

    public static void renderStemPlant(int x, int y, int z, RenderBlocks renderer, IIcon icon, int meta, Block vine, Block block, boolean mature) {
        Tessellator tessellator = Tessellator.instance;
        int translation = meta>=6?0:5-meta;
        tessellator.setBrightness(vine.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        int l = vine.getRenderColor(meta);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f, f1, f2);
        tessellator.addTranslation(x, y - Constants.UNIT * 2 * translation, z);
        //render the vines
        if(mature) {
            renderStemPattern(tessellator, icon);
        }
        else {
            renderCrossPattern(tessellator, icon, 0);
        }
        tessellator.addTranslation(-x, -y+Constants.UNIT*2*translation, -z);
        //render the block
        if(mature) {
            float u = Constants.UNIT;
            boolean renderFacesSetting = renderer.renderAllFaces;
            renderer.renderAllFaces = true;

            renderer.setRenderBounds(7*u, 0, 2*u, 11*u, 4*u, 6*u);
            renderer.renderStandardBlock(block, x, y, z);

            renderer.setRenderBounds(10*u, 0, 7*u, 14*u, 4*u, 11*u);
            renderer.renderStandardBlock(block, x, y, z);

            renderer.setRenderBounds(5*u, 0, 10*u, 9*u, 4*u, 14*u);
            renderer.renderStandardBlock(block, x, y, z);

            renderer.setRenderBounds(2*u, 0, 5*u, 6*u, 4*u, 9*u);
            renderer.renderStandardBlock(block, x, y, z);

            renderer.renderAllFaces = renderFacesSetting;
        }
    }

    private static void renderStemPattern(Tessellator tessellator, IIcon icon) {
        int minY = 0;
        int maxY = 12;
        //plane 1 front left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        //plane 1 back left
        RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        //plane 2 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        //plane 2 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        //plane 3 front right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        //plane 3 back right
        RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        //plane 4 front left
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, -2, 16, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        //plane 4 back left
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY,-2, 16, 16, icon);
    }
}
