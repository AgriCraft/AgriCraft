package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemTank implements IItemRenderer {
    private TileEntity tileEntity;

    public RenderItemTank(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TileEntityTank tank = (TileEntityTank) tileEntity;
        tank.setMaterial(item.getTagCompound());
        this.renderModel(tank, 0.0, 0.0, 0.0, item.getItemDamage());
    }

    public void renderModel(TileEntityTank tank, double x, double y, double z, int meta) {
        Tessellator tessellator = Tessellator.instance;
        //render the model
        GL11.glPushMatrix();
            //translate the matrix to the right spot
            GL11.glTranslated(x,y,z);
            //draw the tank
            if(meta==0) {
                this.drawWoodTank(tank, tessellator);
            }
            else if(meta==1) {
                this.drawIronTank(tank, tessellator);
            }
        GL11.glPopMatrix();
    }

    private void drawWoodTank(TileEntityTank tank, Tessellator tessellator) {
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(tank.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            //draw first plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 0);
            //draw first plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 14, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 14, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 14, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 14, 0, 16);
            //draw first plane top
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 14, 0, 14);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 14, 16, 14);
            //draw second plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
            //draw second plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 16, 0, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 0, 16, 0, 16);
            //draw second plane top
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 16, 0, 14, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 14, 16, 16, 14, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
            //draw third plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 0, 16, 0);
            //draw third plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 2, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 2, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 2, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 2, 0, 16);
            //draw third plane top
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 2, 0, 2);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 2, 16, 2);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
            //draw fourth plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 16, 16, 0);
            //draw fourth plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 0, 0, 0, 16);
            //draw fourth plane top
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 16, 16, 2, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 2, 16, 0, 2, 0);
            //draw bottom plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 0);
            //draw bottom plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 1, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 1, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 1, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 1, 0, 0, 16);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void drawIronTank(TileEntityTank tank, Tessellator tessellator) {
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(tank.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
