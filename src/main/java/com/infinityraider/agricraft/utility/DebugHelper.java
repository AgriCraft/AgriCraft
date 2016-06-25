package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.v1.misc.IAgriDebuggable;

/**
 * A class to aid in the management of debug data.
 */
public abstract class DebugHelper {
	
    /**
     * Retrieves the debug data for a location, and displays it in a chat message to the specified player in conjunction with the log.
     * 
     * @param player the player requesting the debug data.
     * @param world the world object
     * @param pos the block position
     */
    public static void debug(EntityPlayer player, World world, BlockPos pos) {
        for(String dataLine:getDebugData(world, pos)) {
            AgriCore.getLogger("AgriCraft").debug(dataLine);
            player.addChatComponentMessage(new TextComponentString(dataLine));
        }
    }

    /**
     * Constructs a list of strings representing the debug information for the provided location.
     * 
     * @param world the world object
     * @param pos the block position
     * @return a list of strings representing the requested debug data.
     */
    private static List<String> getDebugData(World world, BlockPos pos) {
    	
    	List<String> debugData = new ArrayList<>();
    	
        if (!world.isRemote) {
            debugData.add("Server debug info:");
            debugData.add("------------------");
        } else {
            debugData.add("Client debug info:");
            debugData.add("------------------");
        }
        
        TileEntity tile = world.getTileEntity(pos);
        
        if(tile!=null && tile instanceof IAgriDebuggable) {
            ((IAgriDebuggable) tile).addDebugInfo(debugData);
        }
        else {
            debugData.add("Block: "+ Block.REGISTRY.getNameForObject(world.getBlockState(pos).getBlock()));
        }
        
        debugData.add(" ");
        
        return debugData;
    }
}
