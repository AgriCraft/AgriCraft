/*
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

/**
 *
 *
 */
public class DebugModeIGrowable extends DebugMode {

    @Override
    public String debugName() {
        return "test IGrowable blocks";
    }

    // Saved copies of the texts that do not change. Unicode 00A7 is the formatting code squiggle. 'r' is reset.
    private static final String chatTrue =  "`2True  `r";                               // '2' is dark green.
    private static final String chatFalse = "`4False `r";                               // '4' is dark red.
    private static final String chatNotIG = "`8----  ----  (Block is not IGrowable)`r"; // '8' is dark gray.
    private static final String chatInfo = "x, y, z | canGrow | canUseBonemeal | BlockName\n"
            + "Note: for testing purposes, all three methods (including grow)\n"
            + "get called by this mode. It is not mimicking bonemeal's logic.";

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return;
        }

        // Start with the position of the block that was clicked on. '7' is gray. Btw, the chat font is not fixed width. :(
        StringBuilder outputRaw = new StringBuilder(String.format("`7%1$4d,%2$4d,%3$4d`r ", pos.getX(), pos.getY(), pos.getZ()));

        // Check if the clicked on block has the IGrowable interface.
        final IGrowable crop = WorldHelper.getBlock(world, pos, IGrowable.class).orElse(null);
        if (crop == null) {
            // If it does not, add a nicely formatted report, then skip onward.
            outputRaw.append(chatNotIG);
        } else {
            // Otherwise run the tests and record the results.
            IBlockState state = world.getBlockState(pos);
            outputRaw.append(crop.canGrow(world, pos, state, false) ? chatTrue : chatFalse); // canGrow
            outputRaw.append(crop.canUseBonemeal(world, world.rand, pos, state) ? chatTrue : chatFalse); // canUseBonemeal
            crop.grow(world, world.rand, pos, state);                                                    // grow

            // It's also helpful to also make clear what block was being tested.
            outputRaw.append("`3"); // '3' is dark aqua.
            outputRaw.append(crop.toString().replaceFirst("Block", ""));
            outputRaw.append("`r");
        }

        // Ellipsis are added as a clue that there's more text.
        outputRaw.append(" `8[...]`r"); // '8' is dark gray.

        // Create a hover box with explanatory information.
        TextComponentString hoverComponent = new TextComponentString(MessageUtil.colorize(chatInfo));
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);

        // Turn the output String into a chat message.
        TextComponentString outputComponent = new TextComponentString(MessageUtil.colorize(outputRaw.toString()));

        // Add the hover box to the chat message.
        outputComponent.getStyle().setHoverEvent(hoverEvent);

        // Now send the completed chat message.
        player.sendMessage(outputComponent);
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        // NOP
    }

}
