package com.InfinityRaider.AgriCraft.renderers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.InfinityRaider.AgriCraft.proxy.ClientProxy;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderCrop implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
            RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
            Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tileEntity;
            this.renderBase(renderer, block, x, y, z);
            BlockBush plant = SeedHelper.getPlant((ItemSeeds) crop.seed);
            if(crop.crossCrop)
            {
                renderer.setRenderBounds(0.1875F, 0.6875F , 0.0F, 0.125F, 0.6F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(0.8125F, 0.6875F , 1.0F, 0.875F, 0.6F, 0F);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(1.0F, 0.6875F , 0.8125F, 0.0F, 0.6F, 0.875F);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(0.0F, 0.6875F , 0.1875, 1.0F, 0.6F, 0.125F);
                renderer.renderStandardBlock(block, x, y, z);
            }
            if(plant != null)
                renderer.renderBlockByRenderType(plant, x, y, z);
        }
        return true;
    }
    
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
        return ClientProxy.cropRenderID;
    }

}
