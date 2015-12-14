package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.blocks.BlockGrate;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.tconstruct.TinkersConstructHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@SuppressWarnings("unused")
public class PlayerInteractEventHandler {
    /** Event handler to disable vanilla farming */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedPlanting(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().stackSize > 0 && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IPlantable) {
                if (GrowthRequirementHandler.isSoilValid(event.world, event.x, event.y, event.z)) {
                    if (ConfigurationHandler.disableVanillaFarming) {
                        if(!allowVanillaPlanting(event.entityPlayer.getCurrentEquippedItem())) {
                            this.denyEvent(event, false);
                            return;
                        }
                    } if (event.entityPlayer.getCurrentEquippedItem().hasTagCompound()) {
                        NBTTagCompound tag = (NBTTagCompound) event.entityPlayer.getCurrentEquippedItem().getTagCompound().copy();
                        if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                            //TODO: place a tile entity storing the seed's data
                            this.denyEvent(event, false);
                        }
                    }
                }
            }
        }
    }

    private static boolean allowVanillaPlanting(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return false;
        }
        if(ConfigurationHandler.disableVanillaFarming) {
            if(ignoresVanillaPlantingSetting(seed)) {
                return true;
            }
            if(CropPlantHandler.isValidSeed(seed)) {
                return false;
            }
            if(seed.getItem() == Items.potato) {
                return false;
            }
            if(seed.getItem() == Items.carrot) {
                return false;
            }
            if(seed.getItem() == Items.reeds) {
                return false;
            }
        }
        return true;
    }

    private static boolean ignoresVanillaPlantingSetting(ItemStack seed) {
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        return plant != null && plant.ingoresVanillaPlantingRule();
    }

    /** Event handler to create water pads */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void waterPadCreation(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.world.getBlock(event.x, event.y, event.z) != Blocks.farmland) {
                return;
            }
            boolean flag = false;
            if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemSpade) {
                flag = true;
            } else if (ModHelper.allowIntegration(Names.Mods.tconstruct) && TinkersConstructHelper.isShovel(event.entityPlayer.getCurrentEquippedItem())) {
                flag = true;
            }
            if (flag) {
                if (event.world.isRemote) {
                    denyEvent(event, true);
                }
                Block block = event.world.getBlock(event.x, event.y, event.z);
                event.world.setBlock(event.x, event.y, event.z, com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPad, 0, 3);
                if (!event.entityPlayer.capabilities.isCreativeMode) {
                    event.entityPlayer.getCurrentEquippedItem().damageItem(1, event.entityPlayer);
                    event.setResult(Event.Result.ALLOW);
                }
                event.world.playSoundEffect((double) ((float) event.x + 0.5F), (double) ((float) event.y + 0.5F), (double) ((float) event.z + 0.5F), block.stepSound.getStepResourcePath(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                denyEvent(event, false);
            }
        }
    }

    /** This is done with an event because else the player will place the vines as a block instead of applying them to the grate */
    @SubscribeEvent
    public void applyVinesToGrate(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
            if(stack==null || stack.getItem()==null || stack.getItem()!= Item.getItemFromBlock(Blocks.vine)) {
                return;
            }
            Block block = event.world.getBlock(event.x, event.y, event.z);
            if(!(block instanceof BlockGrate)) {
                return;
            }
            if(event.world.isRemote) {
                denyEvent(event, true);
            } else {
                block.onBlockActivated(event.world, event.x, event.y, event.z, event.entityPlayer, event.face, 0, 0, 0);
            }
        }
    }

    /** Event handler to deny bonemeal while sneaking on crops that are not allowed to be bonemealed */
    @SubscribeEvent
    public void denyBonemeal(PlayerInteractEvent event) {
        if(event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if(!event.entityPlayer.isSneaking()) {
            return;
        }
        ItemStack heldItem = event.entityPlayer.getHeldItem();
        if(heldItem!=null && heldItem.getItem()==net.minecraft.init.Items.dye && heldItem.getItemDamage()==15) {
            TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
            if(te!=null && (te instanceof TileEntityCrop)) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(!crop.canBonemeal()) {
                    this.denyEvent(event, false);
                }
            }
        }
    }

    private void denyEvent(PlayerInteractEvent event, boolean sendToServer) {
        //cancel event to prevent the Hunger Overhaul event handler from being called
        event.setResult(Event.Result.DENY);
        event.useItem = Event.Result.DENY;
        event.useBlock = Event.Result.DENY;
        if (sendToServer && event.world.isRemote) {
            //send the right click to the server manually (cancelling the event will prevent the client from telling the server a right click happened, and nothing will happen, but we still want stuff to happen)
            FMLClientHandler.instance().getClientPlayerEntity().sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(event.x, event.y, event.z, event.face, event.entityPlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
        }
        event.setCanceled(true);
    }
}
