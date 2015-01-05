package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderCrop implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tileEntity;
            this.renderBase(renderer, block, x, y, z);
            IIcon icon = crop.getPlantIcon();
            int renderType = crop.getRenderType();
            if (crop.crossCrop) {
                //render four horizontal sticks (crosscrop)
                renderer.setRenderBounds(0.1875F, 0.6875F, 0.0F, 0.125F, 0.6F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.8125F, 0.6875F, 1.0F, 0.875F, 0.6F, 0F);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(1.0F, 0.6875F, 0.8125F, 0.0F, 0.6F, 0.875F);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.0F, 0.6875F, 0.1875, 1.0F, 0.6F, 0.125F);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else if (icon!=null && renderType>=0) {
                //render the plant
                this.renderPlant(icon, x, y, z, renderType);
            }
        }

        return true;
    }

    //render four sticks vertical in the ground
    private void renderBase(RenderBlocks renderer, Block block, int x, int y, int z){
        renderer.setRenderBounds(0.125F, -0.125F, 0.125F, 0.1875F, Constants.unit * 13, 0.1875F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.875F, -0.125F, 0.875F, 0.8125F, Constants.unit * 13, 0.8125F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.8125F, -0.125F, 0.125F, 0.875F, Constants.unit * 13, 0.1875F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.125F, -0.125F, 0.8125F, 0.1875F, Constants.unit * 13, 0.875F);
        renderer.renderStandardBlock(block, x, y, z);
        
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.cropId);
    }

    //draws the plant on the crops
    private void renderPlant(IIcon icon, int x, int y, int z, int renderType) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addTranslation(x, y, z);
        if(renderType != 6) {
            //the shift along x and z axis
            int shift = 4;
            //scale factor
            float f = 0.8F;
            //plane 1 front right
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2*f, 0, 4.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2*f, 16*f, 4.001F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 16*f, 4.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 0, 4.001F, 0, 16, icon);
            //plane 1 front left
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 0, 4, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 16*f, 4, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 16*f, 4, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 0, 4, 0, 16, icon);
            //plane 1 back right
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 0, 4.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 0, 4.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 16*f, 4.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 16*f, 4.001F, 16, 0, icon);
            //plane 1 back left
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 0, 4, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 0, 4, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 16*f, 4, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 16*f, 4, 16, 0, icon);
            //plane 2 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, (shift*2)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, (16+shift*2)*f, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16*f, (16+shift*2)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16*f, (shift*2)*f, 0, 0, icon);
            //plane 2 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 0, (0-shift)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 0, (16-shift)*f, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 16*f, (16-shift)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 16*f, (0-shift)*f, 0, 0, icon);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, (shift*2)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16*f, (shift*2)*f, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16*f, (16+shift*2)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, (16+shift*2)*f, 16, 16, icon);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 0, (0-shift)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 16*f, (0-shift)*f, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 16*f, (16-shift)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, 0, (16-shift)*f, 16, 16, icon);
            //plane 3 front right
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 0, 12, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 0, 12, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 16*f, 12, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 16*f, 12, 0, 0, icon);
            //plane 3 front left
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 0, 12.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 0, 12.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 16*f, 12.001F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 16*f, 12.001F, 0, 0, icon);
            //plane 3 back right
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 0, 12, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (shift*2)*f, 16*f, 12, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 16*f, 12, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16+shift*2)*f, 0, 16, 12, 16, icon);
            //plane 3 back left
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 0, 12.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (0-shift)*f, 16*f, 12.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 16*f, 12.001F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, (16-shift)*f, 0, 12.001F, 16, 16, icon);
            //plane 4 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, (16+shift*2)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, (shift*2)*f, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16*f, (shift*2)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16*f, (16+shift*2)*f, 0, 0, icon);
            //plane 4 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 0, (16-shift)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 0, (0-shift)*f, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 16*f, (0-shift)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 16*f, (16-shift)*f, 0, 0, icon);
            //plane 4 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, (16+shift*2)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16*f, (16+shift*2)*f, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16*f, (shift*2)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, (shift*2)*f, 16, 16, icon);
            //plane 4 back left
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 0, (16-shift)*f, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 16*f, (16-shift)*f, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 16*f, (0-shift)*f, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, 0,(0-shift)*f, 16, 16, icon);
        }
        else {
            //plane 1 front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 4, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 4, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 4, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 4, 0, 16, icon);
            //plane 1 back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 4, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 4, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 4, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 4, 16, 0, icon);
            //plane 2 front
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0, 0, 0, icon);
            //plane 2 back
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16, 16, 16, icon);
            //plane 3 front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 12, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 12, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 12, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 12, 0, 0, icon);
            //plane 3 back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 12, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 12, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 12, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 12, 16, 16, icon);
            //plane 4 front
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 16, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 0, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 0, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 16, 0, 0, icon);
            //plane 4 back
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 16, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 16, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 0, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 0, 16, 16, icon);
        }
        tessellator.addTranslation(-x, -y, -z);
    }
}
