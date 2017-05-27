package com.infinityraider.agricraft.compat.waila;

import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AgriWailaAdapter implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        // NAH.
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        Block b = dataAccessor.getBlock();
        if (b instanceof IAgriDisplayable) {
            ((IAgriDisplayable) b).addDisplayInfo(list::add);
        }
        TileEntity te = dataAccessor.getTileEntity();
        if (te instanceof IAgriDisplayable) {
            ((IAgriDisplayable) te).addDisplayInfo(list::add);
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
