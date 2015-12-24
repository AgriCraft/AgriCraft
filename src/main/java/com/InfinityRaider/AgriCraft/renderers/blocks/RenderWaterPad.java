package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterPad;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPadFull;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockBase {
    public RenderWaterPad(Block block) {
        super(block, true);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, BlockPos pos, IBlockState state, Block block, TileEntity tile, int modelId, float f) {
        int x = (int) xCoord;
        int y = (int) yCoord;
        int z = (int) zCoord;
        boolean full = block instanceof BlockWaterPadFull;
        tessellator.startDrawingQuads();
        this.renderBase(tessellator, world, pos, full);
        this.renderSide(tessellator, world, pos, full, ForgeDirection.NORTH);
        this.renderSide(tessellator, world, pos, full, ForgeDirection.EAST);
        this.renderSide(tessellator, world, pos, full, ForgeDirection.SOUTH);
        this.renderSide(tessellator, world, pos, full, ForgeDirection.WEST);
        tessellator.draw();
        return false;
    }

    private void renderBase(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos, boolean full) {
        float u = Constants.UNIT;
        tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        //boolean renderAllFaces = renderer.renderAllFaces;
        //renderer.renderAllFaces = true;

        if (shouldRenderCorner(world, pos, full, ForgeDirection.WEST, ForgeDirection.NORTH)) {
            //renderer.setRenderBounds(0, 8 * u, 0, u, 15 * u, 1 * u);
            //renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            int l = Blocks.water.colorMultiplier(world, pos);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
            addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0);
            addScaledVertexWithUV(tessellator, 0, 14, 1, 0, 1);
            addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1);
            addScaledVertexWithUV(tessellator, 1, 14, 0, 1, 0);
            tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        if (shouldRenderCorner(world, pos, full, ForgeDirection.NORTH, ForgeDirection.EAST)) {
            //renderer.setRenderBounds(15 * u, 8 * u, 0, 16 * u, 15 * u, 1 * u);
            //renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            int l = Blocks.water.colorMultiplier(world, pos);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
            addScaledVertexWithUV(tessellator, 15, 14, 0, 15, 0);
            addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1);
            addScaledVertexWithUV(tessellator, 16, 14, 1, 16, 1);
            addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0);
            tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        if (shouldRenderCorner(world, pos, full, ForgeDirection.EAST, ForgeDirection.SOUTH)) {
            //renderer.setRenderBounds(15 * u, 8 * u, 15 * u, 16 * u, 15 * u, 16 * u);
            //renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            int l = Blocks.water.colorMultiplier(world, pos);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
            addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15);
            addScaledVertexWithUV(tessellator, 15, 14, 16, 15, 16);
            addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16);
            addScaledVertexWithUV(tessellator, 16, 14, 15, 16, 15);
            tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        if (shouldRenderCorner(world, pos, full, ForgeDirection.SOUTH, ForgeDirection.WEST)) {
            //renderer.setRenderBounds(0, 8 * u, 15 * u, u, 15 * u, 16 * u);
            //renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        } else if(full) {
            int l = Blocks.water.colorMultiplier(world, pos);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
            addScaledVertexWithUV(tessellator, 0, 14, 15, 0, 15);
            addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16);
            addScaledVertexWithUV(tessellator, 1, 14, 16, 1, 16);
            addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15);
            tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }

        //renderer.renderAllFaces = renderAllFaces;

        if(full) {
            int l = Blocks.water.colorMultiplier(world, pos);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
            addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1);
            addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15);
            addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15);
            addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1);
            tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }
    }

    private boolean shouldRenderCorner(IBlockAccess world, BlockPos pos, boolean full, ForgeDirection dir1, ForgeDirection dir2) {
        Block block = world.getBlockState(pos.add(dir1.offsetX, 0,  dir1.offsetZ)).getBlock();
        boolean flag1 = block instanceof BlockWaterPad;
        boolean flag2 =  block instanceof BlockWaterPadFull;
        if(!flag1 || (full!=flag2)) {
            return true;
        }
        block = world.getBlockState(pos.add(dir2.offsetX, 0,  dir2.offsetZ)).getBlock();
        flag1 = block instanceof BlockWaterPad;
        flag2 = block instanceof BlockWaterPadFull;
        if(!flag1 || (full!=flag2)) {
            return true;
        }
        block = world.getBlockState(pos.add(dir1.offsetX + dir2.offsetX, 0, dir1.offsetZ + dir2.offsetZ)).getBlock();
        flag1 = block instanceof BlockWaterPad;
        flag2 = block instanceof BlockWaterPadFull;
        return !flag1 || (full!=flag2);
    }

    private void renderSide(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos, boolean full, ForgeDirection side) {
        float u = Constants.UNIT;
        int xLower = Math.max(0, 1 + 14 * side.offsetX);
        int xUpper = Math.min(16, 15 + 14 * side.offsetX);
        int zLower = Math.max(0, 1 + 14 * side.offsetZ);
        int zUpper = Math.min(16, 15 + 14 * side.offsetZ);
        Block block = world.getBlockState(pos.add(side.offsetX, 0, side.offsetZ)).getBlock();
        if(block!=null && block instanceof BlockWaterPad) {
            boolean flag = block instanceof BlockWaterPadFull;
            if (full) {
                int l = Blocks.water.colorMultiplier(world, pos);
                float f = (float)(l >> 16 & 255) / 255.0F;
                float f1 = (float)(l >> 8 & 255) / 255.0F;
                float f2 = (float)(l & 255) / 255.0F;
                float f4 = 1.0F;
                tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
                tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
                tessellator.addTranslation(pos.getX(), pos.getY(), pos.getZ());
                addScaledVertexWithUV(tessellator, xLower, 14, zLower, xLower, zLower);
                addScaledVertexWithUV(tessellator, xLower, 14, zUpper, xLower, zUpper);
                addScaledVertexWithUV(tessellator, xUpper, 14, zUpper, xUpper, zUpper);
                addScaledVertexWithUV(tessellator, xUpper, 14, zLower, xUpper, zLower);
                tessellator.addTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
            }
            if (flag == full) {
                return;
            }
        }
        tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        /*
        boolean renderAllFaces = renderer.renderAllFaces;
        renderer.renderAllFaces = true;
        renderer.setRenderBounds(xLower * u, 8 * u, zLower * u, xUpper * u, 15 * u, zUpper * u);
        renderer.renderStandardBlock(Blocks.farmland, x, y, z);
        renderer.renderAllFaces = renderAllFaces;
        */
    }
}
