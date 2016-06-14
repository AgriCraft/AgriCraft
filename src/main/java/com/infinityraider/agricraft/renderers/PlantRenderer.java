package com.infinityraider.agricraft.renderers;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorBakedQuad;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@SideOnly(Side.CLIENT)
public abstract class PlantRenderer {

    public static void renderPlant(IBlockAccess world, BlockPos pos, int growthStage, IAgriCraftPlant plant) {
        ITessellator tessellator = TessellatorBakedQuad.getInstance();
        TextureAtlasSprite iconA = tessellator.getIcon(plant.getPrimaryPlantTexture(growthStage));
        TextureAtlasSprite iconB = tessellator.getIcon(plant.getSecondaryPlantTexture(growthStage));
        if(iconA!=null) {
            tessellator.setBrightness(RenderUtil.getMixedBrightness(world, pos, Blocks.wheat.getDefaultState()));
            tessellator.translate(pos);
            switch (plant.getRenderMethod()) {
                case CROSSED:
                    renderCrossPattern(tessellator, iconA, 0);
                    break;
                case HASHTAG:
                    renderHashTagPattern(tessellator, iconA, 0);
                    break;
                case STEM:
                    renderStemPlant(tessellator, world, pos, iconA, iconB, growthStage, plant.getBlock());
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
        int minY = 16*layer;
        int maxY = 16*(layer+1);
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(0, minY, 16, maxY, EnumFacing.EAST, icon, 12);
		tessellator.draw();
    }

    public static void renderCrossPattern(ITessellator tessellator, TextureAtlasSprite icon, int layer) {
        int minY = 12*layer;
        int maxY = 12*(layer+1);
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 3.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 4.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 3.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 4.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 11.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 12.001F);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 11.999F);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 12.001F);
		tessellator.draw();
    }

    public static void renderStemPlant(ITessellator tessellator, IBlockAccess world, BlockPos pos, TextureAtlasSprite vineIcon, TextureAtlasSprite fruitIcon, int growhtStage, Block vine) {
        int translation = growhtStage>=6?0:5-growhtStage;
        /*
        tessellator.setBrightness(RenderUtil.getMixedBrightness(world, pos, Blocks.vine.getDefaultState()));
        int l = vine.getRenderColor(vine.getStateFromMeta(growhtStage));
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f, f1, f2);
        */
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.translate(0, -Constants.UNIT*2*translation, 0);
        if(growhtStage >= Constants.MATURE) {
            //render the vines
            renderStemPattern(tessellator, vineIcon);
            //render the block
            if(fruitIcon != null) {
                tessellator.drawScaledPrism(7, 0, 2, 11, 4, 6, fruitIcon);
                tessellator.drawScaledPrism(10, 0, 7, 14, 4, 11, fruitIcon);
                tessellator.drawScaledPrism(5, 0, 10, 9, 4, 14, fruitIcon);
                tessellator.drawScaledPrism(2, 0, 5, 6, 4, 9, fruitIcon);
            }
        }
        else {
            //render the vines
            renderCrossPattern(tessellator, vineIcon, 0);
        }
        tessellator.translate(0, Constants.UNIT * 2 * translation, 0);
		tessellator.draw();
    }

    public static void renderStemPattern(ITessellator tessellator, TextureAtlasSprite icon) {
        int minY = 0;
        int maxY = 12;
		tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.NORTH, icon, 4);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.EAST, icon, 4);
        tessellator.drawScaledFaceDouble(6, minY, 18, maxY, EnumFacing.NORTH, icon, 12);
        tessellator.drawScaledFaceDouble(-2, minY, 10, maxY, EnumFacing.EAST, icon, 12);
		tessellator.draw();
    }
}
