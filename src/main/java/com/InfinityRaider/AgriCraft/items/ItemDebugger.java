package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.entity.EntityVillagerFarmer;
import com.InfinityRaider.AgriCraft.init.WorldGen;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.utility.DebugHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDebugger extends ItemBase {
	
	public ItemDebugger() {
		super("debugger");
		this.setMaxStackSize(1);
	}

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
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
        return false;
    }
	
}
