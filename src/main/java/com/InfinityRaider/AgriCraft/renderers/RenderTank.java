package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.sun.prism.util.tess.Tess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTank extends TileEntitySpecialRenderer{
    private ResourceLocation[] baseTexture = {new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/tankWood.png"), new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/tankIron.png")};
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
            }
            else if(tank.getBlockMetadata()==1) {
                this.drawIronTank(tank, tessellator);
            }
            //draw the waterTexture
            if(tank.getFluidLevel()>0) {
                this.drawWater(tank, tessellator);
            }
        GL11.glPopMatrix();

    }

    private void drawWoodTank(TileEntityTank tank, Tessellator tessellator) {
        double unit = Constants.unit;
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(baseTexture[tank.getBlockMetadata()]);
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
        Minecraft.getMinecraft().renderEngine.bindTexture(baseTexture[tank.getBlockMetadata()]);
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
        float y;
        if(layer==0) {
            y = unit + (((float) tank.getFluidLevel())/tank.getSingleCapacity())*(1.000F-unit);
        }
        else {
            y = ((float) tank.getFluidLevel())/tank.getSingleCapacity();
        }
        if(y>layer && y <=layer+1) {
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(this.waterTexture);
            tessellator.startDrawingQuads();
                tessellator.addVertexWithUV(0, y, 0, 0, 0);
                tessellator.addVertexWithUV(0, y, 1, 0, 1);
                tessellator.addVertexWithUV(1, y, 1, 1, 1);
                tessellator.addVertexWithUV(1, y, 0, 1, 0);
            tessellator.draw();
        }
    }
}
