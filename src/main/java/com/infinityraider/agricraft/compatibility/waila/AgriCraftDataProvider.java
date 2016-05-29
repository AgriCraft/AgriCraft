package com.infinityraider.agricraft.compatibility.waila;

import com.infinityraider.agricraft.blocks.BlockBase;
import com.infinityraider.agricraft.tileentity.TileEntityBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public class AgriCraftDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
    	if(dataAccessor.getBlock() instanceof BlockBase) {
    		BlockBase block = (BlockBase)dataAccessor.getBlock();
    		TileEntityBase tea = dataAccessor.getTileEntity() instanceof TileEntityBase ? (TileEntityBase)dataAccessor.getTileEntity() : null;
    		// Todo: fix.
			return null;
    	} else {
    		return null;
    	}
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        TileEntity te = dataAccessor.getTileEntity();
        if(te!=null && te instanceof TileEntityBase) {
            ((TileEntityBase)te).addWailaInformation(list);
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return currenttip;
    }

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP epmp, TileEntity te, NBTTagCompound tag, World world, BlockPos bp) {
		if (te != null) {
            te.writeToNBT(tag);
    	}
        return tag;
	}
}
