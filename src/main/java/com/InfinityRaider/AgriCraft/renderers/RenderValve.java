package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;


public class RenderValve extends RenderChannel implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntityValve valve = (TileEntityValve) world.getTileEntity(x, y, z);
        if (valve != null) {
            Tessellator tessellator = Tessellator.instance;
            float f = Constants.unit;
            //render the channel
            tessellator.addTranslation(x, y, z);
            this.renderWoodChannel(valve, tessellator);
            tessellator.addTranslation(-x, -y, -z);
            /*
            renderer.setRenderBounds(3 * f, 4 * f, 3 * f, 13 * f, 12 * f, 13 * f);
            renderer.renderStandardBlock(block, x, y, z);
            */

            //render the top iron part
            renderer.setOverrideBlockTexture(Blocks.iron_block.getIcon(0, 0));
            renderer.setRenderBounds(5 * f, 11.8f * f, 5 * f, 11 * f, 15.8f * f, 11 * f);
            if (valve.isPowered()) {
                tessellator.addTranslation(0, -3 * f, 0);
                renderer.renderStandardBlock(block, x, y, z);
                tessellator.addTranslation(0, 3 * f, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }
            //render the bottom iron part
            renderer.setRenderBounds(5 * f, 0.2f * f, 5 * f, 11 * f, 4.2f * f, 11 * f);
            if (valve.isPowered()) {
                tessellator.addTranslation(0, 3 * f, 0);
                renderer.renderStandardBlock(block, x, y, z);
                tessellator.addTranslation(0, -3 * f, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }
            //render the wooden guide rails
            renderer.setOverrideBlockTexture(valve.getIcon());
            renderer.setRenderBounds(3.999F * f, 0, 3.999F * f, 5.999F * f, 1, 5.999F* f);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(6 * f, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(0, 0, 6 * f);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(-6 * f, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(0, 0, -6 * f);
            renderer.clearOverrideBlockTexture();

        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.valveId);
    }
}
