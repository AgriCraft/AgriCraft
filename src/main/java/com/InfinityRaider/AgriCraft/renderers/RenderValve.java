package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;


public class RenderValve extends RenderChannel {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

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

            boolean xPos = valve.hasNeighbour('x', 1);
            boolean xNeg = valve.hasNeighbour('x', -1);
            boolean zPos = valve.hasNeighbour('z', 1);
            boolean zNeg = valve.hasNeighbour('z', -1);

            //render the iron valves
            renderer.setOverrideBlockTexture(Blocks.iron_block.getIcon(0, 0));
            renderer.setRenderBounds(5*f, 11.5f*f, 0.001*f, 11*f, 15.001*f, 1.999*f);
            if(zNeg) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, -3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, 3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(5*f, 0.999f*f, 0.001*f, 11*f, 5.5f*f, 1.999*f);
            if(zNeg) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, 3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, -3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(5*f, 11.5f*f, 14.001*f, 11*f, 15.001*f, 15.999*f);
            if(zPos) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, -3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, 3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(5*f, 0.999f*f, 14.001*f, 11*f, 5.5f*f, 15.999*f);
            if(zPos) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, 3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, -3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(0.001*f, 11.5f*f, 5*f, 1.999*f, 15.001*f, 11*f);
            if(xNeg) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, -3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, 3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(0.001*f, 0.999f*f, 5*f, 1.999*f, 5.5f*f, 11*f);
            if(xNeg) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, 3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, -3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(14.001*f, 11.5f*f, 5*f, 15.999*f, 15.001*f, 11*f);
            if(xPos) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, -3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, 3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }
            renderer.setRenderBounds(14.001*f, 0.999f*f, 5*f, 15.001*f, 5.5f*f, 11*f);
            if(xPos) {
                if (valve.isPowered()) {
                    tessellator.addTranslation(0, 3 * f, 0);
                    renderer.renderStandardBlock(block, x, y, z);
                    tessellator.addTranslation(0, -3 * f, 0);
                } else {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }

            //render the wooden guide rails along x-axis
            renderer.setOverrideBlockTexture(valve.getIcon());
            renderer.setRenderBounds(3.999F * f, 0, 0, 5.999F * f, 1, 2 * f);
            if(zNeg) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(6*f, 0, 0);
            if(zNeg) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(0, 0, 14*f);
            if(zPos) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(-6*f, 0, 0);
            if(zPos) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(0, 0, -14 * f);

            //render the wooden guide rails along z-axis
            renderer.setRenderBounds(0, 0, 3.999F*f, 2*f, 1, 5.999F*f);
            if(xNeg) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(0, 0, 6*f);
            if(xNeg) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(14*f, 0, 0);
            if(xPos) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(0, 0, -6*f);
            if(xPos) {renderer.renderStandardBlock(block, x, y, z);}
            tessellator.addTranslation(-14*f, 0, 0);
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
                    float y2 = valve.getFluidHeight();
                    this.drawWaterEdge(tessellator, x, direction, y2, y2, icon);
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
