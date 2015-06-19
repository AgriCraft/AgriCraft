package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    public static AtomicInteger renderCallCounter = new AtomicInteger(0);

    public RenderChannel() {}

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        TileEntityChannel channel = (TileEntityChannel) te;
        if (channel.getDiscreteScaledFluidLevel() > 0) {
            GL11.glPushMatrix();                                                            //initiate first gl renderer
                GL11.glTranslatef((float) x, (float) y, (float) z);    //sets the rendering origin to the right spot
                GL11.glPushMatrix();                                                        //initiate second gl renderer
                    //draw the waterTexture
                    Tessellator tessellator = Tessellator.instance;
                    tessellator.startDrawingQuads();
                        GL11.glColor3f(1, 1, 1);
                        this.drawWater(channel, tessellator);
                    tessellator.draw();
                GL11.glPopMatrix();                                                         //close second gl renderer
            GL11.glPopMatrix();                                                             //close first gl renderer
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        renderCallCounter.incrementAndGet();

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        //translate Tessellator to the right position
        tessellator.addTranslation(x, y, z);
        //set colors
        tessellator.setBrightness(Blocks.planks.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        //call correct drawing methods
        if (tileEntity instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) tileEntity;
            if(channel.getBlockMetadata()==0) {
                this.renderWoodChannel(channel, tessellator);
                if(channel.getDiscreteScaledFluidLevel()>0) {
                    this.drawWater(channel, tessellator);
                }
            }
            else if(channel.getBlockMetadata()==1) {
                this.renderIronChannel(channel, tessellator);
            }
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        //translate tessellator back
        tessellator.addTranslation(-x, -y, -z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.channelId);
    }

    protected void renderWoodChannel(TileEntityChannel channel, Tessellator tessellator) {
        this.renderBottom(channel, tessellator);
        this.renderSide(channel, tessellator, 'x', -1);
        this.renderSide(channel, tessellator, 'x', 1);
        this.renderSide(channel, tessellator, 'z', -1);
        this.renderSide(channel, tessellator, 'z', 1);
    }

    protected void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        //the texture
        IIcon icon = channel.getIcon();
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
    }

    //renders one of the four sides of a channel
    protected void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            //checks if there is a neighbouring block that this block can connect to
            boolean neighbour = channel.hasNeighbour(axis, direction);
            boolean x = axis == 'x';
            //the texture
            IIcon icon = channel.getIcon();
            if(neighbour) {
                //extend bottom plane
                //draw bottom plane front
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction), icon);
                //draw bottom plane back
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction), icon);
                //draw side edges
                //draw first edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(1+direction):4, 12, x?12:5.5F*(1+direction), 5.5F*(direction+1), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 4, x?12:5.5F*(1+direction), 5.5F*(direction+1), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 4, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4, icon);
                //draw first edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, x?12:4, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 4, x?11:5.5F*(1+direction), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, x?4:12, x?11:5.5F*(1+direction), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4, icon);
                //draw first edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(5.5F*(1+direction)), x?5.5F*(direction+1):5, x?11:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 12, x?12:(5.5F*(1+direction)), x?5.5F*(direction+1):4, x?12:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):4, x?12:(10.5F+5.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 12, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):5, x?11:(10.5F+5.5F*direction), icon);
                //draw second edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 4, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 4, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4, icon);
                //draw second edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, x?12:4, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(5.5F*(1+direction)), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, x?4:12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4, icon);
                //draw second edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):12, x?4:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), x?5.5F*(direction+1):11, x?5:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):11, x?5:(10.5F+5.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 12, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):12, x?4:(10.5F+5.5F*direction), icon);
            }
            else {
                //draw an edge
                //draw edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), 4, 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 4, x?12:(8.5F+3.5F*direction), 4, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 4, x?4:(8.5F+3.5F*direction), 12, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(8.5F+3.5F*direction), 12, 4, icon);
                //draw edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), 4, 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, x?4:12, x?4:(7.5F+3.5F*direction), x?4:12, x?12:4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, 4, x?12:(7.5F+3.5F*direction), 12, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, x?12:4, x?12:(7.5F+3.5F*direction), x?12:4, x?4:12, icon);
                //draw edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?4:(7.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?12:(8.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?12:(8.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?12:(8.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(7.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?4:(7.5F+3.5F*direction), icon);
            }
        }
    }

    private void renderIronChannel(TileEntityChannel channel, Tessellator tessellator) {
       IIcon icon = channel.getIcon();
    }

    protected void drawWater(TileEntityChannel channel, Tessellator tessellator) {
        float y = channel.getDiscreteScaledFluidHeight();
        //the texture
        IIcon icon = Blocks.water.getIcon(1, 0);
        //stolen from Vanilla code
        int l = Blocks.water.colorMultiplier(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord);
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;
        float f4 = 1.0F;
        tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord));
        tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

        //draw central water level
        RenderHelper.addScaledVertexWithUV(tessellator, 5, y-0.001f, 5, 5, 5, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 5, y-0.001f, 11, 5, 11, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11, y-0.001f, 11, 11, 11, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 11, y-0.001f, 5, 11, 5, icon);
        //connect to edges
        this.connectWater(channel, tessellator, 'x', 1, y, icon);
        this.connectWater(channel, tessellator, 'z', 1, y, icon);
        this.connectWater(channel, tessellator, 'x', -1, y, icon);
        this.connectWater(channel, tessellator, 'z', -1, y, icon);
    }

    protected void connectWater(TileEntityChannel channel, Tessellator tessellator, char axis, int direction, float y, IIcon icon) {
        if(axis=='x' || axis=='z') {
            //checks if there is a neighbouring block that this block can connect to
            if(channel.hasNeighbour(axis, direction)) {
                boolean x = axis=='x';
                TileEntityCustomWood te = (TileEntityCustomWood) channel.getWorldObj().getTileEntity(channel.xCoord+(x?direction:0), channel.yCoord, channel.zCoord+(x?0:direction));
                float y2;
                if(te instanceof TileEntityChannel) {
                    if(te instanceof TileEntityValve && ((TileEntityValve) te).isPowered()) {
                        y2 = y;
                    }
                    else {
                        y2 = (y + ((TileEntityChannel) te).getDiscreteScaledFluidHeight()) / 2;
                    }
                }
                else {
                    float lvl = ((TileEntityTank) te).getScaledDiscreteFluidY()-16*((TileEntityTank) te).getYPosition();
                    y2 = lvl>12?12:lvl<5?(5-0.0001F):lvl;
                }
                this.drawWaterEdge(tessellator, x, direction, y, y2, icon);
            }
        }
    }

    protected void drawWaterEdge(Tessellator tessellator, boolean xAxis, int direction, float lvl1, float lvl2, IIcon icon) {
        RenderHelper.addScaledVertexWithUV(tessellator, xAxis ? (5.5F + direction * 5.5F) : 11, (xAxis?lvl1 :lvl2)-0.001f, xAxis ? 5 : (5.5F + direction * 5.5F), xAxis ? (5.5F + direction * 5.5F) : 11, xAxis ? 5 : (5.5F + direction * 5.5F), icon);
        RenderHelper.addScaledVertexWithUV(tessellator, xAxis?(5.5F+direction*5.5F):5, (xAxis?lvl1:lvl2)-0.001f, xAxis?11:(5.5F+direction*5.5F), xAxis?(5.5F+direction*5.5F):5, xAxis?11:(5.5F+direction*5.5F), icon);
        RenderHelper.addScaledVertexWithUV(tessellator, xAxis?(10.5F+direction*5.5F):5, (xAxis?lvl2:lvl1)-0.001f, xAxis?11:(10.5F+direction*5.5F), xAxis?(10.5F+direction*5.5F):5, xAxis?11:(10.5F+direction*5.5F), icon);
        RenderHelper.addScaledVertexWithUV(tessellator, xAxis?(10.5F+direction*5.5F):11, (xAxis?lvl2:lvl1)-0.001f, xAxis?5:(10.5F+direction*5.5F), xAxis?(10.5F+direction*5.5F):11, xAxis?5:(10.5F+direction*5.5F), icon);
    }
}