package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemValve implements IItemRenderer {
    private TileEntity tileEntity;

    public RenderItemValve(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        TileEntityValve valve = (TileEntityValve) tileEntity;
        valve.setMaterial(item.getTagCompound());
        this.renderModel(valve, 0.0, 0.0, 0.0, item.getItemDamage());
    }

    public void renderModel(TileEntityValve valve, double x, double y, double z, int meta) {
        Tessellator tessellator = Tessellator.instance;
        //render the model
        GL11.glPushMatrix();
        //translate the matrix to the right spot
        GL11.glTranslated(x,y,z);
        //draw the tank
        if(meta==0) {
            this.drawWoodChannel(valve, tessellator);
        }
        else if(meta==1) {
            this.drawIronChannel(valve, tessellator);
        }
        GL11.glPopMatrix();
    }

    private void drawWoodChannel(TileEntityValve valve, Tessellator tessellator) {
        IIcon icon = valve.getIcon();
        float f = Constants.unit;
        this.renderBottom(tessellator, icon);
        this.renderSide(tessellator, 'x', -1, icon);
        this.renderSide(tessellator, 'x', 1, icon);
        this.renderSide(tessellator, 'z', -1, icon);
        this.renderSide(tessellator, 'z', 1, icon);

        //render the iron valves
        IIcon ironIcon = Blocks.iron_block.getIcon(0, 0);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        RenderHelper.drawScaledPrism(tessellator, 5, 11.5f, 0.001f, 11, 15.001f, 1.999f, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 5, 0.999f, 0.001f, 11, 5.5f, 1.999f, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 5, 11.5f, 14.001f, 11, 15.001f, 15.999f, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 5, 0.999f, 14.001f, 11, 5.5f, 15.999f, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, ironIcon);
        RenderHelper.drawScaledPrism(tessellator, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, ironIcon);

        //render the wooden guide rails along x-axis
        RenderHelper.drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon);
        tessellator.addTranslation(6*f, 0, 0);
        RenderHelper.drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon);
        tessellator.addTranslation(0, 0, 14*f);
        RenderHelper.drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon);
        tessellator.addTranslation(-6*f, 0, 0);
        RenderHelper.drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon);
        tessellator.addTranslation(0, 0, -14 * f);

        //render the wooden guide rails along z-axis
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.addTranslation(0, 0, 6*f);
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.addTranslation(14*f, 0, 0);
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.addTranslation(0, 0, -6*f);
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.addTranslation(-14*f, 0, 0);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderBottom(Tessellator tessellator, IIcon icon) {
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        //draw first plane front
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 4, 4, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 12, 4, 12, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 12, 12, 12, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 4, 12, 4, icon);
        //draw first plane back
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 4, 4, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 4,4, 12, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, 12, 12, 12, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 12, 4, 12, icon);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected void renderSide(Tessellator tessellator, char axis, int direction, IIcon icon) {
        if ((axis == 'x' || axis == 'z') && (direction == 1 || direction == -1)) {
            boolean x = axis == 'x';
            //disable lighting
            GL11.glDisable(GL11.GL_LIGHTING);
            //tell the tessellator to start drawing
            tessellator.startDrawingQuads();
            //draw bottom plane front
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 6 * (direction + 1) : 4, 5, x ? 4 : (6 + 6 * direction), x ? 6 * (direction + 1) : 4, x ? 4 : (6 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 6 * (direction + 1) : 4, 5, x ? 12 : (10 + 6 * direction), x ? 6 * (direction + 1) : 4, x ? 12 : (10 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 5, x ? 12 : (10 + 6 * direction), x ? (10.5F + direction * 5.5F) : 12, x ? 12 : (10 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 5, x ? 4 : (6 + 6 * direction), x ? (10.5F + direction * 5.5F) : 12, x ? 4 : (6 + 6 * direction), icon);
            //draw bottom plane back
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 6 * (direction + 1) : 4, 4, x ? 4 : (6 + 6 * direction), x ? 6 * (direction + 1) : 4, x ? 4 : (6 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 4, x ? 4 : (6 + 6 * direction), x ? (10.5F + direction * 5.5F) : 12, x ? 4 : (6 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 4, x ? 12 : (10 + 6 * direction), x ? (10.5F + direction * 5.5F) : 12, x ? 12 : (10 + 6 * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 6 * (direction + 1) : 4, 4, x ? 12 : (10 + 6 * direction), x ? 6 * (direction + 1) : 4, x ? 12 : (10 + 6 * direction), icon);
            //draw side edges
            //draw first edge front
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (1 + direction) : 4, 12, x ? 12 : 5.5F * (1 + direction), 5.5F * (direction + 1), 4, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 4, 4, x ? 12 : 5.5F * (1 + direction), 5.5F * (direction + 1), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 4, 4, x ? 12 : (10.5F + 5.5F * direction), (10.5F + direction * 5.5F), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 4, 12, x ? 12 : (10.5F + 5.5F * direction), (10.5F + direction * 5.5F), 4, icon);
            //draw first edge back
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 5, 12, x ? 11 : (10.5F + 5.5F * direction), x ? 5.5F * (direction + 1) : (16 - (10.5F + 5.5F * direction)), 4, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 5, x ? 12 : 4, x ? 11 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : (16 - (10.5F + 5.5F * direction)), x ? 4 : 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 5, 4, x ? 11 : 5.5F * (1 + direction), x ? (10.5F + direction * 5.5F) : (16 - 5.5F * (1 + direction)), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 5, x ? 4 : 12, x ? 11 : 5.5F * (1 + direction), x ? 5.5F * (direction + 1) : (16 - 5.5F * (1 + direction)), x ? 12 : 4, icon);
            //draw first edge top
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 5, 12, x ? 11 : (5.5F * (1 + direction)), x ? 5.5F * (direction + 1) : 5, x ? 11 : (5.5F * (1 + direction)), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 4, 12, x ? 12 : (5.5F * (1 + direction)), x ? 5.5F * (direction + 1) : 4, x ? 12 : (5.5F * (1 + direction)), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 4, 12, x ? 12 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : 4, x ? 12 : (10.5F + 5.5F * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 5, 12, x ? 11 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : 5, x ? 11 : (10.5F + 5.5F * direction), icon);
            //draw second edge front
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 11, 12, x ? 5 : (5.5F * (1 + direction)), 5.5F * (direction + 1), 4, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 11, 4, x ? 5 : (5.5F * (1 + direction)), 5.5F * (direction + 1), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 11, 4, x ? 5 : (10.5F + 5.5F * direction), (10.5F + direction * 5.5F), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 11, 12, x ? 5 : (10.5F + 5.5F * direction), (10.5F + direction * 5.5F), 4, icon);
            //draw second edge back
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 12, 12, x ? 4 : (10.5F + 5.5F * direction), x ? 5.5F * (direction + 1) : (16 - (10.5F + 5.5F * direction)), 4, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, x ? 12 : 4, x ? 4 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : (16 - (10.5F + 5.5F * direction)), x ? 4 : 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 4, x ? 4 : (5.5F * (1 + direction)), x ? (10.5F + direction * 5.5F) : (16 - 5.5F * (1 + direction)), 12, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 12, x ? 4 : 12, x ? 4 : (5.5F * (1 + direction)), x ? 5.5F * (direction + 1) : (16 - 5.5F * (1 + direction)), x ? 12 : 4, icon);
            //draw second edge top
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 12, 12, x ? 4 : (5.5F * (1 + direction)), x ? 5.5F * (direction + 1) : 12, x ? 4 : (5.5F * (1 + direction)), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? 5.5F * (direction + 1) : 11, 12, x ? 5 : (5.5F * (1 + direction)), x ? 5.5F * (direction + 1) : 11, x ? 5 : (5.5F * (1 + direction)), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 11, 12, x ? 5 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : 11, x ? 5 : (10.5F + 5.5F * direction), icon);
            RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 12, 12, x ? 4 : (10.5F + 5.5F * direction), x ? (10.5F + direction * 5.5F) : 12, x ? 4 : (10.5F + 5.5F * direction), icon);
            tessellator.draw();
            //enable lighting
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void drawIronChannel(TileEntityValve valve, Tessellator tessellator) {
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(valve.getIcon()));
    }
}