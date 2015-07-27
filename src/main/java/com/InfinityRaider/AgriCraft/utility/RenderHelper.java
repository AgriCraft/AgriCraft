package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class RenderHelper {
    public static ResourceLocation getResource(Block block, int meta) {
        return getBlockResource(getIcon(block, meta));
    }

    public static ResourceLocation getResource(Item item, int meta) {
        return getItemResource(getIcon(item, meta));
    }

    public static IIcon getIcon(Item item, int meta) {
        if(item instanceof ItemBlock) {
            return ((ItemBlock) item).field_150939_a.getIcon(3, meta);
        }
        return item.getIconFromDamage(meta);
    }

    public static IIcon getIcon(Block block, int meta) {
        return  block.getIcon(0, meta);
    }

    public static ResourceLocation getBlockResource(IIcon icon) {
        if(icon==null) {
            return null;
        }
        String path = icon.getIconName();
        String domain = path.substring(0, path.indexOf(":") + 1);
        String file = path.substring(path.indexOf(':') + 1);
        return new ResourceLocation(domain + "textures/blocks/" + file + ".png");
    }

    public static ResourceLocation getItemResource(IIcon icon) {
        if(icon==null) {
            return null;
        }
        String path = icon.getIconName();
        String domain = path.substring(0, path.indexOf(":") + 1);
        String file = path.substring(path.indexOf(':')+1);
        return new ResourceLocation(domain+"textures/items/"+file+".png");
    }

    //checks if a lever is facing a block
    public static boolean isLeverFacingBlock(int leverMeta, char axis, int direction) {
        if(axis=='x') {
            if(direction>0) {
                return leverMeta % 8 == 1;
            }
            else {
                return leverMeta %8 == 2;
            }
        }
        else if(axis=='z') {
            if(direction>0) {
                return leverMeta %8 == 3;
            }
            else {
                return leverMeta %8 == 4;
            }
        }
        return false;
    }

    //adds a vertex to the tessellator scaled with 1/16th of a block
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    //same as above method, but does not require the correct texture to be bound
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x * unit, y * unit, z * unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    public static void drawScaledFaceXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z) {
        z = z*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
        //front
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV, icon);
    }

    public static void drawFaceXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z) {
        drawScaledFaceXY(tessellator, minX * 16, minY * 16, maxX * 16, maxY * 16, icon, z);
    }

    public static void drawScaledFaceXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y) {
        y = y*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
        //front
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV, icon);
    }

    public static void drawFaceXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y) {
        drawScaledFaceXY(tessellator, minX * 16, minZ * 16, maxX * 16, maxZ * 16, icon, y);
    }

    public static void drawScaledFaceYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x) {
        x = x*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
        //front
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV, icon);
        //back
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV, icon);
    }

    public static void drawFaceYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x) {
        drawScaledFaceYZ(tessellator, minY * 16, minZ * 16, maxY * 16, maxZ * 16, icon, x);
    }

    //draws a rectangular prism
    public static void drawScaledPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        //front plane
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, 16 - maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, 16-maxY, icon);
        //back plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, 16-minY, icon);
        //right plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, minZ, 16-maxY, icon);
        //left plane
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, maxZ, 16-minY, icon);
        //top plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, maxZ, icon);
        //bottom plane
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, minZ, icon);
    }

    //draws a rectangular prism
    public static void drawPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        drawScaledPrism(tessellator, minX*16, minY*16, minZ*16, maxX*16, maxY*16, maxZ*16, icon);
    }
}
