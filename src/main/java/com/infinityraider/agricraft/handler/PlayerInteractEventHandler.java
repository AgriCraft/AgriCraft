package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class PlayerInteractEventHandler {
    private static final PlayerInteractEventHandler INSTANCE = new PlayerInteractEventHandler();

    public static PlayerInteractEventHandler getInstance() {
        return INSTANCE;
    }

    private PlayerInteractEventHandler() {}

    /**
     * Event handler to disable vanilla farming.
     * 
     * @param event
     */
    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void vanillaSeedPlanting(PlayerInteractEvent.RightClickBlock event) {
        // If not disabled, don't bother.
        if (!AgriCraft.instance.getConfig().disableVanillaFarming()) {
            return;
        }

        // Fetch the event item stack.
        final ItemStack stack = event.getItemStack();

        // If the item in the player's hand is not a seed, who cares?
        if (!AgriApi.getSeedAdapterizer().hasAdapter(stack)) {
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

        /*
        //TODO: send a message to the client here instead
        // Should the server notify the player that vanilla farming has been disabled?
        if (AgriCraftConfig.showDisabledVanillaFarmingWarning) {
            MessageUtil.messagePlayer(event.getPlayer(), "`7Vanilla planting is disabled!`r");
        }
         */
    }

    /*
     * Event handler to deny bonemeal while sneaking on crops that are not
     * allowed to be bonemealed
     */
    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void denyBonemeal(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityLiving().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityLiving().getActiveItemStack();
        if (!heldItem.isEmpty() && heldItem.getItem() == Items.BONE_MEAL) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && (te instanceof TileEntityCropSticks)) {
                event.setUseItem(Event.Result.DENY);
            }
        }
    }

}
