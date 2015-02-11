package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.BlockChannelValve;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AgriCraftValveDataProvider implements IWailaDataProvider {
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        Block block = dataAccessor.getBlock();
        TileEntity te = dataAccessor.getTileEntity();
        if(block instanceof BlockChannelValve && te instanceof TileEntityCustomWood) {
            ItemStack stack = new ItemStack(Blocks.blockChannelValve, 1, 0);
            stack.setTagCompound(((TileEntityCustomWood) te).getMaterialTag());
            return stack;
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return list;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        list = new ArrayList<String>();
        Block block = dataAccessor.getBlock();
        TileEntity te = dataAccessor.getTileEntity();
        if(block!=null && block instanceof BlockChannelValve && te!=null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            //define material
            ItemStack materialStack =valve.getMaterial();
            String material = materialStack.getItem().getItemStackDisplayName(materialStack);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+material);
            //show status
            String status = StatCollector.translateToLocal(valve.isPowered()?"agricraft_tooltip.closed":"agricraft_tooltip.open");
            list.add(StatCollector.translateToLocal("agricraft_tooltip.state")+": "+status);
            //show contents
            int contents = dataAccessor.getNBTData().getInteger(Names.NBT.level);
            int capacity = 500;
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel")+": "+contents+"/"+capacity);
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        if (te != null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            tag.setInteger(Names.NBT.level, valve.getFluidLevel());
        }
        return tag;
    }
}
