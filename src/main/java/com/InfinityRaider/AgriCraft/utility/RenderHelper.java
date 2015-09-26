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
    
    /**
     * Determines if a lever is facing a block, based off of the lever's metadata.
     * 
     * @param leverMeta the metadata value of the lever.
     * @param direction the direction of the block from the lever.
     * @return if the lever is facing the block.
     */
    public static boolean isLeverFacingBlock(int leverMeta, char axis, int direction) {
    	return isLeverFacingBlock(leverMeta, convertDirection(axis, direction));
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
     * Converts an axis-position direction to a ForgeDirection.
     * This is a temporary method to facilitate the conversion to ForgeDirection.
     * 
     * @param axis the axis of the direction. (x,y,z).
     * @param direction the magnitude of the direction. (+1,-1).
     * @return the associated ForgeDirection.
     */
    public static ForgeDirection convertDirection(char axis, int direction) {
    	if(axis=='x') {
            if(direction>0) {
                return ForgeDirection.EAST;
            }
            else {
                return ForgeDirection.WEST;
            }
        }
        else if(axis=='y') {
        	if(direction>0) {
                return ForgeDirection.UP;
            }
            else {
                return ForgeDirection.DOWN;
            }
        }
        else if(axis=='z') {
            if(direction>0) {
                return ForgeDirection.SOUTH;
            }
            else {
                return ForgeDirection.NORTH;
            }
        }
        return ForgeDirection.UNKNOWN;
    }
    
    /**
     * Retrieves the axis char for the old direction system.
     * 
     * @param direction the direction to lookup.
     * @return the char for the direction's axis.
     */
    public static char getAxis(ForgeDirection direction) {
    	switch(direction) {
    		case EAST:
    		case WEST:
    			return 'x';
    		case UP:
    		case DOWN:
    			return 'y';
    		case NORTH:
    		case SOUTH:
    			return 'z';
    		default:
    			return '?'; //Umm... Ignore this.
    	}
    }
    
    /**
     * Retrieves the axis length for the old direction system.
     * 
     * @param direction the direction to lookup.
     * @return how far the direction extends on the axis.
     */
    public static int getMagnitude(ForgeDirection direction) {
    	return direction.offsetX + direction.offsetY + direction.offsetZ; // Should always be in the -1 to 1 range.
    }
    
    /**
     * Rotates a plane. This is impressively useful, but may not be impressively efficient.
     * Always returns a 6-element array. Defaults to the north direction (the base direction).
     * <p>
     * This is for use up to the point that a way to rotate lower down is found. (IE. OpenGL).
     * </p>
     * 
     * TODO: add up/down support.
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
		}
        
        return adj;
        
    }

    //adds a vertex to the tessellator scaled with 1/16th of a block
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        tessellator.addVertexWithUV(x*Constants.UNIT, y*Constants.UNIT, z*Constants.UNIT, u*Constants.UNIT, v*Constants.UNIT);
    }

    //same as above method, but does not require the correct texture to be bound
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        tessellator.addVertexWithUV(x * Constants.UNIT, y * Constants.UNIT, z * Constants.UNIT, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
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
