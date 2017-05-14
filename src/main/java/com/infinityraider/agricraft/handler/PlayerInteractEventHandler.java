package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.blocks.BlockGrate;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.mojang.realmsclient.gui.ChatFormatting;

@SuppressWarnings("unused")
public class PlayerInteractEventHandler {

    /**
     * Event handler to disable vanilla farming
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedPlanting(PlayerInteractEvent.RightClickBlock event) {
        // If not disabled, don't bother.
        if (!AgriCraftConfig.disableVanillaFarming) {
            return;
        }

        // If clicking crop block, who cares?
        if (WorldHelper.getBlock(event.getWorld(), event.getPos(), IAgriCrop.class).isPresent()) {
            return;
        }
        
        // If clicking crop tile, who cares?
        if (WorldHelper.getTile(event.getWorld(), event.getPos(), IAgriCrop.class).isPresent()) {
            return;
        }

        // Test if seed that should be blocked.
        if (SeedRegistry.getInstance().hasAdapter(event.getItemStack())) {
            this.denyEvent(event, true);
            if (AgriCraftConfig.showDisabledVanillaFarmingWarning && event.getSide().isServer()) {
                MessageUtil.messagePlayer(event.getEntityPlayer(), ChatFormatting.GRAY + "Vanilla planting is disabled!");
            }
        }
    }

    /**
     * Event handler to create water pads
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void waterPadCreation(PlayerInteractEvent.RightClickBlock event) {
        // Fetch Information.
        final EntityPlayer player = event.getEntityPlayer();
        final ItemStack stack = event.getItemStack();

        // Check if holding shovel.
        if (!StackHelper.isValid(stack, ItemSpade.class)) {
            return;
        }

        // Fetch BlockState
        final IBlockState state = event.getWorld().getBlockState(event.getPos());

        // Test that clicked block was farmland.
        if (state.getBlock() != Blocks.FARMLAND) {
            return;
        }

        // If we care about the event, but it is remote, simply deny it.
        if (event.getWorld().isRemote) {
            denyEvent(event, true);
        }

        // Create the new block.
        event.getWorld().setBlockState(event.getPos(), AgriBlocks.getInstance().WATER_PAD.getDefaultState(), 3);

        // Damage player's tool if not in creative.
        if (!player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
        }

        // Prevent other things from happening.
        denyEvent(event, false);
    }

    /**
     * This is done with an event because else the player will place the vines
     * as a block instead of applying them to the grate
     */
    @SubscribeEvent
    public void applyVinesToGrate(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getEntityPlayer().getActiveItemStack();
        if (stack == null || stack.getItem() == null || stack.getItem() != Item.getItemFromBlock(Blocks.VINE)) {
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

    /**
     * Event handler to deny bonemeal while sneaking on crops that are not
     * allowed to be bonemealed
     */
    @SubscribeEvent
    public void denyBonemeal(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityPlayer().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityPlayer().getActiveItemStack();
        if (heldItem != null && heldItem.getItem() == Items.DYE && heldItem.getItemDamage() == 15) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && (te instanceof TileEntityCrop)) {
                TileEntityCrop crop = (TileEntityCrop) te;
                this.denyEvent(event, false);
            }
        }
    }

    private void denyEvent(PlayerInteractEvent.RightClickBlock event, boolean sendToServer) {
        //cancel event to prevent the Hunger Overhaul event handler from being called
        event.setResult(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setUseBlock(Event.Result.DENY);
        if (sendToServer && event.getWorld().isRemote) {
            // TODO!!!
            //send the right click to the server manually (cancelling the event will prevent the client from telling the server a right click happened, and nothing will happen, but we still want stuff to happen)
            //FMLClientHandler.instance().getClientPlayerEntity().sendQueue.addToSendQueue(new CPacketPlayerBlockPlacement());
        }
        event.setCanceled(true);
    }
}
