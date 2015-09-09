package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderBlockGrate extends RenderBlockCustomWood<TileEntityGrate> {
    public RenderBlockGrate() {
        super(Blocks.blockGrate, new TileEntityGrate(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = teDummy.getIcon();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();

        drawScaledPrism(tessellator, 7, 0, 1, 9, 16, 3, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 5, 9, 16, 7, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 9, 9, 16, 11, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 0, 13, 9, 16, 15, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 1, 0, 9, 3, 16, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 5, 0, 9, 7, 16, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 9, 0, 9, 11, 16, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 13, 0, 9, 15, 16, icon, COLOR_MULTIPLIER_STANDARD);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if(tile==null || !(tile instanceof TileEntityGrate)) {
            return false;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        int cm = block.colorMultiplier(world, (int) x, (int) y, (int) z);
        IIcon icon = grate.getIcon();
        Block vines = net.minecraft.init.Blocks.vine;
        IIcon vinesIcon = vines.getBlockTextureFromSide(0);
        float offset = ((float) grate.getOffset()*7)/16.0F;
        int orientation = grate.getOrientationValue();

        if(orientation == 0) {
            tessellator.addTranslation(0, 0, offset);

            drawScaledPrism(tessellator, 1, 0, 0, 3, 16, 2, icon, cm);
            drawScaledPrism(tessellator, 5, 0, 0, 7, 16, 2, icon, cm);
            drawScaledPrism(tessellator, 9, 0, 0, 11, 16, 2, icon, cm);
            drawScaledPrism(tessellator, 13, 0, 0, 15, 16, 2, icon, cm);
            drawScaledPrism(tessellator, 0, 1, 0, 16, 3, 2, icon, cm);
            drawScaledPrism(tessellator, 0, 5, 0, 16, 7, 2, icon, cm);
            drawScaledPrism(tessellator, 0, 9, 0, 16, 11, 2, icon, cm);
            drawScaledPrism(tessellator, 0, 13, 0, 16, 15, 2, icon, cm);

            tessellator.addTranslation(0, 0, -offset);
        }
        else if(orientation == 1) {
            tessellator.addTranslation(offset, 0, 0);

            drawScaledPrism(tessellator, 0, 0, 1, 2, 16, 3, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 5, 2, 16, 7, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 9, 2, 16, 11, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 13, 2, 16, 15, icon, cm);
            drawScaledPrism(tessellator, 0, 1, 0, 2, 3, 16, icon, cm);
            drawScaledPrism(tessellator, 0, 5, 0, 2, 7, 16, icon, cm);
            drawScaledPrism(tessellator, 0, 9, 0, 2, 11, 16, icon, cm);
            drawScaledPrism(tessellator, 0, 13, 0, 2, 15, 16, icon, cm);

            tessellator.addTranslation(-offset, 0, 0);
        } else {
            tessellator.addTranslation(0, offset, 0);

            drawScaledPrism(tessellator, 0, 0, 1, 16, 2, 3, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 5, 16, 2, 7, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 9, 16, 2, 11, icon, cm);
            drawScaledPrism(tessellator, 0, 0, 13, 16, 2, 15, icon, cm);
            drawScaledPrism(tessellator, 1, 0, 0, 3, 2, 16, icon, cm);
            drawScaledPrism(tessellator, 5, 0, 0, 7, 2, 16, icon, cm);
            drawScaledPrism(tessellator, 9, 0, 0, 11, 2, 16, icon, cm);
            drawScaledPrism(tessellator, 13, 0, 0, 15, 2, 16, icon, cm);

            tessellator.addTranslation(0, -offset, 0);
        }

        //vines
        int l = vines.colorMultiplier(world, (int) x, (int) y, (int) z);
        float f0 = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;

        tessellator.setColorOpaque_F(f0, f1, f2);
        if(grate.hasVines(true)) {
            drawVines(tessellator, true, offset, orientation, vinesIcon);
        }
        if(grate.hasVines(false)) {
            drawVines(tessellator, false, offset, orientation, vinesIcon);
        }
        tessellator.setColorOpaque_F(1, 1, 1);

        return true;
    }

    private void drawVines(Tessellator tessellator, boolean front, float offset, int orientation, IIcon icon) {
        float pos = offset + (front?-0.001F:2* Constants.UNIT+0.001F);
        if(orientation == 0) {
            drawScaledFaceDoubleXY(tessellator, 0, 0, 16, 16, icon, pos);
        }
        else if(orientation == 1) {
            drawScaledFaceDoubleYZ(tessellator, 0, 0, 16, 16, icon, pos);
        }
        else {
            drawScaledFaceDoubleXZ(tessellator, 0, 0, 16, 16, icon, pos);
        }
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
