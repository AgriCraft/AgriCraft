package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
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
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 4, 12, 4);
            //draw first plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 4, 4, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4,4, 12, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, 12, 12, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 12, 4, 12);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    //renders one of the four sides of a channel
    private void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            //checks if there is a neighbouring block that this block can connect to
            boolean neighbour = channel.hasNeighbour(axis, direction);
            boolean x = axis == 'x';
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
            //disable lighting
            GL11.glDisable(GL11.GL_LIGHTING);
            //tell the tessellator to start drawing
            tessellator.startDrawingQuads();
                if(neighbour) {
                    //extend bottom plane
                        //draw bottom plane front
                        RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction));
                        //draw bottom plane back
                        RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction));
                    //draw side edges
                        //draw first edge front
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(1+direction):4, 12, x?12:5.5F*(1+direction), 5.5F*(direction+1), 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 4, x?12:5.5F*(1+direction), 5.5F*(direction+1), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 4, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4);
                        //draw first edge back
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, x?12:4, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 4, x?11:5.5F*(1+direction), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, x?4:12, x?11:5.5F*(1+direction), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4);
                        //draw first edge top
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(5.5F*(1+direction)), x?5.5F*(direction+1):5, x?11:(5.5F*(1+direction)));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 12, x?12:(5.5F*(1+direction)), x?5.5F*(direction+1):4, x?12:(5.5F*(1+direction)));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):4, x?12:(10.5F+5.5F*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 12, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):5, x?11:(10.5F+5.5F*direction));
                        //draw second edge front
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 4, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 4, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4);
                        //draw second edge back
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, x?12:4, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(5.5F*(1+direction)), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, x?4:12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4);
                        //draw second edge top
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):12, x?4:(5.5F*(1+direction)));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), x?5.5F*(direction+1):11, x?5:(5.5F*(1+direction)));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):11, x?5:(10.5F+5.5F*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 12, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):12, x?4:(10.5F+5.5F*direction));
                    }
                    else {
                    //draw an edge
                        //draw edge front
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), 4, 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 4, x?12:(8.5F+3.5F*direction), 4, 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 4, x?4:(8.5F+3.5F*direction), 12, 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(8.5F+3.5F*direction), 12, 4);
                        //draw edge back
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), 4, 4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, x?4:12, x?4:(7.5F+3.5F*direction), x?4:12, x?12:4);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, 4, x?12:(7.5F+3.5F*direction), 12, 12);
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, x?12:4, x?12:(7.5F+3.5F*direction), x?12:4, x?4:12);
                        //draw edge top
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?4:(7.5F+3.5F*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?12:(8.5F+3.5F*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?12:(8.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?12:(8.5F+3.5F*direction));
                        RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(7.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?4:(7.5F+3.5F*direction));
                }
            tessellator.draw();
            //enable lighting
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void renderIronChannel(TileEntityChannel channel, Tessellator tessellator) {
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
    }

    private void drawWater(TileEntityChannel channel, Tessellator tessellator) {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.waterTexture);
        float y = channel.getFluidHeight();
        //stolen from Vanilla code
        int l = Blocks.water.colorMultiplier(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float)(l & 255) / 255.0F;
        float f4 = 1.0F;
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            //draw central water level
            RenderHelper.addScaledVertexWithUV(tessellator, 5, y, 5, 5, 5);
            RenderHelper.addScaledVertexWithUV(tessellator, 5, y, 11, 5, 11);
            RenderHelper.addScaledVertexWithUV(tessellator, 11, y, 11, 11, 11);
            RenderHelper.addScaledVertexWithUV(tessellator, 11, y, 5, 11, 5);
            //connect to edges
            this.connectWater(channel, tessellator, 'x', 1, y);
            this.connectWater(channel, tessellator, 'z', 1, y);
            this.connectWater(channel, tessellator, 'x', -1, y);
            this.connectWater(channel, tessellator, 'z', -1, y);
        tessellator.draw();
    }

    private void connectWater(TileEntityChannel channel, Tessellator tessellator, char axis, int direction, float y) {
        if(axis=='x' || axis=='z') {
            //checks if there is a neighbouring block that this block can connect to
            if(channel.hasNeighbour(axis, direction)) {
                boolean x = axis=='x';
                TileEntityCustomWood te = (TileEntityCustomWood) channel.getWorldObj().getTileEntity(channel.xCoord+(x?direction:0), channel.yCoord, channel.zCoord+(x?0:direction));
                if(te instanceof TileEntityChannel) {
                    float y2 = (y + ((TileEntityChannel) te).getFluidHeight())/2;
                    RenderHelper.addScaledVertexWithUV(tessellator, x?(5.5F+direction*5.5F):11, x?y:y2, x?5:(5.5F+direction*5.5F), x?(5.5F+direction*5.5F):11, x?5:(5.5F+direction*5.5F));
                    RenderHelper.addScaledVertexWithUV(tessellator, x?(5.5F+direction*5.5F):5, x?y:y2, x?11:(5.5F+direction*5.5F), x?(5.5F+direction*5.5F):5, x?11:(5.5F+direction*5.5F));
                    RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, x?y2:y, x?11:(10.5F+direction*5.5F), x?(10.5F+direction*5.5F):5, x?11:(10.5F+direction*5.5F));
                    RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, x?y2:y, x?5:(10.5F+direction*5.5F), x?(10.5F+direction*5.5F):11, x?5:(10.5F+direction*5.5F));
                }

            }
        }
    }
}
