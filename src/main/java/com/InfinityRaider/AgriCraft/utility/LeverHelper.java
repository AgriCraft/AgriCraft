package com.InfinityRaider.AgriCraft.utility;


import net.minecraftforge.common.util.ForgeDirection;

public abstract class LeverHelper {
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
}
