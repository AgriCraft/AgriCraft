package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeDrainIrrigationComponent extends DebugMode {
    @Override
    public String debugName() {
        return "drain irrigation component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        if(context.getWorld().isRemote()) {
            return;
        }
        TileEntity tile = context.getWorld().getTileEntity(context.getPos());
        if(tile instanceof TileEntityIrrigationComponent) {
            TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
            component.drainWater(component.getCapacity(), true);
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {

    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {

    }
}
