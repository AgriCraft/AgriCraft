/*
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.init.WorldGen;
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
 * 
 */
public class DebugModeSpawnFarmer extends DebugMode {

	@Override
	public String debugName() {
		return "spawn farmer";
	}

	@Override
	public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			EntityVillagerFarmer entityvillager = new EntityVillagerFarmer(world, WorldGen.getVillagerId());
			entityvillager.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 1, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
			world.spawnEntityInWorld(entityvillager);
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
