package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.utility.debug.DebugMode;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DebugModeDrainIrrigationComponent extends DebugMode {
    @Override
    public String debugName() {
        return "drain irrigation component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        if(context.getLevel().isClientSide()) {
            return;
        }
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        if(tile instanceof TileEntityIrrigationComponent) {
            TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
            component.drainWater(component.getCapacity(), true);
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {

    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

    }
}
