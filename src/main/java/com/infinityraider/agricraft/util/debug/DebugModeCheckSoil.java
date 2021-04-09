package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeCheckSoil extends DebugMode {

    @Override
    public String debugName() {
        return "check soil";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        String type = AgriApi.getSoilRegistry().stream()
                .filter(s -> s.isVariant(context.getWorld().getBlockState(context.getPos())))
                .map(IAgriSoil::getId)
                .findFirst()
                .orElse("Unknown Soil");
        MessageUtil.messagePlayer(context.getPlayer(), "{0} Soil Info:", AgriCraft.instance.proxy().getLogicalSide());
        MessageUtil.messagePlayer(context.getPlayer(), " - Soil Type: \"{0}\"", type);
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
