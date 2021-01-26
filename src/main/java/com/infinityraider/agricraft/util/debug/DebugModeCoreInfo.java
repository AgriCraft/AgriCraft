package com.infinityraider.agricraft.util.debug;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeCoreInfo extends DebugMode {

    @Override
    public String debugName() {
        return "core info";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        MessageUtil.messagePlayer(context.getPlayer(), "{0} Info:", AgriCraft.instance.proxy().getLogicalSide());
        MessageUtil.messagePlayer(context.getPlayer(), "========================================");
        MessageUtil.messagePlayer(context.getPlayer(), "AgriPlants Hash: {0}", AgriCore.getPlants().hashCode());
        MessageUtil.messagePlayer(context.getPlayer(), " - Plant Count: {0}", AgriCore.getPlants().getAllElements().size());
        MessageUtil.messagePlayer(context.getPlayer(), "AgriWeeds Hash: {0}", AgriCore.getWeeds().hashCode());
        MessageUtil.messagePlayer(context.getPlayer(), " - Weed Count: {0}", AgriCore.getWeeds().getAllElements().size());
        MessageUtil.messagePlayer(context.getPlayer(), "AgriMutations Hash: {0}", AgriCore.getMutations().hashCode());
        MessageUtil.messagePlayer(context.getPlayer(), " - Mutation Count: {0}", AgriCore.getMutations().getAll().size());
        MessageUtil.messagePlayer(context.getPlayer(), "AgriSoils Hash: {0}", AgriCore.getSoils().hashCode());
        MessageUtil.messagePlayer(context.getPlayer(), " - Soil Count: {0}", AgriCore.getSoils().getAll().size());
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
