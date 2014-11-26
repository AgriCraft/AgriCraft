package com.InfinityRaider.AgriCraft.renderers;

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
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            //draw first plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 4, 4, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 12, 4, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 12, 12, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 4, 12, 12);
            //draw first plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 4, 4, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4,4, 12, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, 12, 12, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 12, 4, 12);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            boolean neighbour = channel.hasNeighbour(axis, direction);
            boolean x = axis == 'x';
            switch (axis) {
                case ('x'):this.renderSideX(channel, tessellator, direction, neighbour);break;
                case ('z'):this.renderSideZ(channel, tessellator, direction, neighbour);break;
            }
        }
    }

    private void renderSideX(TileEntityChannel channel, Tessellator tessellator, int direction, boolean neighbour) {
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            if(neighbour) {
                //extend bottom plane
                    //draw bottom plane front
                    RenderHelper.addScaledVertexWithUV(tessellator, 6*(direction+1), 5, 4, 6*(direction+1), 4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 6*(direction+1), 5, 12, 6*(direction+1), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 5, 12, (10.5F+direction*5.5F), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 5, 4, (10.5F+direction*5.5F), 4);
                    //draw bottom plane back
                    RenderHelper.addScaledVertexWithUV(tessellator, 6*(direction+1),4, 4, 6*(direction+1), 4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 4, (10.5F+direction*5.5F), 4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 12, (10.5F+direction*5.5F), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 6*(direction+1),4, 12, 6*(direction+1), 12);
                //draw side edges
                    //draw first edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(1+direction), 12, 12, 5.5F*(direction+1), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 4, 12, 5.5F*(direction+1), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 12, (10.5F+direction*5.5F), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 12, (10.5F+direction*5.5F), 16-12);
                    //draw first edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 11, 5.5F*(direction+1), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 11, (10.5F+direction*5.5F), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 11, (10.5F+direction*5.5F), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 4, 11, 5.5F*(direction+1), 16-4);
                    //draw first edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 11, 5.5F*(direction+1), 11);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 12, 5.5F*(direction+1), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 12, (10.5F+direction*5.5F), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 11, (10.5F+direction*5.5F), 11);
                    //draw second edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 5, 5.5F*(direction+1), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 4, 5, 5.5F*(direction+1), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 5, (10.5F+direction*5.5F), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 5, (10.5F+direction*5.5F), 16-12);
                    //draw second edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 4, 5.5F*(direction+1), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 4, (10.5F+direction*5.5F), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 4, 4, (10.5F+direction*5.5F), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 4, 4, 5.5F*(direction+1), 16-4);
                    //draw second edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 4, 5.5F*(direction+1), 4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5.5F*(direction+1), 12, 5, 5.5F*(direction+1), 5);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 5, (10.5F+direction*5.5F), 5);
                    RenderHelper.addScaledVertexWithUV(tessellator, (10.5F+direction*5.5F), 12, 4, (10.5F+direction*5.5F), 4);
            }
            else {
                //draw an edge (Z-fighting here, needs work)
                    //draw edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 12, 12, 12, 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 4, 12, 12, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 4, 4, 4, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 12, 4, 4, 16-4);
                    //draw edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 12, 12, 4, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 12, 4, 12, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 4, 4, 12, 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 4, 12, 4, 16-4);
                    //draw edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 12, 4, (7.5F+3.5F*direction), 4);
                    RenderHelper.addScaledVertexWithUV(tessellator, (7.5F+3.5F*direction), 12, 12, (7.5F+3.5F*direction), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 12, 12, (8.5F+3.5F*direction), 12);
                    RenderHelper.addScaledVertexWithUV(tessellator, (8.5F+3.5F*direction), 12, 4, (8.5F+3.5F*direction), 4);
            }
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderSideZ(TileEntityChannel channel, Tessellator tessellator, int direction, boolean neighbour) {
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            if(neighbour) {
                //extend bottom plane
                    //draw bottom plane front
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, (6+6*direction), 4, (6+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, (10+6*direction), 4, (10+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, (10+6*direction), 12, (10+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, (6+6*direction), 12, (6+6*direction));
                    //draw bottom plane back
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, (6+6*direction), 4, (6+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, (6+6*direction), 12, (6+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, (10+6*direction), 12, (10+6*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, (10+6*direction), 4, (10+6*direction));
                //draw side edges
                    //draw first edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 5.5F*(1+direction), 5.5F*(1+direction), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 5.5F*(1+direction), 5.5F*(1+direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-12);
                    //draw first edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 12, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 4, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 4, 5.5F*(1+direction), 5.5F*(1+direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 12, 5.5F*(1+direction), 5.5F*(1+direction), 16-12);
                    //draw first edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 12, 5.5F*(1+direction), 5, 5.5F*(1+direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 5.5F*(1+direction), 4, 5.5F*(1+direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (10.5F+5.5F*direction), 4, (10.5F+5.5F*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 5, 12, (10.5F+5.5F*direction), 5, (10.5F+5.5F*direction));
                    //draw second edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 12, 5.5F*(1+direction), 5.5F*(1+direction), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 4, 5.5F*(1+direction), 5.5F*(1+direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 4, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 12, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-12);
                    //draw second edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, (10.5F+5.5F*direction), (10.5F+5.5F*direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, 5.5F*(1+direction), 5.5F*(1+direction), 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 5.5F*(1+direction), 5.5F*(1+direction), 16-12);
                    //draw second edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 5.5F*(1+direction), 12, 5.5F*(1+direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 12, 5.5F*(1+direction), 11, 5.5F*(1+direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 11, 12, (10.5F+5.5F*direction), 11, (10.5F+5.5F*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (10.5F+5.5F*direction), 12, (10.5F+5.5F*direction));
            }
            else {
                //draw an edge
                    //draw edge front
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (8.5F+3.5F*direction), 4, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, (8.5F+3.5F*direction),  4, 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, (8.5F+3.5F*direction), 12, 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (8.5F+3.5F*direction), 12, 16-12);
                    //draw edge back
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (7.5F+3.5F*direction), 4, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (7.5F+3.5F*direction), 12, 16-12);
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, (7.5F+3.5F*direction), 12, 16-4);
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, (7.5F+3.5F*direction),  4, 16-4);
                    //draw edge top
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (7.5F+3.5F*direction), 4, (7.5F+3.5F*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, (8.5F+3.5F*direction), 4, (8.5F+3.5F*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (8.5F+3.5F*direction), 12, (8.5F+3.5F*direction));
                    RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, (7.5F+3.5F*direction), 12, (7.5F+3.5F*direction));
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
