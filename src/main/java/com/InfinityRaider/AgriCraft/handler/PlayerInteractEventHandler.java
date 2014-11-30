package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerUseItemEvent(PlayerInteractEvent event) {
        if(!event.world.isRemote) {
            LogHelper.debug("PlayerInteractEvent: ");
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().stackSize > 0 && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable && event.entityPlayer.getCurrentEquippedItem().hasTagCompound()) {
                    //prevent players from planting seeds with Gain, Growth and Strength stats
                    NBTTagCompound tag = event.entityPlayer.getCurrentEquippedItem().getTagCompound();
                    if (tag.hasKey(Names.growth) && tag.hasKey(Names.gain) && tag.hasKey(Names.strength)) {
                        if (event.world.getTileEntity(event.x, event.y, event.z) != null && event.world.getTileEntity(event.x, event.y, event.z) instanceof TileEntityCrop) {
                            event.setResult(Event.Result.ALLOW);
                        } else {
                            //TO DO: create tile entity at plant location storing the seed's data
                            event.setResult(Event.Result.DENY);
                            event.setCanceled(true);
                        }
                    }
                }
                //prevent Hunger Overhaul from meddling with my harvest logic
                else if(LoadedMods.hungerOverhaul) {
                    if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrop) {
                        if(event.entityPlayer.getCurrentEquippedItem()!=null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemDebugger) {
                            event.entityPlayer.getCurrentEquippedItem().getItem().onItemUse(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, event.world, event.x, event.y, event.z, 0, 0, 0, 0);
                        }
                        else {
                            LogHelper.debug("crop right clicked");
                            event.world.getBlock(event.x, event.y, event.z).onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, 0, 0, 0, 0);
                            event.setResult(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }
}
