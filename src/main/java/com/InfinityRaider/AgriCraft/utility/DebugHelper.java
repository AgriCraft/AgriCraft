package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class DebugHelper {
    public static void debug(EntityPlayer player, World world, int x, int y, int z) {
        ArrayList<String> list = new ArrayList<String>();
        getDebugData(world, x, y,z, list);
        for(String data:list) {
            LogHelper.debug(data);
            player.addChatComponentMessage(new ChatComponentText(data));
        }
    }

    public static void debug(World world, int x, int y, int z) {
        ArrayList<String> list = new ArrayList<String>();
        getDebugData(world, x, y,z, list);
        for(String data:list) {
            LogHelper.debug(data);
        }
    }

    private static void getDebugData(World world, int x, int y, int z, List<String> list) {
        if (!world.isRemote) {
            list.add("Server debug info:");
            list.add("------------------");
        } else {
            list.add("Client debug info:");
            list.add("------------------");
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile!=null && tile instanceof IDebuggable) {
            ((IDebuggable) tile).addDebugInfo(list);
        }
        else {
            list.add("Block: "+ Block.blockRegistry.getNameForObject(world.getBlock(x, y, z)));
            list.add("Meta: "+world.getBlockMetadata(x, y, z));
        }
        list.add(" ");
    }
}
