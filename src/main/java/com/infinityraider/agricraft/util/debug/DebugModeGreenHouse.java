package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.handler.GreenHouseHandler;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugModeGreenHouse extends DebugMode {
    @Override
    public String debugName() {
        return "greenhouse";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {

    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {
        if(!world.isClientSide()) {
            GreenHouseHandler.getInstance().createGreenHouse(world, player.getOnPos().above());
        }
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

    }
}
