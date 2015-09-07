package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockPeripheral;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderPeripheral extends RenderBlockBase {
    public RenderPeripheral() {
        super(Blocks.blockPeripheral, new TileEntityPeripheral(), true);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if(callFromTESR) {

        } else {
            renderISBRHstuff(tessellator, (BlockPeripheral) block, block.colorMultiplier(world, (int) x, (int) y, (int) z));
        }
        return false;
    }

    private void renderISBRHstuff(Tessellator tessellator2, BlockPeripheral blockPeripheral, int colorMultiplier) {
        IIcon iconFull = blockPeripheral.getIcon(0, 0);
        IIcon iconHollow = blockPeripheral.getIcon(1, 0);
        IIcon iconHalf = blockPeripheral.getIcon(2, 0);
        drawBase(tessellator2, iconFull, iconHollow, colorMultiplier);
        drawTop(tessellator2, iconFull, colorMultiplier);
        drawDiagonals(tessellator2, iconHalf);
    }

    private void drawBase(Tessellator tessellator, IIcon iconFull, IIcon iconHollow, int colorMultiplier) {
        drawScaledFaceDoubleXZ(tessellator, 0, 0, 16, 16, iconFull, 0);
        drawScaledFaceDoubleXY(tessellator, 0, 0, 16, 14, iconHollow, 0);
        drawScaledFaceDoubleXY(tessellator, 0, 0, 16, 14, iconHollow, 1);
        drawScaledFaceDoubleYZ(tessellator, 0, 0, 14, 16, iconHollow, 0);
        drawScaledFaceDoubleYZ(tessellator, 0, 0, 14, 16, iconHollow, 1);
        /*
        drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon, colorMultiplier);
        drawScaledPrism(tessellator, 0, 1, 0, 2, 14, 2, icon, colorMultiplier);
        drawScaledPrism(tessellator, 14, 2, 0, 16, 14, 2, icon, colorMultiplier);
        drawScaledPrism(tessellator, 14, 2, 14, 16, 14, 16, icon, colorMultiplier);
        drawScaledPrism(tessellator, 0, 2, 14, 2, 14, 16, icon, colorMultiplier);
        */
    }

    private void drawTop(Tessellator tessellator, IIcon icon, int colorMultiplier) {
        drawScaledPrism(tessellator, 4, 14, 4, 12, 16, 12, icon, colorMultiplier);

        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 4, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 4, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 4, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 4, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 16, icon);

        addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 12, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 12, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);

        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 16, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 4, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 4, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 4, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 16, 16, 16, icon);

        addScaledVertexWithUV(tessellator, 16, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 4, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 16, 14, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 4, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 12, 16, 12, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);
    }

    private void drawDiagonals(Tessellator tessellator, IIcon icon) {
        addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16, icon);

        addScaledVertexWithUV(tessellator, 0, 0, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 0, 0, 16, 16, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 0, icon);

        addScaledVertexWithUV(tessellator, 0, 0, 16, 0, 16, icon);
        addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0, icon);
        addScaledVertexWithUV(tessellator, 16, 0, 0, 16, 16, icon);

    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();

        renderISBRHstuff(tessellator, (BlockPeripheral) Blocks.blockPeripheral, COLOR_MULTIPLIER_STANDARD);

        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
