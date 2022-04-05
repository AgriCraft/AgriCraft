package com.infinityraider.agricraft.util.debug;

import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DebugModeBonemeal extends DebugMode {

    // Saved copies of the texts that do not change. Unicode 00A7 is the formatting code squiggle. 'r' is reset.
    private static final String chatTrue =  "`2True  `r";                               // '2' is dark green.
    private static final String chatFalse = "`4False `r";                               // '4' is dark red.
    private static final String chatNotIG = "`8----  ----  (Block is not IGrowable)`r"; // '8' is dark gray.
    private static final String chatInfo = "x, y, z | canGrow | canUseBonemeal | BlockName\n"
            + "Note: for testing purposes, all three methods (including grow)\n"
            + "get called by this mode. It is not mimicking bonemeal's logic.";

    @Override
    public String debugName() {
        return "test IGrowable blocks";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return;
        }
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Level world = context.getLevel();
        // Start with the position of the block that was clicked on. '7' is gray. Btw, the chat font is not fixed width. :(
        StringBuilder outputRaw = new StringBuilder(String.format("`7%1$4d,%2$4d,%3$4d`r ", pos.getX(), pos.getY(), pos.getZ()));

        // Check if the clicked on block has the IGrowable interface.
        final BonemealableBlock crop = WorldHelper.getBlock(world, pos, BonemealableBlock.class).orElse(null);
        if (crop == null) {
            // If it does not, add a nicely formatted report, then skip onward.
            outputRaw.append(chatNotIG);
        } else if(world instanceof ServerLevel) {
            // Otherwise run the tests and record the results.
            BlockState state = world.getBlockState(pos);
            outputRaw.append(crop.isValidBonemealTarget(world, pos, state, false) ? chatTrue : chatFalse); // canGrow
            outputRaw.append(crop.isBonemealSuccess(world, world.getRandom(), pos, state) ? chatTrue : chatFalse); // canUseBonemeal
            crop.performBonemeal((ServerLevel) world, world.getRandom(), pos, state);                                                    // grow

            // It's also helpful to also make clear what block was being tested.
            outputRaw.append("`3"); // '3' is dark aqua.
            outputRaw.append(crop.toString().replaceFirst("Block", ""));
            outputRaw.append("`r");
        }

        // Ellipsis are added as a clue that there's more text.
        outputRaw.append(" `8[...]`r"); // '8' is dark gray.

        // Create a hover box with explanatory information.
        TextComponent hoverComponent = new TextComponent(MessageUtil.colorize(chatInfo));
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent);

        // Turn the output String into a chat message.
        TextComponent outputComponent = new TextComponent(MessageUtil.colorize(outputRaw.toString()));

        // Add the hover box to the chat message.
        outputComponent.getStyle().withHoverEvent(hoverEvent);

        // Now send the completed chat message.
        player.sendMessage(outputComponent, Util.NIL_UUID);
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {
        // NOPE
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // NOPE
    }
}
