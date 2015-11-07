package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class RenderHelper {
    /**
     * Retrieves an item's icon, with meta support.
     * 
     * @param item the item to get the icon for.
     * @param meta the meta value (damage) of the item.
     * @return the icon representing the item.
     */
    public static IIcon getIcon(Item item, int meta) {
        if(item instanceof ItemBlock) {
            return ((ItemBlock) item).field_150939_a.getIcon(3, meta);
        }
        return item.getIconFromDamage(meta);
    }

    /**
     * Retrieves an block's icon, with meta support.
     * 
     * @param block the block to get the icon for.
     * @param meta the meta value of the block.
     * @return the icon representing the block.
     */
    public static IIcon getIcon(Block block, int meta) {
        return  block.getIcon(0, meta);
    }

    /**
     * Retrieves a resource location from a <em>block</em> icon.
     * 
     * @param icon the icon to get the resource location from.
     * @return the resource location for the icon, or null.
     */
    public static ResourceLocation getBlockResource(IIcon icon) {
        if(icon==null) {
            return null;
        }
        String path = icon.getIconName();
        String domain = path.substring(0, path.indexOf(":") + 1);
        String file = path.substring(path.indexOf(':') + 1);
        return new ResourceLocation(domain + "textures/blocks/" + file + ".png");
    }
    
    /**
     * Determines if a lever is facing a block, based off of the lever's metadata.
     * 
     * @param leverMeta the metadata value of the lever.
     * @param direction the direction of the block from the lever.
     * @return if the lever is facing the block.
     */
    public static boolean isLeverFacingBlock(int leverMeta, ForgeDirection direction) {
    	switch(direction) {
    		case EAST:
    			return leverMeta % 8 == 1;
    		case WEST:
    			return leverMeta % 8 == 2;
    		case SOUTH:
    			return leverMeta % 8 == 3;
    		case NORTH:
    			return leverMeta % 8 == 4;
    		default:
    			return false;
    	}
    }
    
    /**
     * Rotates a plane. This is impressively useful, but may not be impressively efficient.
     * Always returns a 6-element array. Defaults to the north direction (the base direction).
     * <p>
     * This is for use up to the point that a way to rotate lower down is found. (IE. OpenGL).
     * </p>
     * 
     * TODO: Test up/down rotations more thoroughly.
     */
    public static float[] rotatePrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, ForgeDirection direction) {
        float adj[] = new float[6];
        
        switch (direction) {
        	default:
			case NORTH:
				adj[0] = minX; //-x
				adj[1] = minY; //-y
				adj[2] = minZ; //-z
				adj[3] = maxX; //+x
				adj[4] = maxY; //+y
				adj[5] = maxZ; //+z
				break;
			case EAST:
				adj[0] = Constants.WHOLE - maxZ; //-x
				adj[1] = minY; //-y
				adj[2] = minX; //-z
				adj[3] = Constants.WHOLE - minZ; //+x
				adj[4] = maxY; //+y
				adj[5] = maxX; //+z
				break;
			case SOUTH:
				adj[0] = minX; //-x
				adj[1] = minY; //-y
				adj[2] = Constants.WHOLE - maxZ; //-z
				adj[3] = maxX; //+x
				adj[4] = maxY; //+y
				adj[5] = Constants.WHOLE - minZ; //+z
				break;
			case WEST:
				adj[0] = minZ; //-x
				adj[1] = minY; //-y
				adj[2] = minX; //-z
				adj[3] = maxZ; //+x
				adj[4] = maxY; //+y
				adj[5] = maxX; //+z
				break;
			case UP:
				adj[0] = minX; //-x
				adj[1] = Constants.WHOLE - maxZ; //-y
				adj[2] = minY; //-z
				adj[3] = maxX; //+x
				adj[4] = Constants.WHOLE - minZ; //+y
				adj[5] = maxY; //+z
			case DOWN:
				adj[0] = minX; //-x
				adj[1] = minZ; //-y
				adj[2] = minY; //-z
				adj[3] = maxX; //+x
				adj[4] = maxZ; //+y
				adj[5] = maxY; //+z
		}
        return adj;
    }

    /**
     * Adds a vertex to the tessellator scaled to the unit size of a block.
     * 
     * @param tessellator the Tessellator instance used for rendering
     * @param x the x position, from 0 to 1.
     * @param y the y position, from 0 to 1.
     * @param z the z position, from 0 to 1.
     * @param u u offset for the bound texture
     * @param v v offset for the bound texture
     */
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        tessellator.addVertexWithUV(x*Constants.UNIT, y*Constants.UNIT, z*Constants.UNIT, u*Constants.UNIT, v*Constants.UNIT);
    }

    //same as above method, but does not require the correct texture to be bound
    /**
     * Adds a vertex to the tessellator scaled to the unit size of a block.
     * Same as {@link #addScaledVertexWithUV(Tessellator, float, float, float, float, float)}, but does not require the correct texture to be bound.
     * 
     * @param tessellator the Tessellator instance used for rendering
     * @param x the x position, from 0 to 1
     * @param y the y position, from 0 to 1
     * @param z the z position, from 0 to 1
     * @param u u offset for the bound texture
     * @param v v offset for the bound texture
     * @param icon the icon to render
     */
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        tessellator.addVertexWithUV(x * Constants.UNIT, y * Constants.UNIT, z * Constants.UNIT, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    /**
     * Utility method: splits the string in different lines so it will fit on the page.
     * 
     * @param fontRendererObj the font renderer to check against.
     * @param input the line to split up.
     * @param maxWidth the maximum allowable width of the line before being wrapped.
     * @param scale the scale of the text to the width.
     * @return the string split up into lines by the '\n' character.
     */
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
