package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
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
            tessellator.setColorRGBA_F(1, 1, 1, 1);
            tessellator.addTranslation(x, y, z);
            this.renderWoodChannel(valve, tessellator);
            this.drawWater(valve, tessellator);
            tessellator.addTranslation(-x, -y, -z);
            //render the top iron part
            renderer.setOverrideBlockTexture(Blocks.iron_block.getIcon(0, 0));
            renderer.setRenderBounds(5*f, 11.5f*f, 5*f, 11*f, 15.001*f, 11*f);
            if (valve.isPowered()) {
                tessellator.addTranslation(0, -3*f, 0);
                renderer.renderStandardBlock(block, x, y, z);
                tessellator.addTranslation(0, 3*f, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }
            //render the bottom iron part
            renderer.setRenderBounds(5*f, 0.999f*f, 5*f, 11*f, 5.5f*f, 11*f);
            if (valve.isPowered()) {
                tessellator.addTranslation(0, 3*f, 0);
                renderer.renderStandardBlock(block, x, y, z);
                tessellator.addTranslation(0, -3*f, 0);
            } else {
                renderer.renderStandardBlock(block, x, y, z);
            }
            //render the wooden guide rails
            renderer.setOverrideBlockTexture(valve.getIcon());
            renderer.setRenderBounds(3.999F*f, 0, 3.999F*f, 5.999F*f, 1, 5.999F* f);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(6*f, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(0, 0, 6*f);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(-6*f, 0, 0);
            renderer.renderStandardBlock(block, x, y, z);
            tessellator.addTranslation(0, 0, -6*f);
            renderer.clearOverrideBlockTexture();
        }
        return true;
    }

    @Override
    protected void connectWater(TileEntityChannel channel, Tessellator tessellator, char axis, int direction, float y, IIcon icon) {
        TileEntityValve valve = (TileEntityValve) channel;
        if(axis=='x' || axis=='z') {
            //checks if there is a neighbouring block that this block can connect to
            if(channel.hasNeighbour(axis, direction)) {
                if (valve.isPowered()) {
                    boolean x = axis == 'x';
                    TileEntityCustomWood te = (TileEntityCustomWood) channel.getWorldObj().getTileEntity(channel.xCoord + (x ? direction : 0), channel.yCoord, channel.zCoord + (x ? 0 : direction));
                    float y2;
                    if (te instanceof TileEntityChannel) {
                        y2 = ((TileEntityChannel) te).getFluidHeight();
                    } else {
                        float lvl = ((TileEntityTank) te).getFluidY() - 16 * ((TileEntityTank) te).getYPosition();
                        y2 = lvl > 12 ? 12 : lvl < 5 ? (5 - 0.0001F) : lvl;
                    }
                    RenderHelper.addScaledVertexWithUV(tessellator, x ? (5.5F + direction * 5.5F) : 11, y2-0.001f, x ? 5 : (5.5F + direction * 5.5F), x ? (5.5F + direction * 5.5F) : 11, x ? 5 : (5.5F + direction * 5.5F), icon);
                    RenderHelper.addScaledVertexWithUV(tessellator, x ? (5.5F + direction * 5.5F) : 5, y2-0.001f, x ? 11 : (5.5F + direction * 5.5F), x ? (5.5F + direction * 5.5F) : 5, x ? 11 : (5.5F + direction * 5.5F), icon);
                    RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 5, y2-0.001f, x ? 11 : (10.5F + direction * 5.5F), x ? (10.5F + direction * 5.5F) : 5, x ? 11 : (10.5F + direction * 5.5F), icon);
                    RenderHelper.addScaledVertexWithUV(tessellator, x ? (10.5F + direction * 5.5F) : 11, y2-0.001f, x ? 5 : (10.5F + direction * 5.5F), x ? (10.5F + direction * 5.5F) : 11, x ? 5 : (10.5F + direction * 5.5F), icon);
                } else {
                    super.connectWater(channel, tessellator, axis, direction, y, icon);
                }
            }
        }
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
