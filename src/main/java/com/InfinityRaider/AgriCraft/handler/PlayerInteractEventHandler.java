package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerUseItemEvent(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if(event.world.getBlock(event.x, event.y, event.z)==Blocks.farmland) {
                if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().stackSize > 0 && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable && event.entityPlayer.getCurrentEquippedItem().hasTagCompound()) {
                    if(ConfigurationHandler.disableVanillaFarming && SeedHelper.isValidSeed((ItemSeeds)  event.entityPlayer.getCurrentEquippedItem().getItem())) {
                        event.setResult(Event.Result.DENY);
                        event.setCanceled(true);
                    }
                    else {
                        //prevent players from planting seeds with Gain, Growth and Strength stats
                        NBTTagCompound tag = event.entityPlayer.getCurrentEquippedItem().getTagCompound();
                        if (tag.hasKey(Names.growth) && tag.hasKey(Names.gain) && tag.hasKey(Names.strength)) {
                            event.setResult(Event.Result.DENY);
                            event.setCanceled(true);
                            //TO-DO: create tile-entity to store seed data
                        }
                    }
                }
            }
            else if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrop) {
                //prevent Hunger Overhaul from meddling with my harvest logic
                if(LoadedMods.hungerOverhaul) {
                    if(event.entityPlayer.getCurrentEquippedItem()!=null) {
                        if(event.entityPlayer.getCurrentEquippedItem().getItem()==Items.dye && event.entityPlayer.getCurrentEquippedItem().getItemDamage()==15) {
                            //bonemeal
                            if(!((TileEntityCrop) event.world.getTileEntity(event.x, event.y, event.z)).isMature()) {
                                if(!event.world.isRemote) {
                                    event.setResult(Event.Result.ALLOW);
                                    return;
                                }
                            }
                        }
                        else if(event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemDebugger) {
                            event.entityPlayer.getCurrentEquippedItem().getItem().onItemUse(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, event.world, event.x, event.y, event.z, event.face, 0, 0, 0);
                        }
                    }
                    event.world.getBlock(event.x, event.y, event.z).onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, event.face, 0, 0, 0);
                    //cancel event to prevent the Hunger Overhaul event handler from being called
                    event.setResult(Event.Result.DENY);
                    event.useItem = Event.Result.DENY;
                    event.useBlock = Event.Result.DENY;
                    if(event.world.isRemote) {
                        //send the right click to the server manually (cancelling the event will prevent the client from telling the server a right click happened, and nothing will happen, but we still want stuff to happen)
                        FMLClientHandler.instance().getClientPlayerEntity().sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(event.x, event.y, event.z, event.face, event.entityPlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                    }
                    event.setCanceled(true);
                }
            }
        }

    }
}
