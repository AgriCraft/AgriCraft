package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.network.MessageCompareLight;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugModeDiffLight extends DebugMode {

    @Override
    public String debugName() {
        return "Diff. Server/Client Light Levels";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            new MessageCompareLight(context.getLevel(), context.getClickedPos()).sendToServer();
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // NOP
    }

}
