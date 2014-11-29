package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
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
            this.renderBottom(tank, tessellator);
            this.renderSide(tank, tessellator, 'x', -1);
            this.renderSide(tank, tessellator, 'x', 1);
            this.renderSide(tank, tessellator, 'z', -1);
            this.renderSide(tank, tessellator, 'z', 1);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderBottom(TileEntityTank tank, Tessellator tessellator) {
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
    }

    private void renderSide(TileEntityTank tank, Tessellator tessellator, char axis, int direction) {
        if ((axis == 'x' || axis == 'z') && (direction == 1 || direction == -1)) {
            boolean x = axis=='x';
            if(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))instanceof TileEntityChannel && ((TileEntityChannel) tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))).isSameMaterial(tank)) {
                //draw plane front top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(9+7*direction), 0, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 11, x?0:(9+7*direction), 0, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 11, x?16:(9+7*direction), 16, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 16, x?16:(9+7*direction), 16, 0);
                //draw plane front left
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 11, x?0:(9+7*direction), 0, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 5, x?0:(9+7*direction), 0, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?5:(9+7*direction), 5, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?5:(9+7*direction), 5, 5);
                //draw plane front bottom
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 5, x?0:(9+7*direction), 0, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 0, x?0:(9+7*direction), 0, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 0, x?16:(9+7*direction), 16, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 5, x?16:(9+7*direction), 16, 11);
                //draw plane front right
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 11, x?11:(9+7*direction), 11, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 5, x?11:(9+7*direction), 11, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 5, x?16:(9+7*direction), 16, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 11, x?16:(9+7*direction), 16, 5);
                //draw plane back top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 16, x?0:(7+7*direction), 0, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(7+7*direction), 16, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 11, x?16:(7+7*direction), 16, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 11, x?0:(7+7*direction), 0, 5);
                //draw plane back left
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 11, x?0:(7+7*direction), 0, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 11, x?5:(7+7*direction), 5, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 5, x?5:(7+7*direction), 5, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 5, x?0:(7+7*direction), 0, 11);
                //draw plane back bottom
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 5, x?0:(7+7*direction), 0, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 5, x?16:(7+7*direction), 16, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 0, x?16:(7+7*direction), 16, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 0, x?0:(7+7*direction), 0, 16);
                //draw plane back right
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?11:(7+7*direction), 11, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 11, x?16:(7+7*direction), 16, 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 5, x?16:(7+7*direction), 16, 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?11:(7+7*direction), 11, 11);
                //draw hole bottom plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?5:(7+7*direction), x?(7+7*direction):5, x?5:(7+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?11:(9+7*direction), x?(7+7*direction):5, x?11:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?11:(9+7*direction), x?(9+7*direction):11, x?11:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?5:(7+7*direction), x?(9+7*direction):11, x?5:(7+7*direction));
                //draw hole right plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 11, x?5:(7+7*direction), (7+7*direction), 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 5, x?5:(7+7*direction), (7+7*direction), 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?5:(9+7*direction), (9+7*direction), 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?5:(9+7*direction), (9+7*direction), 5);
                //draw hole top plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?5:(7+7*direction), x?(7+7*direction):5, x?5:(7+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?5:(7+7*direction), x?(9+7*direction):11, x?5:(7+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?11:(9+7*direction), x?(9+7*direction):11, x?11:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?11:(9+7*direction), x?(7+7*direction):5, x?11:(9+7*direction));
                //draw hole left plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?11:(7+7*direction), (7+7*direction), 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 11, x?11:(9+7*direction), (9+7*direction), 5);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 5, x?11:(9+7*direction), (9+7*direction), 11);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?11:(7+7*direction), (7+7*direction), 11);
                //draw plane top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(7+7*direction), x?(7+7*direction):0, x?0:(7+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(9+7*direction), x?(7+7*direction):0, x?16:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(9+7*direction), x?(9+7*direction):16, x?16:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(7+7*direction), x?(9+7*direction):16, x?0:(7+7*direction));
            }
            else if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction)))) {
                //draw front plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 16, x?16:(9+7*direction), 0, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 0, x?16:(9+7*direction), 0, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 0, x?0:(9+7*direction), 16, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(9+7*direction), 16, 0);
                //draw back plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(7+7*direction), 0, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 16, x?0:(7+7*direction), 16, 0);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 0, x?0:(7+7*direction), 16, 16);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 0, x?16:(7+7*direction), 0, 16);
                //draw top plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(7+7*direction), x?(7+7*direction):0, x?0:(7+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(9+7*direction), x?(7+7*direction):0, x?16:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(9+7*direction), x?(9+7*direction):16, x?16:(9+7*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(7+7*direction), x?(9+7*direction):16, x?0:(7+7*direction));
            }
        }
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
