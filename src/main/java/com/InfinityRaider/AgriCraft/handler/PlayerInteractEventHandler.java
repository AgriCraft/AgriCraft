package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.items.ItemMagnifyingGlass;
import com.InfinityRaider.AgriCraft.items.ItemTrowel;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.BlockFarmland;
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
            if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockFarmland) {
                if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().stackSize > 0 && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable) {
                    if (ConfigurationHandler.disableVanillaFarming && SeedHelper.isValidSeed((ItemSeeds) event.entityPlayer.getCurrentEquippedItem().getItem(), event.entityPlayer.getCurrentEquippedItem().getItemDamage())) {
                        this.denyEvent(event, false);
                    } else if(event.entityPlayer.getCurrentEquippedItem().hasTagCompound()){
                        NBTTagCompound tag = (NBTTagCompound) event.entityPlayer.getCurrentEquippedItem().getTagCompound().copy();
                        if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                            //WIP: place a tile entity storing the seeds data
                            this.denyEvent(event, false);
                        }
                    }
                }
            } else if (event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrop) {
                //prevent Hunger Overhaul from meddling with my harvest logic
                if (ModIntegration.LoadedMods.hungerOverhaul) {
                    if (event.entityPlayer.getCurrentEquippedItem() != null) {
                        if (event.entityPlayer.getCurrentEquippedItem().getItem() == Items.dye && event.entityPlayer.getCurrentEquippedItem().getItemDamage() == 15) {
                            //bonemeal
                            if (!((TileEntityCrop) event.world.getTileEntity(event.x, event.y, event.z)).isMature()) {
                                if (!event.world.isRemote) {
                                    event.setResult(Event.Result.ALLOW);
                                    return;
                                }
                            }
                        }
                        //debugger can be used
                        else if (event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemDebugger) {
                            event.entityPlayer.getCurrentEquippedItem().getItem().onItemUse(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, event.world, event.x, event.y, event.z, event.face, 0, 0, 0);
                        }
                        //use the trowel or magnifying glass
                        else if (event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemTrowel || event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemMagnifyingGlass) {
                            event.entityPlayer.getCurrentEquippedItem().getItem().onItemUseFirst(event.entityPlayer.getCurrentEquippedItem(), event.entityPlayer, event.world, event.x, event.y, event.z, event.face, 0, 0, 0);
                            this.denyEvent(event, event.world.isRemote);
                            return;
                        }
                    }
                    //call the block's onBlockActivated method
                    event.world.getBlock(event.x, event.y, event.z).onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, event.face, 0, 0, 0);
                    //cancel event to prevent the Hunger Overhaul event handler from being called
                    this.denyEvent(event, event.world.isRemote);
                }
            }
        }
    }

    private void denyEvent(PlayerInteractEvent event, boolean sendToServer) {
        //cancel event to prevent the Hunger Overhaul event handler from being called
        event.setResult(Event.Result.DENY);
        event.useItem = Event.Result.DENY;
        event.useBlock = Event.Result.DENY;
        if (sendToServer) {
            //send the right click to the server manually (cancelling the event will prevent the client from telling the server a right click happened, and nothing will happen, but we still want stuff to happen)
            FMLClientHandler.instance().getClientPlayerEntity().sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(event.x, event.y, event.z, event.face, event.entityPlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
        }
        event.setCanceled(true);
    }
}
