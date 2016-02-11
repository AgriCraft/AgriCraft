package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class PlantRenderer {
    private static final RenderUtil renderUtil = RenderUtil.getInstance();

    public static void renderPlant(WorldRenderer renderer, IBlockAccess world, BlockPos pos, IBlockState state, int growthStage, ICropPlant plant) {
        TextureAtlasSprite iconA = plant.getPrimaryPlantTexture(growthStage);
        TextureAtlasSprite iconB = plant.getSecondaryPlantTexture(growthStage);
        if(iconA!=null) {
            TessellatorV2 tessellator = TessellatorV2.getInstance(renderer);
            tessellator.setBrightness(Blocks.wheat.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
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

    public static void renderHashTagPattern(TessellatorV2 tessellator, TextureAtlasSprite icon, int layer) {
        int minY = 16*layer;
        int maxY = 16*(layer+1);
        //plane 1 front
        addScaledVertexWithUV(tessellator, 0, minY, 4, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 0, maxY, 4, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, maxY, 4, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 16, minY, 4, 0, 16, icon);
        //plane 1 back
        addScaledVertexWithUV(tessellator, 0, minY, 4, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 16, minY, 4, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, maxY, 4, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 0, maxY, 4, 16, 0, icon);
        //plane 2 front
        addScaledVertexWithUV(tessellator, 4, minY, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4, minY, 16, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 4, maxY, 16, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4, maxY, 0, 0, 0, icon);
        //plane 2 back
        addScaledVertexWithUV(tessellator, 4, minY, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4, maxY, 0, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 4, maxY, 16, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4, minY, 16, 16, 16, icon);
        //plane 3 front
        addScaledVertexWithUV(tessellator, 0, minY, 12, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, minY, 12, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 16, maxY, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 0, maxY, 12, 0, 0, icon);
        //plane 3 back
        addScaledVertexWithUV(tessellator, 0, minY, 12, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 0, maxY, 12, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 16, maxY, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, minY, 12, 16, 16, icon);
        //plane 4 front
        addScaledVertexWithUV(tessellator, 12, minY, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12, minY, 0, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12, maxY, 0, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12, maxY, 16, 0, 0, icon);
        //plane 4 back
        addScaledVertexWithUV(tessellator, 12, minY, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12, maxY, 16, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12, maxY, 0, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12, minY, 0, 16, 16, icon);
    }

    public static void renderCrossPattern(TessellatorV2 tessellator, TextureAtlasSprite icon, int layer) {
        int minY = 12*layer;
        int maxY = 12*(layer+1);
        //plane 1 front right
        addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 0, 16, icon);
        //plane 1 front left
        addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        //plane 1 back right
        addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 16, 0, icon);
        //plane 1 back left
        addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        //plane 2 front right
        addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        //plane 2 front left
        addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 0, 0, icon);
        //plane 2 back right
        addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        //plane 2 back right
        addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 16, 16, icon);
        //plane 3 front right
        addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        //plane 3 front left
        addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 0, 0, icon);
        //plane 3 back right
        addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        //plane 3 back left
        addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 16, 16, icon);
        //plane 4 front right
        addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 0, 0, icon);
        //plane 4 front left
        addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, minY, -2, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        //plane 4 back right
        addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 16, 16, icon);
        //plane 4 back left
        addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, minY,-2, 16, 16, icon);
    }

    public static void renderStemPlant(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos, TextureAtlasSprite vineIcon, TextureAtlasSprite fruitIcon, int growhtStage, Block vine) {
        int translation = growhtStage>=6?0:5-growhtStage;
        tessellator.setBrightness(vine.getMixedBrightnessForBlock(world, pos));
        int l = vine.getRenderColor(vine.getStateFromMeta(growhtStage));
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        tessellator.setColorOpaque_F(f, f1, f2);
        tessellator.translate(0, -Constants.UNIT*2*translation, 0);
        if(growhtStage >= Constants.MATURE) {
            //render the vines
            renderStemPattern(tessellator, vineIcon);
            //render the block
            if(fruitIcon != null) {
                drawScaledPrism(tessellator, 7, 0, 2, 11, 4, 6, fruitIcon);
                drawScaledPrism(tessellator, 10, 0, 7, 14, 4, 11, fruitIcon);
                drawScaledPrism(tessellator, 5, 0, 10, 9, 4, 14, fruitIcon);
                drawScaledPrism(tessellator, 2, 0, 5, 6, 4, 9, fruitIcon);
            }
        }
        else {
            //render the vines
            renderCrossPattern(tessellator, vineIcon, 0);
        }
        tessellator.translate(0, Constants.UNIT*2*translation, 0);
    }

    public static void renderStemPattern(TessellatorV2 tessellator, TextureAtlasSprite icon) {
        int minY = 0;
        int maxY = 12;
        //plane 1 front left
        addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        //plane 1 back left
        addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 16, 0, icon);
        //plane 2 front right
        addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        //plane 2 back right
        addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 16, 16, icon);
        //plane 3 front right
        addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        //plane 3 back right
        addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 16, 16, icon);
        //plane 4 front left
        addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, minY, -2, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        //plane 4 back left
        addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12.001F, minY,-2, 16, 16, icon);
    }

    public static void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v, TextureAtlasSprite icon) {
        renderUtil.addScaledVertexWithUV(tessellator, x, y, z, u, v, icon);
    }

    public static void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        renderUtil.drawScaledPrism(tessellator, minX, minY, minZ, maxX, maxY, maxZ, icon);
    }
}
