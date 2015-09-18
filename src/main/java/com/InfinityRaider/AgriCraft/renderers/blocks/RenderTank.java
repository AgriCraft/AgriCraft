package com.InfinityRaider.AgriCraft.renderers.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {
    public RenderTank() {
        super(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, new TileEntityTank(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        this.renderTank(teDummy, tessellator, item.getItemDamage());
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        //call correct drawing methods
        boolean success = false;
        if (tile instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tile;
            if(callFromTESR) {
                if(tank.getScaledDiscreteFluidLevel()>0) {
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                    drawWater(tank, tessellator);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                    success = true;
                }
            } else {
                success = renderTank(tank, tessellator, tank.getBlockMetadata());
            }
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        return success;
    }

    private boolean renderTank(TileEntityTank tank, Tessellator tessellator, int meta) {
        if(meta==0) {
            this.drawWoodTank(tank, tessellator);
            //draw the waterTexture
            if((!shouldBehaveAsTESR()) && (tank.getScaledDiscreteFluidLevel()>0)) {
                this.drawWater(tank, tessellator);
            }
        }
        else if(meta==1) {
            this.drawIronTank(tank, tessellator);
        }
        return true;
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }

    private void drawWoodTank(TileEntityTank tank, Tessellator tessellator) {
        this.renderBottom(tank, tessellator);
        this.renderSide(tank, tessellator, 'x', -1);
        this.renderSide(tank, tessellator, 'x', 1);
        this.renderSide(tank, tessellator, 'z', -1);
        this.renderSide(tank, tessellator, 'z', 1);
    }

    private void renderBottom(TileEntityTank tank, Tessellator tessellator) {
        //the texture
        IIcon icon = tank.getIcon();
        int cm = tank.colorMultiplier();
        //bottom
        boolean bottom = !tank.hasNeighbour('y', -1);
        if (bottom) {
            drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon, cm);
        }
        //corners
        int yMin = bottom?1:0;
        if (!tank.hasNeighbour('x', -1) || !tank.hasNeighbour('z', -1)) {
            drawScaledPrism(tessellator, 0, yMin, 0, 2, 16, 2, icon, cm);
        }
        if (!tank.hasNeighbour('x', 1) || !tank.hasNeighbour('z', -1)) {
            drawScaledPrism(tessellator, 14, yMin, 0, 16, 16, 2, icon, cm);
        }
        if (!tank.hasNeighbour('x', -1) || !tank.hasNeighbour('z', 1)) {
            drawScaledPrism(tessellator, 0, yMin, 14, 2, 16, 16, icon, cm);
        }
        if (!tank.hasNeighbour('x', 1) || !tank.hasNeighbour('z', 1)) {
            drawScaledPrism(tessellator, 14, yMin, 14, 16, 16, 16, icon, cm);
        }
    }

    private void renderSide(TileEntityTank tank, Tessellator tessellator, char axis, int direction) {
        //the texture
        IIcon icon = tank.getIcon();
        int cm = tank.colorMultiplier();
        int yMin = tank.hasNeighbour('y', -1)?0:1;
        if ((axis == 'x' || axis == 'z') && (direction == 1 || direction == -1)) {
            boolean x = axis=='x';
            //connected to a channel
            if(tank.isConnectedToChannel(axis, direction)) {
                drawScaledPrism(tessellator, x?(7+7*direction):2, yMin, x?2:(7+7*direction), x?(9+7*direction):14, 5, x?14:(9+7*direction), icon, cm);
                drawScaledPrism(tessellator, x?(7+7*direction):2, 5, x?2:(7+7*direction), x?(9+7*direction):5, 12, x?5:(9+7*direction), icon, cm);
                drawScaledPrism(tessellator, x?(7+7*direction):11, 5, x?11:(7+7*direction), x?(9+7*direction):14, 12, x?14:(9+7*direction), icon, cm);
                drawScaledPrism(tessellator, x?(7+7*direction):2, 12, x?2:(7+7*direction), x?(9+7*direction):14, 16, x?14:(9+7*direction), icon, cm);
            }
            //not connected to anything
            else if(!tank.hasNeighbour(axis, direction)) {
                drawScaledPrism(tessellator, x?(7+7*direction):2, yMin, x?2:(7+7* direction),  x?(9+7*direction):14, 16, x?14:(9+7*direction), icon, cm);
            }
        }
    }

    private void drawIronTank(TileEntityTank tank, Tessellator tessellator) {
        IIcon icon = tank.getIcon();
    }

    private void drawWater(TileEntityTank tank, Tessellator tessellator) {
        //only render water on the bottom layer
        if(tank.getYPosition()==0) {
            float y = tank.getScaledDiscreteFluidY()-0.01F; //-0.0001F to avoid Z-fighting on maximum filled tanks
            //the texture
            IIcon icon = Blocks.water.getIcon(1,0);
            //stolen from Vanilla code
            int l = Blocks.water.colorMultiplier(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            //draw surface
            addScaledVertexWithUV(tessellator, 0, y, 0, 0, 0, icon);
            addScaledVertexWithUV(tessellator, 0, y, 16, 0, 16, icon);
            addScaledVertexWithUV(tessellator, 16, y, 16, 16, 16, icon);
            addScaledVertexWithUV(tessellator, 16, y, 0, 16, 0, icon);
        }
    }
}