package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEventHandler {
    @SubscribeEvent
    public void onPlayerUseItemEvent(PlayerInteractEvent event) {
        if(event.action==PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer.getCurrentEquippedItem().getItem()!=null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable && event.entityPlayer.getCurrentEquippedItem().hasTagCompound()) {
            NBTTagCompound tag = event.entityPlayer.getCurrentEquippedItem().getTagCompound();
            if(tag.hasKey(Names.growth) && tag.hasKey(Names.gain) && tag.hasKey(Names.strength)) {
                if(event.world.getTileEntity(event.x, event.y, event.z)!=null && event.world.getTileEntity(event.x, event.y, event.z) instanceof TileEntityCrop) {
                    event.setResult(Event.Result.ALLOW);
                }
                else {
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
