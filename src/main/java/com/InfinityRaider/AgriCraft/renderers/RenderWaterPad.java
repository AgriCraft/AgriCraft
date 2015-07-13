package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPad;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPadFull;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class RenderWaterPad implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor3f(1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();
        boolean full = block instanceof BlockWaterPadFull;
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 0, 16, 8, 16, Blocks.dirt.getIcon(0, 0));
        RenderHelper.drawScaledPrism(tessellator, 1, 8, 0, 1, 15, 16, Blocks.dirt.getIcon(0, 0));
        RenderHelper.drawScaledPrism(tessellator, 15, 8, 1, 16, 15, 16, Blocks.dirt.getIcon(0, 0));
        RenderHelper.drawScaledPrism(tessellator, 0, 8, 15, 15, 15, 16, Blocks.dirt.getIcon(0, 0));
        RenderHelper.drawScaledPrism(tessellator, 0, 8, 0, 15, 1, 15, Blocks.dirt.getIcon(0, 0));
        if(full) {
            RenderHelper.drawScaledPrism(tessellator, 1, 14, 1, 15, 15, 15, Blocks.water.getIcon(0, 0));
        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        boolean full = block instanceof BlockWaterPadFull;
        this.renderBase(tessellator, world, x, y, z, full, renderer);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.NORTH);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.EAST);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.SOUTH);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.WEST);
        return false;
    }

    private void renderBase(Tessellator tessellator, IBlockAccess world, int x, int y, int z, boolean full, RenderBlocks renderer) {
        float u = Constants.unit;
        tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        renderer.setRenderBounds(0, 0, 0, 16 * u, 8 * u, 16 * u);
        renderer.renderStandardBlock(Blocks.dirt, x, y, z);

        boolean renderAllFaces = renderer.renderAllFaces;
        renderer.renderAllFaces = true;

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.WEST, ForgeDirection.NORTH)) {
            renderer.setRenderBounds(0, 8 * u, 0, u, 15 * u, 1 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            tessellator.addTranslation(x, y, z);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 14, 1, 0, 1, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 0, 1, 0, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.NORTH, ForgeDirection.EAST)) {
            renderer.setRenderBounds(15 * u, 8 * u, 0, 16 * u, 15 * u, 1 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            tessellator.addTranslation(x, y, z);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 0, 15, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 14, 1, 16, 1, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.EAST, ForgeDirection.SOUTH)) {
            renderer.setRenderBounds(15 * u, 8 * u, 15 * u, 16 * u, 15 * u, 16 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            tessellator.addTranslation(x, y, z);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 16, 15, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 14, 15, 16, 15, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.SOUTH, ForgeDirection.WEST)) {
            renderer.setRenderBounds(0, 8 * u, 15 * u, u, 15 * u, 16 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            tessellator.addTranslation(x, y, z);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 14, 15, 0, 15, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 16, 1, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        renderer.renderAllFaces = renderAllFaces;

        if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            tessellator.addTranslation(x, y, z);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, icon);
            tessellator.addTranslation(-x, -y, -z);
        }
    }

    private boolean shouldRenderCorner(IBlockAccess world, int x, int y, int z, boolean full, ForgeDirection dir1, ForgeDirection dir2) {
        Block block = world.getBlock(x + dir1.offsetX, y, z + dir1.offsetZ);
        boolean flag1 = block instanceof BlockWaterPad;
        boolean flag2 =  block instanceof BlockWaterPadFull;
        if(!flag1 || (full!=flag2)) {
            return true;
        }
        block = world.getBlock(x + dir2.offsetX, y, z + dir2.offsetZ);
        flag1 = block instanceof BlockWaterPad;
        flag2 = block instanceof BlockWaterPadFull;
        if(!flag1 || (full!=flag2)) {
            return true;
        }
        block = world.getBlock(x + dir1.offsetX + dir2.offsetX, y, z + dir1.offsetZ + dir2.offsetZ);
        flag1 = block instanceof BlockWaterPad;
        flag2 = block instanceof BlockWaterPadFull;
        return !flag1 || (full!=flag2);
    }

    private void renderSide(Tessellator tessellator, IBlockAccess world, int x, int y, int z, boolean full, RenderBlocks renderer, ForgeDirection side) {
        float u = Constants.unit;
        int xLower = Math.max(0, 1 + 14 * side.offsetX);
        int xUpper = Math.min(16, 15 + 14 * side.offsetX);
        int zLower = Math.max(0, 1 + 14 * side.offsetZ);
        int zUpper = Math.min(16, 15 + 14 * side.offsetZ);
        Block block = world.getBlock(x+side.offsetX, y, z+side.offsetZ);
        if(block!=null && block instanceof BlockWaterPad) {
            boolean flag = block instanceof BlockWaterPadFull;
            if (full) {
                IIcon icon = Blocks.water.getIcon(0, 0);
                tessellator.addTranslation(x, y, z);
                RenderHelper.addScaledVertexWithUV(tessellator, xLower, 14, zLower, xLower, zLower, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, xLower, 14, zUpper, xLower, zUpper, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, xUpper, 14, zUpper, xUpper, zUpper, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, xUpper, 14, zLower, xUpper, zLower, icon);
                tessellator.addTranslation(-x, -y, -z);
            }
            if (flag == full) {
                return;
            }
        }
        tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        boolean renderAllFaces = renderer.renderAllFaces;
        renderer.renderAllFaces = true;
        renderer.setRenderBounds(xLower * u, 8 * u, zLower * u, xUpper * u, 15 * u, zUpper * u);
        renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        renderer.renderAllFaces = renderAllFaces;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.waterPadId);
    }
}
