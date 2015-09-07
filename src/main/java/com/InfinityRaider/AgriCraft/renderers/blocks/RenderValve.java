package com.InfinityRaider.AgriCraft.renderers.blocks;

import codechicken.multipart.BlockMultipart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.LeverPart;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderValve extends RenderChannel {
    public RenderValve() {
        super(com.InfinityRaider.AgriCraft.init.Blocks.blockChannelValve, new TileEntityValve());
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        super.renderInInventory(type, item, data);
        IIcon icon = teDummy.getIcon();
        int cm = teDummy.colorMultiplier();
        Tessellator tessellator = Tessellator.instance;
        //render the iron valves
        float f = Constants.UNIT;
        IIcon ironIcon = Blocks.iron_block.getIcon(0, 0);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        drawScaledPrism(tessellator, 5, 11.5f, 0.001f, 11, 15.001f, 1.999f, ironIcon, cm);
        drawScaledPrism(tessellator, 5, 0.999f, 0.001f, 11, 5.5f, 1.999f, ironIcon, cm);
        drawScaledPrism(tessellator, 5, 11.5f, 14.001f, 11, 15.001f, 15.999f, ironIcon, cm);
        drawScaledPrism(tessellator, 5, 0.999f, 14.001f, 11, 5.5f, 15.999f, ironIcon, cm);
        drawScaledPrism(tessellator, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, ironIcon, cm);

        //render the wooden guide rails along x-axis
        drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon, cm);
        tessellator.addTranslation(6*f, 0, 0);
        drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon, cm);
        tessellator.addTranslation(0, 0, 14*f);
        drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon, cm);
        tessellator.addTranslation(-6*f, 0, 0);
        drawScaledPrism(tessellator, 3.999F, 0, 0, 5.999F, 16, 2, icon, cm);
        tessellator.addTranslation(0, 0, -14 * f);

        //render the wooden guide rails along z-axis
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(0, 0, 6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(14*f, 0, 0);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(0, 0, -6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(-14*f, 0, 0);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator2, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        Tessellator tessellator1 = Tessellator.instance;
        TileEntityValve valve = (TileEntityValve) tile;
        if (valve != null) {
            float unit = Constants.UNIT;
            this.renderWoodChannel(valve, tessellator2);
            this.drawWater(valve, tessellator2);

            boolean xPos = valve.hasNeighbour('x', 1);
            boolean xNeg = valve.hasNeighbour('x', -1);
            boolean zPos = valve.hasNeighbour('z', 1);
            boolean zNeg = valve.hasNeighbour('z', -1);

            //render the iron valves
            renderer.setOverrideBlockTexture(Blocks.iron_block.getIcon(0, 0));
            if(zNeg) {
                renderer.setRenderBounds(5 * unit, 11.5f * unit, 0.001 * unit, 11 * unit, 15.001 * unit, 1.999 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, -3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, 3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
                renderer.setRenderBounds(5 * unit, 0.999f * unit, 0.001 * unit, 11 * unit, 5.5f * unit, 1.999 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, 3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, -3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
            }
            if(zPos) {
                renderer.setRenderBounds(5 * unit, 11.5f * unit, 14.001 * unit, 11 * unit, 15.001 * unit, 15.999 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, -3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, 3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
                renderer.setRenderBounds(5 * unit, 0.999f * unit, 14.001 * unit, 11 * unit, 5.5f * unit, 15.999 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, 3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, -3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
            }
            if(xNeg) {
                renderer.setRenderBounds(0.001 * unit, 11.5f * unit, 5 * unit, 1.999 * unit, 15.001 * unit, 11 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, -3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, 3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }

                renderer.setRenderBounds(0.001 * unit, 0.999f * unit, 5 * unit, 1.999 * unit, 5.5f * unit, 11 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, 3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, -3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
            }
            if(xPos) {
                renderer.setRenderBounds(14.001 * unit, 11.5f * unit, 5 * unit, 15.999 * unit, 15.001 * unit, 11 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, -3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, 3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }

                renderer.setRenderBounds(14.001 * unit, 0.999f * unit, 5 * unit, 15.999 * unit, 5.5f * unit, 11 * unit);
                if (valve.isPowered()) {
                    tessellator1.addTranslation(0, 3 * unit, 0);
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                    tessellator1.addTranslation(0, -3 * unit, 0);
                } else {
                    renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);
                }
            }

            //render the wooden guide rails along x-axis
            renderer.setOverrideBlockTexture(valve.getIcon());
            renderer.setRenderBounds(3.999F * unit, 0, 0, 5.999F * unit, 1, 2 * unit);
            if(zNeg) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(6*unit, 0, 0);
            if(zNeg) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(0, 0, 14*unit);
            if(zPos) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(-6*unit, 0, 0);
            if(zPos) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(0, 0, -14 * unit);

            //render the wooden guide rails along z-axis
            renderer.setRenderBounds(0, 0, 3.999F*unit, 2*unit, 1, 5.999F*unit);
            if(xNeg) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(0, 0, 6*unit);
            if(xNeg) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(14*unit, 0, 0);
            if(xPos) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(0, 0, -6*unit);
            if(xPos) {renderer.renderStandardBlock(block, (int) x, (int) y, (int) z);}
            tessellator1.addTranslation(-14*unit, 0, 0);
            renderer.clearOverrideBlockTexture();
        }
        return true;
    }

    @Override
    protected void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        boolean x = axis=='x';
        Block neighbour = channel.getWorldObj()==null?null:channel.getWorldObj().getBlock(channel.xCoord+(x?direction:0), channel.yCoord, channel.zCoord+(x?0:direction));
        if(neighbour!=null) {
            int cm = channel.colorMultiplier();
            if (neighbour instanceof BlockLever && RenderHelper.isLeverFacingBlock(channel.getWorldObj().getBlockMetadata(channel.xCoord + (x ? direction : 0), channel.yCoord, channel.zCoord + (x ? 0 : direction)), axis, direction)) {
                IIcon icon = channel.getIcon();
                if (x) {
                    drawScaledPrism(tessellator, direction > 0 ? 12 : 0, 4, 5, direction > 0 ? 16 : 4, 12, 11, icon, cm);
                } else {
                    drawScaledPrism(tessellator, 5, 4, direction > 0 ? 12 : 0, 11, 12, direction > 0 ? 16 : 4, icon, cm);
                }
            } else if (Loader.isModLoaded(Names.Mods.mcMultipart) && (neighbour instanceof BlockMultipart)) {
                TileMultipart tile = BlockMultipart.getTile(channel.getWorldObj(), channel.xCoord + (x ? direction : 0), channel.yCoord, channel.zCoord + (x ? 0 : direction));
                for (TMultiPart multiPart : tile.jPartList()) {
                    if (multiPart instanceof LeverPart) {
                        LeverPart leverPart = (LeverPart) multiPart;
                        if (RenderHelper.isLeverFacingBlock(leverPart.getMetadata(), axis, direction)) {
                            IIcon icon = channel.getIcon();
                            if (x) {
                                drawScaledPrism(tessellator, direction > 0 ? 12 : 0, 4, 5, direction > 0 ? 16 : 4, 12, 11, icon, cm);
                            } else {
                                drawScaledPrism(tessellator, 5, 4, direction > 0 ? 12 : 0, 11, 12, direction > 0 ? 16 : 4, icon, cm);
                            }
                            break;
                        }
                    }
                }
            }
        }
        super.renderSide(channel, tessellator, axis, direction);
    }

    @Override
    protected void connectWater(TileEntityChannel channel, Tessellator tessellator, char axis, int direction, float y, IIcon icon) {
        TileEntityValve valve = (TileEntityValve) channel;
        if(axis=='x' || axis=='z') {
            //checks if there is a neighbouring block that this block can connect to
            if(channel.hasNeighbour(axis, direction)) {
                if (valve.isPowered()) {
                    boolean x = axis == 'x';
                    float y2 = valve.getDiscreteScaledFluidHeight();
                    this.drawWaterEdge(tessellator, x, direction, y2, y2, icon);
                } else {
                    super.connectWater(channel, tessellator, axis, direction, y, icon);
                }
            }
        }
    }
}
