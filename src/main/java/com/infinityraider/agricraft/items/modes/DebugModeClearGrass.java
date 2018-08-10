/*
 */
package com.infinityraider.agricraft.items.modes;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 *
 */
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
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        pos = pos.toImmutable();
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                BlockPos loc = pos.add(x, 0, z);
                Block block = world.getBlockState(loc).getBlock();
                if (block instanceof BlockBush) {
                    world.destroyBlock(loc, false);
                }
            }
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        // NOP
    }
    
    static {
        AgriCore.getConfig().addConfigurable(DebugModeClearGrass.class);
    }

}
