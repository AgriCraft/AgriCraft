package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.network.MessageCompareLight;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeDiffLight extends DebugMode {

    @Override
    public String debugName() {
        return "Diff. Server/Client Light Levels";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        if (context.getWorld().isRemote) {
            new MessageCompareLight(context.getWorld(), context.getPos()).sendToServer();
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        // NOP
    }

}
