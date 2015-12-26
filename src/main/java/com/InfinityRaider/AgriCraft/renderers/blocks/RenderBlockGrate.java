package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockBase;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBlockGrate extends RenderBlockCustomWood<TileEntityGrate> {
    public RenderBlockGrate() {
        super(Blocks.blockGrate, new TileEntityGrate(), true);
    }

    @Override
    protected void renderInInventory(ItemStack item, Object... data) {
        TessellatorV2 tessellator = TessellatorV2.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
/*
        drawScaledPrism(tessellator, 7, 0, 1, 9, 16, 3, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 5, 9, 16, 7, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 9, 9, 16, 11, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 13, 9, 16, 15, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 1, 0, 9, 3, 16, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 5, 0, 9, 7, 16, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 9, 0, 9, 11, 16, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 13, 0, 9, 15, 16, COLOR_MULTIPLIER_STANDARD);
*/
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, IBlockState state, BlockBase block, TileEntity tile, int modelId, float f) {
        if(tile==null || !(tile instanceof TileEntityGrate)) {
            return false;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        TextureAtlasSprite texture = grate.getTexture(state, null);
        tessellator.startDrawingQuads();
        int cm = block.colorMultiplier(world, pos);
        Block vines = net.minecraft.init.Blocks.vine;
        float offset = ((float) grate.getOffset()*7)/16.0F;
        int orientation = grate.getOrientationValue();

        if(orientation == 0) {
            tessellator.addTranslation(0, 0, offset);

            drawScaledPrism(tessellator, 1, 0, 0, 3, 16, 2, cm, texture);
            drawScaledPrism(tessellator, 5, 0, 0, 7, 16, 2, cm, texture);
            drawScaledPrism(tessellator, 9, 0, 0, 11, 16, 2, cm, texture);
            drawScaledPrism(tessellator, 13, 0, 0, 15, 16, 2, cm, texture);
            drawScaledPrism(tessellator, 0, 1, 0, 16, 3, 2, cm, texture);
            drawScaledPrism(tessellator, 0, 5, 0, 16, 7, 2, cm, texture);
            drawScaledPrism(tessellator, 0, 9, 0, 16, 11, 2, cm, texture);
            drawScaledPrism(tessellator, 0, 13, 0, 16, 15, 2, cm, texture);

            tessellator.addTranslation(0, 0, -offset);
        }
        else if(orientation == 1) {
            tessellator.addTranslation(offset, 0, 0);

            drawScaledPrism(tessellator, 0, 0, 1, 2, 16, 3, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 5, 2, 16, 7, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 9, 2, 16, 11, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 13, 2, 16, 15, cm, texture);
            drawScaledPrism(tessellator, 0, 1, 0, 2, 3, 16, cm, texture);
            drawScaledPrism(tessellator, 0, 5, 0, 2, 7, 16, cm, texture);
            drawScaledPrism(tessellator, 0, 9, 0, 2, 11, 16, cm, texture);
            drawScaledPrism(tessellator, 0, 13, 0, 2, 15, 16, cm, texture);

            tessellator.addTranslation(-offset, 0, 0);
        } else {
            tessellator.addTranslation(0, offset, 0);

            drawScaledPrism(tessellator, 0, 0, 1, 16, 2, 3, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 5, 16, 2, 7, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 9, 16, 2, 11, cm, texture);
            drawScaledPrism(tessellator, 0, 0, 13, 16, 2, 15, cm, texture);
            drawScaledPrism(tessellator, 1, 0, 0, 3, 2, 16, cm, texture);
            drawScaledPrism(tessellator, 5, 0, 0, 7, 2, 16, cm, texture);
            drawScaledPrism(tessellator, 9, 0, 0, 11, 2, 16, cm, texture);
            drawScaledPrism(tessellator, 13, 0, 0, 15, 2, 16, cm, texture);

            tessellator.addTranslation(0, -offset, 0);
        }

        //vines
        int l = vines.colorMultiplier(world, pos);
        float f0 = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        tessellator.setColorOpaque_F(f0, f1, f2);
        if(grate.hasVines(true)) {
            drawVines(tessellator, true, offset, orientation);
        }
        if(grate.hasVines(false)) {
            drawVines(tessellator, false, offset, orientation);
        }
        tessellator.setColorOpaque_F(1, 1, 1);
        tessellator.draw();

        return true;
    }

    private void drawVines(TessellatorV2 tessellator, boolean front, float offset, int orientation) {
        float pos = offset + (front?-0.001F:2* Constants.UNIT+0.001F);
        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();;
        if(orientation == 0) {
            drawScaledFaceDoubleXY(tessellator, 0, 0, 16, 16, pos, texture);
        }
        else if(orientation == 1) {
            drawScaledFaceDoubleYZ(tessellator, 0, 0, 16, 16, pos, texture);
        }
        else {
            drawScaledFaceDoubleXZ(tessellator, 0, 0, 16, 16, pos, texture);
        }
    }
}
