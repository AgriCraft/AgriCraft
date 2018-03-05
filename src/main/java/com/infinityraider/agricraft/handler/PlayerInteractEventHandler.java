package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.blocks.BlockGrate;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = "agricraft")
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

        // If the stack is null, or otherwise invalid, who cares?
        if (!StackHelper.isValid(stack)) {
            return;
        }

        // If the item in the player's hand is not a seed, who cares?
        if (!AgriApi.getSeedRegistry().hasAdapter(stack)) {
            return;
        }

        // Fetch world information.
        final BlockPos pos = event.getPos();
        final World world = event.getWorld();
        final IBlockState state = world.getBlockState(pos);

        // Fetch the block at the location.
        final Block block = state.getBlock();

        // If clicking crop block, who cares?
        if (block instanceof IAgriCrop) {
            return;
        }

        // If the item is an instance of IPlantable we need to perfom an extra check.
        if (stack.getItem() instanceof IPlantable) {
            // If the clicked block cannot support the given plant, then who cares?
            if (!block.canSustainPlant(state, world, pos, EnumFacing.UP, (IPlantable) stack.getItem())) {
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
            MessageUtil.messagePlayer(event.getEntityPlayer(), "`7Vanilla planting is disabled!`r");
        }
    }

    /*
     * Event handler to create water pads
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void waterPadCreation(PlayerInteractEvent.RightClickBlock event) {
        // Fetch held item.
        final ItemStack stack = event.getItemStack();

        // Check if holding shovel.
        if (!StackHelper.isValid(stack, ItemSpade.class)) {
            return;
        }

        // Fetch world information.
        final BlockPos pos = event.getPos();
        final World world = event.getWorld();
        final IBlockState state = world.getBlockState(pos);

        // Fetch the block at the location.
        final Block block = state.getBlock();

        // Test that clicked block was farmland.
        if (block != Blocks.FARMLAND) {
            return;
        }

        // Deny the event.
        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setResult(Event.Result.DENY);

        // If we are on the client side we are done.
        if (event.getSide().isClient()) {
            return;
        }

        // Fetch the player.
        final EntityPlayer player = event.getEntityPlayer();

        // Create the new block on the server side.
        world.setBlockState(pos, AgriBlocks.getInstance().WATER_PAD.getDefaultState(), 3);

        // Damage player's tool if not in creative.
        if (!player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
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

        // If the stack is null, or otherwise invalid, who cares?
        if (!StackHelper.isValid(stack)) {
            return;
        }

        // If the player is not holding a stack of vines, who cares?
        if (stack.getItem() != Item.getItemFromBlock(Blocks.VINE)) {
            return;
        }

        // Fetch world information.
        final BlockPos pos = event.getPos();
        final World world = event.getWorld();
        final IBlockState state = world.getBlockState(pos);

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
        if (!event.getEntityPlayer().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityPlayer().getActiveItemStack();
        if (!heldItem.isEmpty() && heldItem.getItem() == Items.DYE && heldItem.getItemDamage() == 15) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && (te instanceof TileEntityCrop)) {
                event.setUseItem(Event.Result.DENY);
            }
        }
    }

}
