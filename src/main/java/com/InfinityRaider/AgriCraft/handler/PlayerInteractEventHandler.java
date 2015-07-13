package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.tconstruct.TinkersConstructHelper;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerInteractEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedPlanting(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Block block = event.world.getBlock(event.x, event.y, event.z);
            if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().stackSize > 0 && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable) {
                if (GrowthRequirementHandler.isSoilValid(event.world, event.x, event.y, event.z) || block == Blocks.farmland) {
                    if (ConfigurationHandler.disableVanillaFarming) {
                        if(!SeedHelper.allowVanillaPlanting(event.entityPlayer.getCurrentEquippedItem())) {
                            this.denyEvent(event, false);
                        }
                    } else if (event.entityPlayer.getCurrentEquippedItem().hasTagCompound()) {
                        NBTTagCompound tag = (NBTTagCompound) event.entityPlayer.getCurrentEquippedItem().getTagCompound().copy();
                        if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                            //WIP: place a tile entity storing the seeds data
                            this.denyEvent(event, false);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void waterPadCreation(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            boolean flag = false;
            if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemSpade) {
                flag = true;
            } else if(ModHelper.allowIntegration(Names.Mods.tconstruct) && TinkersConstructHelper.isShovel(event.entityPlayer.getCurrentEquippedItem())) {
                flag = true;
            }
            if(flag) {
                if (event.world.isRemote) {
                    return;
                }
                Block block = event.world.getBlock(event.x, event.y, event.z);
                if (block == Blocks.farmland) {
                    event.world.setBlock(event.x, event.y, event.z, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPad, 0, 3);
                    if (!event.entityPlayer.capabilities.isCreativeMode) {
                        event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
                        event.setResult(Event.Result.ALLOW);
                    }
                    event.world.playSoundEffect((double) ((float) event.x + 0.5F), (double) ((float) event.y + 0.5F), (double) ((float) event.z + 0.5F), block.stepSound.getStepResourcePath(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
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
