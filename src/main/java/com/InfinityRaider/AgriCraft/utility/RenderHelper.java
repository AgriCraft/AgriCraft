package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
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
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    //same as above method, but does not require the correct texture to be bound
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x * unit, y * unit, z * unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    //utility method: splits the string in different lines so it will fit on the page
    public static String splitInLines(FontRenderer fontRendererObj, String input, float maxWidth, float scale) {
        maxWidth = maxWidth / scale;
        String notProcessed = input;
        String output = "";
        while (fontRendererObj.getStringWidth(notProcessed) > maxWidth) {
            int index = 0;
            if (notProcessed != null && !notProcessed.equals("")) {
                //find the first index at which the string exceeds the size limit
                while (notProcessed.length() - 1 > index && fontRendererObj.getStringWidth(notProcessed.substring(0, index)) < maxWidth) {
                    index = (index + 1) < notProcessed.length() ? index + 1 : index;
                }
                //go back to the first space to cut the string in two lines
                while (index>0 && notProcessed.charAt(index) != ' ') {
                    index--;
                }
                //update the data for the next iteration
                output = output.equals("") ? output : output + '\n';
                output = output + notProcessed.substring(0, index);
                notProcessed = notProcessed.length() > index + 1 ? notProcessed.substring(index + 1) : notProcessed;
            }
        }
        return output + '\n' + notProcessed;
    }
}
