package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.blocks.BlockGrate;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class PlayerInteractEventHandler {
    /** Event handler to disable vanilla farming */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedPlanting(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getActiveItemStack();
        if (stack != null && stack.stackSize > 0 && stack.getItem() != null && stack.getItem() instanceof IPlantable) {
            if (GrowthRequirementHandler.isSoilValid(event.getWorld(), event.getPos())) {
                if (AgriCraftConfig.disableVanillaFarming) {
                    if (!allowVanillaPlanting(stack)) {
                        this.denyEvent(event, false);
                        return;
                    }
                }
                if (stack.hasTagCompound()) {
                    NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().copy();
                    if (tag.hasKey(AgriCraftNBT.GROWTH) && tag.hasKey(AgriCraftNBT.GAIN) && tag.hasKey(AgriCraftNBT.STRENGTH)) {
                        //TODO: place a tile entity storing the SEED's data
                        this.denyEvent(event, false);
                    }
                }
            }
        }
    }

    private static boolean allowVanillaPlanting(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return false;
        }
        if(AgriCraftConfig.disableVanillaFarming) {
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
		// TODO: REPLACE
        ICropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        return true;
    }

    /** Event handler to create water pads */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void waterPadCreation(PlayerInteractEvent.RightClickBlock event) {
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        Block block = state.getBlock();
        if (block != Blocks.farmland) {
            return;
        }
        boolean flag = false;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getActiveItemStack();
        if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemSpade) {
            flag = true;
        }
            /*
            else if (ModHelper.allowIntegration(Names.Mods.tconstruct) && TinkersConstructHelper.isShovel(event.entityPlayer.getCurrentEquippedItem())) {
                FLAG = true;
            }
            */
        if (flag) {
            if (event.getWorld().isRemote) {
                denyEvent(event, true);
            }
            event.getWorld().setBlockState(event.getPos(), com.infinityraider.agricraft.init.AgriCraftBlocks.blockWaterPad.getDefaultState(), 3);
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
                event.setResult(Event.Result.ALLOW);
            }
            SoundType sound = block.getStepSound();
            event.getWorld().playSound(null, (double) ((float) event.getPos().getX() + 0.5F), (double) ((float) event.getPos().getY() + 0.5F), (double) ((float) event.getPos().getZ() + 0.5F), sound.getBreakSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            denyEvent(event, false);
        }
    }

    /** This is done with an event because else the player will place the vines as a block instead of applying them to the grate */
    @SubscribeEvent
    public void applyVinesToGrate(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getEntityPlayer().getActiveItemStack();
        if (stack == null || stack.getItem() == null || stack.getItem() != Item.getItemFromBlock(Blocks.vine)) {
            return;
        }
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (!(block instanceof BlockGrate)) {
            return;
        }
        if (event.getWorld().isRemote) {
            denyEvent(event, true);
        } else {
            block.onBlockActivated(event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), event.getEntityPlayer(), EnumHand.MAIN_HAND, stack, event.getFace(), 0, 0, 0);
        }
    }

    /** Event handler to deny bonemeal while sneaking on crops that are not allowed to be bone mealed */
    @SubscribeEvent
    public void denyBonemeal(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getEntityPlayer().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityPlayer().getActiveItemStack();
        if(heldItem!=null && heldItem.getItem()==net.minecraft.init.Items.dye && heldItem.getItemDamage()==15) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if(te!=null && (te instanceof TileEntityCrop)) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(!crop.canBonemeal()) {
                    this.denyEvent(event, false);
                }
            }
        }
    }

    private void denyEvent(PlayerInteractEvent.RightClickBlock event, boolean sendToServer) {
        //cancel event to prevent the Hunger Overhaul event handler from being called
        event.setResult(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setUseBlock(Event.Result.DENY);
        if (sendToServer && event.getWorld().isRemote) {
            //send the right click to the server manually (cancelling the event will prevent the client from telling the server a right click happened, and nothing will happen, but we still want stuff to happen)
            FMLClientHandler.instance().getClientPlayerEntity().sendQueue.addToSendQueue(new CPacketPlayerBlockPlacement());
        }
        event.setCanceled(true);
    }
}
