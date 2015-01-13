package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;


public class RenderValve implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntityValve valve = (TileEntityValve) world.getTileEntity(x, y, z);
        if (valve != null) {
            renderer.setRenderBounds(3 * Constants.unit, 4 * Constants.unit, 3 * Constants.unit,
                    13 * Constants.unit, 12 * Constants.unit, 13 * Constants.unit);
            renderer.renderStandardBlock(block, x, y, z);

            renderer.setOverrideBlockTexture(Blocks.iron_block.getIcon(0, 0));
            renderer.setRenderBounds(5 * Constants.unit, 11.8f * Constants.unit, 5 * Constants.unit,
                    11 * Constants.unit, 15.8f * Constants.unit, 11 * Constants.unit);
            if (valve.isPowered()) {
                Tessellator.instance.addTranslation(0, -3 * Constants.unit, 0);
                renderer.renderStandardBlock(block, x, y, z);
                Tessellator.instance.addTranslation(0, 3 * Constants.unit, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }

            renderer.setRenderBounds(5 * Constants.unit, 0.2f * Constants.unit, 5 * Constants.unit,
                    11 * Constants.unit, 4.2f * Constants.unit, 11 * Constants.unit);
            if (valve.isPowered()) {
                Tessellator.instance.addTranslation(0, 3 * Constants.unit, 0);
                renderer.renderStandardBlock(block, x, y, z);
                Tessellator.instance.addTranslation(0, -3 * Constants.unit, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }

            renderer.setOverrideBlockTexture(block.getIcon(0, 0));
            renderer.setRenderBounds(4 * Constants.unit, 0, 4 * Constants.unit,
                    6 * Constants.unit, 1, 6 * Constants.unit);
            renderer.renderStandardBlock(block, x, y, z);
            Tessellator.instance.addTranslation(6 * Constants.unit, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            Tessellator.instance.addTranslation(0, 0, 6 * Constants.unit);
            renderer.renderStandardBlock(block, x, y, z);
            Tessellator.instance.addTranslation(-6 * Constants.unit, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            Tessellator.instance.addTranslation(0, 0, -6 * Constants.unit);
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
