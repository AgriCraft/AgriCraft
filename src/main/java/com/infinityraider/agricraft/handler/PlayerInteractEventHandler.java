package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class PlayerInteractEventHandler {

    /**
     * Event handler to disable vanilla farming.
     * 
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void vanillaSeedPlanting(PlayerInteractEvent.RightClickBlock event) {
        // If not disabled, don't bother.
        if (!AgriCraftConfig.disableVanillaFarming) {
            return;
        }

        // Fetch the event itemstack.
        final ItemStack stack = event.getItemStack();

        // If the item in the player's hand is not a seed, who cares?
        if (!AgriApi.getSeedRegistry().hasAdapter(stack)) {
            return;
        }

        // Fetch world information.
        final BlockPos pos = event.getPos();
        final World world = event.getWorld();
        final BlockState state = world.getBlockState(pos);

        // Fetch the block at the location.
        final Block block = state.getBlock();

        // If clicking crop block, who cares?
        if (block instanceof IAgriCrop) {
            return;
        }

        // If the item is an instance of IPlantable we need to perfom an extra check.
        if (stack.getItem() instanceof IPlantable) {
            // If the clicked block cannot support the given plant, then who cares?
            if (!block.canSustainPlant(state, world, pos, Direction.UP, (IPlantable) stack.getItem())) {
                return;
            }
        }

        // If clicking crop tile, who cares?
        if (WorldHelper.getTile(event.getWorld(), event.getPos(), IAgriCrop.class).isPresent()) {
            return;
        }

        // The player is attempting to plant a seed, which is simply unacceptable.
        // We must deny this event.
        event.setUseItem(Event.Result.DENY);

        // If we are on the client side we are done.
        if (event.getSide().isClient()) {
            return;
        }

        // Should the server notify the player that vanilla farming has been disabled?
        if (AgriCraftConfig.showDisabledVanillaFarmingWarning) {
            MessageUtil.messagePlayer(event.getPlayer(), "`7Vanilla planting is disabled!`r");
        }
    }

        /*
     * This is done with an event because else the player will place the vines
     * as a block instead of applying them to the grate
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void applyVinesToGrate(PlayerInteractEvent.RightClickBlock event) {
        // Fetch the ItemStack
        final ItemStack stack = event.getItemStack();

        // If the player is not holding a stack of vines, who cares?
        if (stack.getItem() != Item.getItemFromBlock(Blocks.VINE)) {
            return;
        }

        // Fetch world information.
        final BlockPos pos = event.getPos();
        final World world = event.getWorld();
        final BlockState state = world.getBlockState(pos);

        // Fetch the block at the location.
        final Block block = state.getBlock();

        // If the player isn't clicking a grate, who cares?
        if (!(block instanceof BlockGrate)) {
            return;
        }
        // The player is trying to place vines! Scandalous!
        // We better deny the event!
        event.setUseItem(Event.Result.DENY);
    }

    /*
     * Event handler to deny bonemeal while sneaking on crops that are not
     * allowed to be bonemealed
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void denyBonemeal(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityLiving().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityLiving().getActiveItemStack();
        if (!heldItem.isEmpty() && heldItem.getItem() == Items.BONE_MEAL) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && (te instanceof TileEntityCrop)) {
                event.setUseItem(Event.Result.DENY);
            }
        }
    }

}
