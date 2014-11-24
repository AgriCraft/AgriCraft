package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderChannel  extends TileEntitySpecialRenderer {
    private ResourceLocation waterTexture = new ResourceLocation("minecraft:textures/blocks/water_still.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        TileEntityChannel channel = (TileEntityChannel) tileEntity;
        Tessellator tessellator = Tessellator.instance;
        //render the model
        GL11.glPushMatrix();
            //translate the matrix to the right spot
            GL11.glTranslated(x,y,z);
            //draw the channel
            if(channel.getBlockMetadata()==0) {
                this.renderWoodChannel(channel, tessellator);
                //draw the waterTexture
                if(channel.getFluidLevel()>0) {
                    this.drawWater(channel, tessellator);
                }
            }
            else if(channel.getBlockMetadata()==1) {
                this.renderIronChannel(channel, tessellator);
            }
        GL11.glPopMatrix();
    }

    private void renderWoodChannel(TileEntityChannel channel, Tessellator tessellator) {
        this.renderBottom(channel, tessellator);
        this.renderSide(channel, tessellator, 'x', -1);
        this.renderSide(channel, tessellator, 'x', 1);
        this.renderSide(channel, tessellator, 'z', -1);
        this.renderSide(channel, tessellator, 'z', 1);
    }

    private void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        float unit = Constants.unit;
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            //draw first plane front
            tessellator.addVertexWithUV(unit*4, unit*5, unit*4, unit*4, unit*4);
            tessellator.addVertexWithUV(unit*4, unit*5, 1-unit*4, unit*4, 1-unit*4);
            tessellator.addVertexWithUV(1-unit*4, unit*5, 1-unit*4, 1-unit*4, 1-unit*4);
            tessellator.addVertexWithUV(1-unit*4, unit*5, unit*4, 1-unit*4, unit*4);
            //draw first plane back
            tessellator.addVertexWithUV(unit*4, unit*4, unit*4, unit*4, unit*4);
            tessellator.addVertexWithUV(1-unit*4, unit*4, unit*4, 1-unit*4, unit*4);
            tessellator.addVertexWithUV(1-unit*4, unit*4, 1-unit*4, 1-unit*4, 1-unit*4);
            tessellator.addVertexWithUV(unit*4, unit*4, 1-unit*4, unit*4, 1-unit*4);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        boolean neighbour = channel.hasNeighbour(axis, direction);
        boolean x = axis=='x';
        switch(axis) {
            case('x'): this.renderSideX(channel, tessellator, direction, neighbour);break;
            //case('z'): this.renderSideZ(channel, tessellator, direction, neighbour);break;
        }
    }

    private void renderSideX(TileEntityChannel channel, Tessellator tessellator, int direction, boolean neighbour) {
        float unit = Constants.unit;    //1 block = 16 units
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            if(neighbour) {
                //extend bottom plane
                    //draw bottom plane front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*5, unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*5, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*5, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*5, unit*4, (10+direction*6)*unit, unit*4);
                    //draw bottom plane back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                //draw side edges
                    //draw first edge front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    //draw first edge back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 1-unit*5, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 1-unit*5, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*5, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*5, 6*(direction+1)*unit, unit*4);
                    //draw first edge top
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 11*unit, 6*(direction+1)*unit, 11*unit);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 12*unit, 6*(direction+1)*unit, 12*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 12*unit, (10+direction*6)*unit, 12*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 11*unit, (10+direction*6)*unit, 11*unit);
                    //draw second edge front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, unit*5, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*5, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*5, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, unit*5, (10+direction*6)*unit, 1-unit*4);
                    //draw second edge back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*4, 6*(direction+1)*unit, unit*4);
                    //draw second edge top
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 4*unit, 6*(direction+1)*unit, 4*unit);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 5*unit, 6*(direction+1)*unit, 5*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 5*unit, (10+direction*6)*unit, 5*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 4*unit, (10+direction*6)*unit, 4*unit);
            }
            else {
                //draw an edge
                    //draw edge front
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, 1-unit*4, unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, unit*4, 1-unit*4, 1-unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, unit*4, unit*4, unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, unit*4, unit*4, unit*4);
                    //draw edge back
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, unit*4, 1-unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, unit*4, unit*4, 1-unit*4, unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, unit*4, 1-unit*4, unit*4, unit*4);
                    //draw edge top
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, unit*4, (7.5F+3.5F*direction)*unit, unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, (7.5F+3.5F*direction)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, (8.5F+3.5F*direction)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, unit*4, (8.5F+3.5F*direction)*unit, unit*4);
            }
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderSideZ(TileEntityChannel channel, Tessellator tessellator, int direction, boolean neighbour) {
        float unit = Constants.unit;    //1 block = 16 units
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            if(neighbour) {
                //extend bottom plane
                    //draw bottom plane front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*5, unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*5, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*5, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*5, unit*4, (10+direction*6)*unit, unit*4);
                    //draw bottom plane back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                //draw side edges
                    //draw first edge front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 1-unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*4, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 1-unit*4, (10+direction*6)*unit, 1-unit*4);
                    //draw first edge back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 1-unit*5, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 1-unit*5, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, 1-unit*5, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, 1-unit*5, 6*(direction+1)*unit, unit*4);
                    //draw first edge top
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 11*unit, 6*(direction+1)*unit, 11*unit);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 12*unit, 6*(direction+1)*unit, 12*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 12*unit, (10+direction*6)*unit, 12*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 11*unit, (10+direction*6)*unit, 11*unit);
                    //draw second edge front
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, unit*5, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*5, 6*(direction+1)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*5, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, unit*5, (10+direction*6)*unit, 1-unit*4);
                    //draw second edge back
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, unit*4, 6*(direction+1)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, unit*4, (10+direction*6)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((10+direction*6)*unit, unit*4, unit*4, (10+direction*6)*unit, unit*4);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, unit*4, unit*4, 6*(direction+1)*unit, unit*4);
                    //draw second edge top
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 4*unit, 6*(direction+1)*unit, 4*unit);
                    tessellator.addVertexWithUV(6*(direction+1)*unit, 1-unit*4, 5*unit, 6*(direction+1)*unit, 5*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 5*unit, (10+direction*6)*unit, 5*unit);
                    tessellator.addVertexWithUV((10+direction*6)*unit, 1-unit*4, 4*unit, (10+direction*6)*unit, 4*unit);
            }
            else {
                //draw an edge
                    //draw edge front
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, 1-unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, unit*4, 1-unit*4,  unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, unit*4, unit*4, unit*4, unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, unit*4, 1-unit*4, unit*4);
                    //draw edge back
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, 1-unit*4, 1-unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, unit*4, 1-unit*4, unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, unit*4, unit*4, unit*4, unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, unit*4, 1-unit*4, unit*4, 1-unit*4);
                    //draw edge top
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, unit*4, (7.5F+3.5F*direction)*unit, unit*4);
                    tessellator.addVertexWithUV((7.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, (7.5F+3.5F*direction)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, 1-unit*4, (8.5F+3.5F*direction)*unit, 1-unit*4);
                    tessellator.addVertexWithUV((8.5F+3.5F*direction)*unit, 1-unit*4, unit*4, (8.5F+3.5F*direction)*unit, unit*4);
        }
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderIronChannel(TileEntityChannel channel, Tessellator tessellator) {

    }

    private void drawWater(TileEntityChannel channel, Tessellator tessellator) {

    }
}
