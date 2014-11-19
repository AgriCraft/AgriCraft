package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;

import java.util.Random;

//a class to handle bonemeal events
public class BonemealEventHandler {
    @SubscribeEvent
    public void onBonemealEvent(BonemealEvent event) {
        if (event.block instanceof BlockCrop) {
            TileEntityCrop crop = (TileEntityCrop) event.world.getTileEntity(event.x, event.y, event.z);
            if((crop.hasPlant() && crop.getBlockMetadata()<7) || (crop.crossCrop && ConfigurationHandler.bonemealMutation)) {
                ((BlockCrop) event.world.getBlock(event.x, event.y, event.z)).func_149853_b(event.world, new Random(), event.x, event.y, event.z);
                event.setResult(Event.Result.ALLOW);
            } else {
                event.setCanceled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
