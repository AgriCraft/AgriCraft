package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeCheckIrrigationComponent extends DebugMode {
    @Override
    public String debugName() {
        return "check irrigation component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        MessageUtil.messagePlayer(context.getPlayer(), "{0} Irrigation Component Info:", AgriCraft.instance.proxy().getLogicalSide());
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
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
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {

    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {

    }
}
