package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.handler.GreenHouseHandler;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeGreenHouse extends DebugMode {
    @Override
    public String debugName() {
        return "greenhouse";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {

    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        if(!world.isRemote()) {
            GreenHouseHandler.getInstance().checkAndFormGreenHouse(world, player.getPosition().up());
        }
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {

    }
}
