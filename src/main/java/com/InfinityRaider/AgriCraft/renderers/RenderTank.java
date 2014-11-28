package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTank extends TileEntitySpecialRenderer{
   private ResourceLocation waterTexture = new ResourceLocation("minecraft:textures/blocks/water_still.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        TileEntityTank tank= (TileEntityTank) tileEntity;
        Tessellator tessellator = Tessellator.instance;
        //render the model
        GL11.glPushMatrix();
            //translate the matrix to the right spot
            GL11.glTranslated(x,y,z);
            //draw the tank
            if(tank.getBlockMetadata()==0) {
                this.drawWoodTank(tank, tessellator);
                //draw the waterTexture
                if(tank.getFluidLevel()>0) {
                    this.drawWater(tank, tessellator);
                }
            }
            else if(tank.getBlockMetadata()==1) {
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
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord, tank.zCoord+1))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord+1, tank.yCoord, tank.zCoord))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord, tank.zCoord-1))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord-1, tank.yCoord, tank.zCoord))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord-1, tank.zCoord))) {
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
            }
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

    private void drawWater(TileEntityTank tank, Tessellator tessellator) {
        int layer = tank.getYPosition();
        int area = tank.getXSize()*tank.getZSize();
        int waterLevel = (int) Math.floor(((float)tank.getFluidLevel()-0.1F)/((float)(tank.getSingleCapacity()*area)));
        //only render water on the relevant layer
        if(layer==waterLevel) {
            int yLevel = (tank.getFluidLevel()/area)-waterLevel*tank.getSingleCapacity();
            float y = ((float) yLevel)/((float) tank.getSingleCapacity());
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(this.waterTexture);
            //stolen from Vanilla code
            int l = Blocks.water.colorMultiplier(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            //draw the water
            if(layer==0) {
                tessellator.startDrawingQuads();
                    tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord));
                    tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
                    RenderHelper.addScaledVertexWithUV(tessellator, 0, 1+15*y - 0.0001F, 0, 0, 0);
                    RenderHelper.addScaledVertexWithUV(tessellator, 0, 1+15*y - 0.0001F, 16, 0, 16);
                    RenderHelper.addScaledVertexWithUV(tessellator, 16, 1+15*y - 0.0001F, 16, 16, 16);
                    RenderHelper.addScaledVertexWithUV(tessellator, 16, 1+15*y - 0.0001F, 0, 16, 0);
                tessellator.draw();
            }
            else {
                tessellator.startDrawingQuads();
                    tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord));
                    tessellator.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
                    RenderHelper.addScaledVertexWithUV(tessellator, 0, 16*y - 0.001F, 0, 0, 0);
                    RenderHelper.addScaledVertexWithUV(tessellator, 0, 16*y - 0.001F, 16, 0, 16);
                    RenderHelper.addScaledVertexWithUV(tessellator, 16, 16*y - 0.001F, 16, 16, 16);
                    RenderHelper.addScaledVertexWithUV(tessellator, 16, 16*y - 0.001F, 0, 16, 0);
                tessellator.draw();
            }
        }
    }
}
