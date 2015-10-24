package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to aid in the management of debug data.
 */
public abstract class DebugHelper {
	
    /**
     * Retrieves the debug data for a location, and displays it in a chat message to the specified player in conjunction with the log.
     * 
     * @param player the player requesting the debug data.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void debug(EntityPlayer player, World world, int x, int y, int z) {
        for(String dataLine:getDebugData(world, x, y,z)) {
            LogHelper.debug(dataLine);
            //player.addChatComponentMessage(new ChatComponentText(dataLine));
        }
    }

    /**
     * Constructs a list of strings representing the debug information for the provided location.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return a list of strings representing the requested debug data.
     */
    private static List<String> getDebugData(World world, int x, int y, int z) {
    	
    	List<String> debugData = new ArrayList<String>();
    	
        if (!world.isRemote) {
            debugData.add("Server debug info:");
            debugData.add("------------------");
        } else {
            debugData.add("Client debug info:");
            debugData.add("------------------");
        }
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if(tile!=null && tile instanceof IDebuggable) {
            ((IDebuggable) tile).addDebugInfo(debugData);
        }
        else {
            debugData.add("Block: "+ Block.blockRegistry.getNameForObject(world.getBlock(x, y, z)));
            debugData.add("Meta: "+world.getBlockMetadata(x, y, z));
        }
        
        debugData.add(" ");
        
        return debugData;
    }
}
