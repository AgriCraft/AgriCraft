package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
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
    TileEntitySpecialRenderer renderer;
    private TileEntity tileEntity;

    public RenderItemTank(TileEntitySpecialRenderer renderer, TileEntity tileEntity) {
        this.renderer = renderer;
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
        double unit = Constants.unit;
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(tank.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            //draw first plane front
            tessellator.addVertexWithUV(0, 1, 1, 0, 0);
            tessellator.addVertexWithUV(0, 0, 1, 0, 1);
            tessellator.addVertexWithUV(1, 0, 1, 1, 1);
            tessellator.addVertexWithUV(1, 1, 1, 1, 0);
            //draw first plane back
            tessellator.addVertexWithUV(0, 1, 1 - unit*2, 0, 0);
            tessellator.addVertexWithUV(1, 1, 1 - unit*2, 1, 0);
            tessellator.addVertexWithUV(1, 0, 1 - unit*2, 1, 1);
            tessellator.addVertexWithUV(0, 0, 1 - unit*2, 0, 1);
            //draw first plane top
            tessellator.addVertexWithUV(0, 1, 1-unit*2, 0, 1-unit*2);
            tessellator.addVertexWithUV(0, 1, 1, 0, 1);
            tessellator.addVertexWithUV(1, 1, 1, 1, 1);
            tessellator.addVertexWithUV(1, 1, 1 - unit*2, 1, 1-unit*2);

            //draw second plane front
            tessellator.addVertexWithUV(1, 1, 1, 0, 0);
            tessellator.addVertexWithUV(1, 0, 1, 0, 1);
            tessellator.addVertexWithUV(1, 0, 0, 1, 1);
            tessellator.addVertexWithUV(1, 1, 0, 1, 0);
            //draw second plane back
            tessellator.addVertexWithUV(1 - unit*2, 1, 1, 0, 0);
            tessellator.addVertexWithUV(1 - unit*2, 1, 0, 1, 0);
            tessellator.addVertexWithUV(1 - unit*2, 0, 0, 1, 1);
            tessellator.addVertexWithUV(1 - unit*2, 0, 1, 0, 1);
            //draw second plane top
            tessellator.addVertexWithUV(1 - unit*2, 1, 0, 1-unit*2, 0);
            tessellator.addVertexWithUV(1 - unit*2, 1, 1, 1-unit*2, 1);
            tessellator.addVertexWithUV(1, 1, 1, 1, 1);
            tessellator.addVertexWithUV(1, 1, 0, 1, 0);

            //draw third plane front
            tessellator.addVertexWithUV(1, 1, 0, 0, 0);
            tessellator.addVertexWithUV(1, 0, 0, 0, 1);
            tessellator.addVertexWithUV(0, 0, 0, 1, 1);
            tessellator.addVertexWithUV(0, 1, 0, 1, 0);
            //draw third plane back
            tessellator.addVertexWithUV(1, 1, unit*2, 0, 0);
            tessellator.addVertexWithUV(0, 1, unit*2, 1, 0);
            tessellator.addVertexWithUV(0, 0, unit*2, 1, 1);
            tessellator.addVertexWithUV(1, 0, unit*2, 0, 1);
            //draw third plane top
            tessellator.addVertexWithUV(0, 1, 0, 0, 0);
            tessellator.addVertexWithUV(0, 1, unit*2, 0, unit*2);
            tessellator.addVertexWithUV(1, 1, unit*2, 1, unit*2);
            tessellator.addVertexWithUV(1, 1, 0, 1, 0);

            //draw fourth plane front
            tessellator.addVertexWithUV(0, 1, 0, 0, 0);
            tessellator.addVertexWithUV(0, 0, 0, 0, 1);
            tessellator.addVertexWithUV(0, 0, 1, 1, 1);
            tessellator.addVertexWithUV(0, 1, 1, 1, 0);
            //draw fourth plane back
            tessellator.addVertexWithUV(unit*2, 1, 0, 0, 0);
            tessellator.addVertexWithUV(unit*2, 1, 1, 1, 0);
            tessellator.addVertexWithUV(unit*2, 0, 1, 1, 1);
            tessellator.addVertexWithUV(unit*2, 0, 0, 0, 1);
            //draw fourth plane top
            tessellator.addVertexWithUV(0, 1, 0, 0, 0);
            tessellator.addVertexWithUV(0, 1, 1, 0, 1);
            tessellator.addVertexWithUV(unit*2, 1, 1, unit*2, 1);
            tessellator.addVertexWithUV(unit*2, 1, 0, unit*2, 0);

            //draw bottom plane front
            tessellator.addVertexWithUV(0, 0, 0, 0, 0);
            tessellator.addVertexWithUV(1, 0, 0, 0, 1);
            tessellator.addVertexWithUV(1, 0, 1, 1, 1);
            tessellator.addVertexWithUV(0, 0, 1, 1, 0);
            //draw bottom plane back
            tessellator.addVertexWithUV(0, unit, 0, 0, 0);
            tessellator.addVertexWithUV(0, unit, 1, 1, 0);
            tessellator.addVertexWithUV(1, unit, 1, 1, 1);
            tessellator.addVertexWithUV(1, unit, 0, 0, 1);
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
