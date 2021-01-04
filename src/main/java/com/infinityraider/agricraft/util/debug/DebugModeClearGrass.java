package com.infinityraider.agricraft.util.debug;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.Block;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugModeClearGrass extends DebugMode {

    @AgriConfigurable(
            category = AgriConfigCategory.DEBUG,
            key = "Grass Breaker Radius",
            min = "1", max = "50",
            comment = "The radius of the grass breaking tool."
    )
    private static int radius = 10;

    @Override
    public String debugName() {
        return "clear grass";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        BlockPos pos = context.getPos().toImmutable();
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                BlockPos loc = pos.add(x, 0, z);
                Block block = context.getWorld().getBlockState(loc).getBlock();
                if (block instanceof BushBlock) {
                    context.getWorld().destroyBlock(loc, false);
                }
            }
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
    
    static {
        AgriCore.getConfig().addConfigurable(DebugModeClearGrass.class);
    }

}
