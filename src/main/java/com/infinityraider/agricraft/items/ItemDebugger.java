package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.utility.DebugHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDebugger extends ItemBase {
	
	public ItemDebugger() {
		super("debugger", true);
		this.setMaxStackSize(1);
	}

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if(!player.isSneaking()) {
            DebugHelper.debug(player, world, pos);
        }
        else {
            if(!world.isRemote) {
                EntityVillager entityvillager = new EntityVillagerFarmer(world, WorldGen.getVillagerId());
                entityvillager.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 1, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntityInWorld(entityvillager);
            }
        }
        return EnumActionResult.PASS;
    }
	
}
