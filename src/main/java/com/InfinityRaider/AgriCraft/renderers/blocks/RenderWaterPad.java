package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterPad;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPadFull;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockBase {
    public RenderWaterPad(Block block) {
        super(block, true);
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor3f(1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();
        boolean full = ((ItemBlock) item.getItem()).field_150939_a instanceof BlockWaterPadFull;
        int cm = COLOR_MULTIPLIER_STANDARD;
        drawScaledPrism(tessellator, 0, 0, 0, 16, 8, 16, Blocks.dirt.getIcon(0, 0), cm);
        drawScaledPrism(tessellator, 1, 8, 0, 1, 15, 16, Blocks.dirt.getIcon(0, 0), cm);
        drawScaledPrism(tessellator, 15, 8, 1, 16, 15, 16, Blocks.dirt.getIcon(0, 0), cm);
        drawScaledPrism(tessellator, 0, 8, 15, 15, 15, 16, Blocks.dirt.getIcon(0, 0), cm);
        drawScaledPrism(tessellator, 0, 8, 0, 15, 1, 15, Blocks.dirt.getIcon(0, 0), cm);
        if(full) {
            drawScaledPrism(tessellator, 1, 14, 1, 15, 15, 15, Blocks.water.getIcon(0, 0), cm);
        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);

    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator2, IBlockAccess world, double xCoord, double yCoord, double zCoord, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        Tessellator tessellator = Tessellator.instance;
        int x = (int) xCoord;
        int y = (int) yCoord;
        int z = (int) zCoord;
        boolean full = block instanceof BlockWaterPadFull;
        this.renderBase(tessellator, world, x, y, z, full, renderer);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.NORTH);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.EAST);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.SOUTH);
        this.renderSide(tessellator, world, x, y, z, full, renderer, ForgeDirection.WEST);
        return false;
    }

    private void renderBase(Tessellator tessellator, IBlockAccess world, int x, int y, int z, boolean full, RenderBlocks renderer) {
        float u = Constants.UNIT;
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
            int l = Blocks.water.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(x, y, z);
            addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0, icon);
            addScaledVertexWithUV(tessellator, 0, 14, 1, 0, 1, icon);
            addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, icon);
            addScaledVertexWithUV(tessellator, 1, 14, 0, 1, 0, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.NORTH, ForgeDirection.EAST)) {
            renderer.setRenderBounds(15 * u, 8 * u, 0, 16 * u, 15 * u, 1 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            int l = Blocks.water.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(x, y, z);
            addScaledVertexWithUV(tessellator, 15, 14, 0, 15, 0, icon);
            addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, icon);
            addScaledVertexWithUV(tessellator, 16, 14, 1, 16, 1, icon);
            addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.EAST, ForgeDirection.SOUTH)) {
            renderer.setRenderBounds(15 * u, 8 * u, 15 * u, 16 * u, 15 * u, 16 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            int l = Blocks.water.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(x, y, z);
            addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, icon);
            addScaledVertexWithUV(tessellator, 15, 14, 16, 15, 16, icon);
            addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, icon);
            addScaledVertexWithUV(tessellator, 16, 14, 15, 16, 15, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        if (shouldRenderCorner(world, x, y, z, full, ForgeDirection.SOUTH, ForgeDirection.WEST)) {
            renderer.setRenderBounds(0, 8 * u, 15 * u, u, 15 * u, 16 * u);
            renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            int l = Blocks.water.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(x, y, z);
            addScaledVertexWithUV(tessellator, 0, 14, 15, 0, 15, icon);
            addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16, icon);
            addScaledVertexWithUV(tessellator, 1, 14, 16, 1, 16, icon);
            addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, icon);
            tessellator.addTranslation(-x, -y, -z);
        }

        renderer.renderAllFaces = renderAllFaces;

        if(full) {
            IIcon icon = Blocks.water.getIcon(0, 0);
            int l = Blocks.water.colorMultiplier(world, x, y, z);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(x, y, z);
            addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, icon);
            addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, icon);
            addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, icon);
            addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, icon);
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
        float u = Constants.UNIT;
        int xLower = Math.max(0, 1 + 14 * side.offsetX);
        int xUpper = Math.min(16, 15 + 14 * side.offsetX);
        int zLower = Math.max(0, 1 + 14 * side.offsetZ);
        int zUpper = Math.min(16, 15 + 14 * side.offsetZ);
        Block block = world.getBlock(x+side.offsetX, y, z+side.offsetZ);
        if(block!=null && block instanceof BlockWaterPad) {
            boolean flag = block instanceof BlockWaterPadFull;
            if (full) {
                IIcon icon = Blocks.water.getIcon(0, 0);
                int l = Blocks.water.colorMultiplier(world, x, y, z);
                float f = (float)(l >> 16 & 255) / 255.0F;
                float f1 = (float)(l >> 8 & 255) / 255.0F;
                float f2 = (float)(l & 255) / 255.0F;
                float f4 = 1.0F;
                tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, x, y, z));
                tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
                tessellator.addTranslation(x, y, z);
                addScaledVertexWithUV(tessellator, xLower, 14, zLower, xLower, zLower, icon);
                addScaledVertexWithUV(tessellator, xLower, 14, zUpper, xLower, zUpper, icon);
                addScaledVertexWithUV(tessellator, xUpper, 14, zUpper, xUpper, zUpper, icon);
                addScaledVertexWithUV(tessellator, xUpper, 14, zLower, xUpper, zLower, icon);
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
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
