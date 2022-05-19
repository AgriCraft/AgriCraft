package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DebugModeCheckIrrigationComponent extends DebugMode {
    @Override
    public String debugName() {
        return "check irrigation component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        MessageUtil.messagePlayer(context.getPlayer(), "{0} Irrigation Component Info:", AgriCraft.instance.proxy().getLogicalSide());
        BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        if(!(tile instanceof TileEntityIrrigationComponent)) {
            MessageUtil.messagePlayer(context.getPlayer(), "This is not an irrigation component");
        } else {
            TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
            MessageUtil.messagePlayer(context.getPlayer(), " - Contents: {0}/{1}", component.getContent(), component.getCapacity());
            MessageUtil.messagePlayer(context.getPlayer(), " - Level: {0}, [{1}; {2}]", component.getLevel(), component.getMinLevel(), component.getMaxLevel());
            MessageUtil.messagePlayer(context.getPlayer(), " - Neighbours:");
            MessageUtil.messagePlayer(context.getPlayer(), "   - NORTH: {0}", component.describeNeighbour(Direction.NORTH));
            MessageUtil.messagePlayer(context.getPlayer(), "   - EAST: {0}", component.describeNeighbour(Direction.EAST));
            MessageUtil.messagePlayer(context.getPlayer(), "   - SOUTH: {0}", component.describeNeighbour(Direction.SOUTH));
            MessageUtil.messagePlayer(context.getPlayer(), "   - WEST: {0}", component.describeNeighbour(Direction.WEST));
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {

    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

    }
}
