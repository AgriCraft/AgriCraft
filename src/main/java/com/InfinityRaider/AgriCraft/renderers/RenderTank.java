package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
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
        double unit = Constants.unit;
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(tank.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord, tank.zCoord+1))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord+1, tank.yCoord, tank.zCoord))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord, tank.zCoord-1))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord-1, tank.yCoord, tank.zCoord))) {
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
            }
            if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord-1, tank.zCoord))) {
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
            }
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void drawIronTank(TileEntityTank tank, Tessellator tessellator) {
        double unit = Constants.unit;
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
        float unit = Constants.unit;
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
                    tessellator.addVertexWithUV(0, unit + (1.0000F-unit)*y - 0.0001F, 0, 0, 0);
                    tessellator.addVertexWithUV(0, unit + (1.0000F-unit)*y - 0.0001F, 1, 0, 1);
                    tessellator.addVertexWithUV(1, unit + (1.0000F-unit)*y - 0.0001F, 1, 1, 1);
                    tessellator.addVertexWithUV(1, unit + (1.0000F-unit)*y - 0.0001F, 0, 1, 0);
                tessellator.draw();
            }
            else {
                tessellator.startDrawingQuads();
                    tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord));
                    tessellator.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
                    tessellator.addVertexWithUV(0, y - 0.001, 0, 0, 0);
                    tessellator.addVertexWithUV(0, y - 0.001, 1, 0, 1);
                    tessellator.addVertexWithUV(1, y - 0.001, 1, 1, 1);
                    tessellator.addVertexWithUV(1, y - 0.001, 0, 1, 0);
                tessellator.draw();
            }
        }
    }
}
