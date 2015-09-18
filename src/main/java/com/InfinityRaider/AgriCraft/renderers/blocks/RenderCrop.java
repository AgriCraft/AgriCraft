package com.InfinityRaider.AgriCraft.renderers.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase {
    public RenderCrop() {
        super(Blocks.blockCrop, false);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        int x = (int) xCoord;
        int y = (int) yCoord;
        int z = (int) zCoord;
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tileEntity;
            this.renderBase(renderer, block, x, y, z);
            if (crop.isCrossCrop()) {
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
            else if (crop.hasPlant()) {
                //render the plant
                crop.getPlant().renderPlantInCrop(world, x, y, z, renderer);
            }
            else if(crop.hasWeed()) {
                //render weeds
                PlantRenderer.renderPlantLayer(x, y, z, renderer, 6, crop.getPlantIcon(), 0);
            }
        }
        return true;
    }

    //render four sticks vertical in the ground
    private void renderBase(RenderBlocks renderer, Block block, int x, int y, int z) {
        renderer.setRenderBounds(0.125F, -0.125F*8, 0.125F, 0.1875F, Constants.UNIT * 13, 0.1875F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.875F, -0.125F*8, 0.875F, 0.8125F, Constants.UNIT * 13, 0.8125F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.8125F, -0.125F*8, 0.125F, 0.875F, Constants.UNIT * 13, 0.1875F);
        renderer.renderStandardBlock(block, x, y, z);
        
        renderer.setRenderBounds(0.125F, -0.125F*8, 0.8125F, 0.1875F, Constants.UNIT * 13, 0.875F);
        renderer.renderStandardBlock(block, x, y, z);
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
