package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderTank implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        //translate Tesselator to the right position
        tessellator.addTranslation(x, y, z);
        //set colors
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        //call correct drawing methods
        if (tileEntity instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tileEntity;
            if(tank.getBlockMetadata()==0) {
                this.drawWoodTank(tank, tessellator);
                //draw the waterTexture
                if(tank.getScaledDiscreteFluidLevel()>0) {
                    this.drawWater(tank, tessellator);
                }
            }
            else if(tank.getBlockMetadata()==1) {
                this.drawIronTank(tank, tessellator);
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
        return AgriCraft.proxy.getRenderId(Constants.tankId);
    }

    private void drawWoodTank(TileEntityTank tank, Tessellator tessellator) {
        this.renderBottom(tank, tessellator);
        this.renderSide(tank, tessellator, 'x', -1);
        this.renderSide(tank, tessellator, 'x', 1);
        this.renderSide(tank, tessellator, 'z', -1);
        this.renderSide(tank, tessellator, 'z', 1);
    }

    private void renderBottom(TileEntityTank tank, Tessellator tessellator) {
        if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord-1, tank.zCoord))) {
            //the texture
            IIcon icon = tank.getIcon();
            //draw bottom plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 0, icon);
            //draw bottom plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 1, 0, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 1, 16, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 1, 16, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 1, 0, 0, 16, icon);
        }
    }

    private void renderSide(TileEntityTank tank, Tessellator tessellator, char axis, int direction) {
        //the texture
        IIcon icon = tank.getIcon();
        if ((axis == 'x' || axis == 'z') && (direction == 1 || direction == -1)) {
            boolean x = axis=='x';
            if(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))instanceof TileEntityChannel && ((TileEntityChannel) tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))).isSameMaterial(tank)) {
                //draw plane front top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(9+7*direction), 0, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 11, x?0:(9+7*direction), 0, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 11, x?16:(9+7*direction), 16, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 16, x?16:(9+7*direction), 16, 0, icon);
                //draw plane front left
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 11, x?0:(9+7*direction), 0, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 5, x?0:(9+7*direction), 0, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?5:(9+7*direction), 5, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?5:(9+7*direction), 5, 5, icon);
                //draw plane front bottom
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 5, x?0:(9+7*direction), 0, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 0, x?0:(9+7*direction), 0, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 0, x?16:(9+7*direction), 16, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 5, x?16:(9+7*direction), 16, 11, icon);
                //draw plane front right
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 11, x?11:(9+7*direction), 11, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 5, x?11:(9+7*direction), 11, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 5, x?16:(9+7*direction), 16, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 11, x?16:(9+7*direction), 16, 5, icon);
                //draw plane back top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 16, x?0:(7+7*direction), 0, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(7+7*direction), 16, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 11, x?16:(7+7*direction), 16, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 11, x?0:(7+7*direction), 0, 5, icon);
                //draw plane back left
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 11, x?0:(7+7*direction), 0, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 11, x?5:(7+7*direction), 5, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 5, x?5:(7+7*direction), 5, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 5, x?0:(7+7*direction), 0, 11, icon);
                //draw plane back bottom
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 5, x?0:(7+7*direction), 0, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 5, x?16:(7+7*direction), 16, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 0, x?16:(7+7*direction), 16, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 0, x?0:(7+7*direction), 0, 16, icon);
                //draw plane back right
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?11:(7+7*direction), 11, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 11, x?16:(7+7*direction), 16, 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 5, x?16:(7+7*direction), 16, 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?11:(7+7*direction), 11, 11, icon);
                //draw hole bottom plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?5:(7+7*direction), x?(7+7*direction):5, x?5:(7+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?11:(9+7*direction), x?(7+7*direction):5, x?11:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?11:(9+7*direction), x?(9+7*direction):11, x?11:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?5:(7+7*direction), x?(9+7*direction):11, x?5:(7+7*direction), icon);
                //draw hole right plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 11, x?5:(7+7*direction), (7+7*direction), 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):11, 5, x?5:(7+7*direction), (7+7*direction), 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 5, x?5:(9+7*direction), (9+7*direction), 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?5:(9+7*direction), (9+7*direction), 5, icon);
                //draw hole top plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?5:(7+7*direction), x?(7+7*direction):5, x?5:(7+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?5:(7+7*direction), x?(9+7*direction):11, x?5:(7+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):11, 11, x?11:(9+7*direction), x?(9+7*direction):11, x?11:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?11:(9+7*direction), x?(7+7*direction):5, x?11:(9+7*direction), icon);
                //draw hole left plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 11, x?11:(7+7*direction), (7+7*direction), 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 11, x?11:(9+7*direction), (9+7*direction), 5, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):5, 5, x?11:(9+7*direction), (9+7*direction), 11, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):5, 5, x?11:(7+7*direction), (7+7*direction), 11, icon);
                //draw plane top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(7+7*direction), x?(7+7*direction):0, x?0:(7+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(9+7*direction), x?(7+7*direction):0, x?16:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(9+7*direction), x?(9+7*direction):16, x?16:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(7+7*direction), x?(9+7*direction):16, x?0:(7+7*direction), icon);
            }
            else if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction)))) {
                //draw front plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 16, x?16:(9+7*direction), 0, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):0, 0, x?16:(9+7*direction), 0, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 0, x?0:(9+7*direction), 16, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(9+7*direction), 16, 0, icon);
                //draw back plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(7+7*direction), 0, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 16, x?0:(7+7*direction), 16, 0, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):16, 0, x?0:(7+7*direction), 16, 16, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 0, x?16:(7+7*direction), 0, 16, icon);
                //draw top plane
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?0:(7+7*direction), x?(7+7*direction):0, x?0:(7+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7+7*direction):0, 16, x?16:(9+7*direction), x?(7+7*direction):0, x?16:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?16:(9+7*direction), x?(9+7*direction):16, x?16:(9+7*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9+7*direction):16, 16, x?0:(7+7*direction), x?(9+7*direction):16, x?0:(7+7*direction), icon);
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
            RenderHelper.addScaledVertexWithUV(tessellator, 0, y, 0, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, y, 16, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, y, 16, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, y, 0, 16, 0, icon);
        }
    }
}