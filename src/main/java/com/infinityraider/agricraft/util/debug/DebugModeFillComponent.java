package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.api.v1.misc.IAgriFluidComponent;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DebugModeFillComponent extends DebugMode {

    @Override
    public String debugName() {
        return "fill component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        // Get the tile
        final IAgriFluidComponent component = WorldHelper.getTile(context.getWorld(), context.getPos(), IAgriFluidComponent.class).orElse(null);

        // If not null...
        if (component != null) {
            // Push a ridiculous amount of fluid at max height.
            component.acceptFluid(1_000_000, 100_000, true, false);
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
