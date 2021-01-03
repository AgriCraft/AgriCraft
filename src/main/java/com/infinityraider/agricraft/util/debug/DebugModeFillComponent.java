/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.agricraft.api.v1.misc.IAgriFluidComponent;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author Ryan
 */
public class DebugModeFillComponent extends DebugMode {

    @Override
    public String debugName() {
        return "fill component";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Get the tile
        final IAgriFluidComponent component = WorldHelper.getTile(world, pos, IAgriFluidComponent.class).orElse(null);
        
        // If not null...
        if (component != null) {
            // Push a ridiculous amount of fluid at max height.
            component.acceptFluid(1_000_000, 100_000, true, false);
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
    
}
