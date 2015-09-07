package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFenceGate;
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

public class RenderBlockFenceGate extends RenderBlockCustomWood<TileEntityFenceGate> {
    public RenderBlockFenceGate() {
        super(Blocks.blockFenceGate, new TileEntityFenceGate(), true);
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

        drawScaledPrism(tessellator, 7, 5, 0, 9, 16, 2, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 5, 14, 9, 16, 16, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 12, 2, 9, 15, 14, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 6, 2, 9, 9, 14, icon, COLOR_MULTIPLIER_STANDARD);
        drawScaledPrism(tessellator, 7, 9, 6, 9, 12, 10, icon, COLOR_MULTIPLIER_STANDARD);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return false;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        int cm = block.colorMultiplier(world, (int) x, (int) y, (int) z);
        IIcon icon = gate.getIcon();
        if(gate.isZAxis()) {
            drawScaledPrism(tessellator, 0, 5, 7, 2, 16, 9, icon, cm);
            drawScaledPrism(tessellator, 14, 5, 7, 16, 16, 9, icon, cm);
            if(!gate.isOpen()) {
                drawScaledPrism(tessellator, 2, 12, 7, 14, 15, 9, icon, cm);
                drawScaledPrism(tessellator, 2, 6, 7, 14, 9, 9, icon, cm);
                drawScaledPrism(tessellator, 6, 9, 7, 10, 12, 9, icon, cm);
            } else {
                if(gate.getOpenDirection()>0) {
                    drawScaledPrism(tessellator, 0, 12, 1, 2, 15, 7, icon, cm);
                    drawScaledPrism(tessellator, 14, 12, 1, 16, 15, 7, icon, cm);
                    drawScaledPrism(tessellator, 0, 6, 1, 2, 9, 7, icon, cm);
                    drawScaledPrism(tessellator, 14, 6, 1, 16, 9, 7, icon, cm);
                    drawScaledPrism(tessellator, 0, 9, 1, 2, 12, 3, icon, cm);
                    drawScaledPrism(tessellator, 14, 9, 1, 16, 12, 3, icon, cm);
                } else {
                    drawScaledPrism(tessellator, 0, 12, 9, 2, 15, 15, icon, cm);
                    drawScaledPrism(tessellator, 14, 12, 9, 16, 15, 15, icon, cm);
                    drawScaledPrism(tessellator, 0, 6, 9, 2, 9, 15, icon, cm);
                    drawScaledPrism(tessellator, 14, 6, 9, 16, 9, 15, icon, cm);
                    drawScaledPrism(tessellator, 0, 9, 13, 2, 12, 15, icon, cm);
                    drawScaledPrism(tessellator, 14, 9, 13, 16, 12, 15, icon, cm);
                }
            }
        } else {
            drawScaledPrism(tessellator, 7, 5, 0, 9, 16, 2, icon, cm);
            drawScaledPrism(tessellator, 7, 5, 14, 9, 16, 16, icon, cm);
            if(!gate.isOpen()) {
                drawScaledPrism(tessellator, 7, 12, 2, 9, 15, 14, icon, cm);
                drawScaledPrism(tessellator, 7, 6, 2, 9, 9, 14, icon, cm);
                drawScaledPrism(tessellator, 7, 9, 6, 9, 12, 10, icon, cm);
            } else {
                if(gate.getOpenDirection()>0) {
                    drawScaledPrism(tessellator, 1, 12, 0, 7, 15, 2, icon, cm);
                    drawScaledPrism(tessellator, 1, 12, 14, 7, 15, 16, icon, cm);
                    drawScaledPrism(tessellator, 1, 6, 0, 7, 9, 2, icon, cm);
                    drawScaledPrism(tessellator, 1, 6, 14, 7, 9, 16, icon, cm);
                    drawScaledPrism(tessellator, 1, 9, 0, 3, 12, 2, icon, cm);
                    drawScaledPrism(tessellator, 1, 9, 14, 3, 12, 16, icon, cm);
                } else {
                    drawScaledPrism(tessellator, 9, 12, 0, 15, 15, 2, icon, cm);
                    drawScaledPrism(tessellator, 9, 12, 14, 15, 15, 16, icon, cm);
                    drawScaledPrism(tessellator, 9, 6, 0, 15, 9, 2, icon, cm);
                    drawScaledPrism(tessellator, 9, 6, 14, 15, 9, 16, icon, cm);
                    drawScaledPrism(tessellator, 13, 9, 0, 15, 12, 2, icon, cm);
                    drawScaledPrism(tessellator, 13, 9, 14, 15, 12, 16, icon, cm);
                }
            }
        }
        return true;
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
