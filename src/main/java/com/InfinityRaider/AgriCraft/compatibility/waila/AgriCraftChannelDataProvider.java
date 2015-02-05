package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

public class AgriCraftChannelDataProvider implements IWailaDataProvider {
        @Override
        public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
            Block block = dataAccessor.getBlock();
            TileEntity te = dataAccessor.getTileEntity();
            if(block instanceof BlockWaterChannel && te instanceof TileEntityCustomWood) {
                ItemStack stack = new ItemStack(Blocks.blockWaterChannel, 1, 0);
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
            if(block!=null && block instanceof BlockWaterChannel && te!=null && te instanceof TileEntityChannel) {
                TileEntityChannel channel = (TileEntityChannel) te;
                //define material
                ItemStack materialStack =channel.getMaterial();
                String material = materialStack.getItem().getItemStackDisplayName(materialStack);
                list.add(StatCollector.translateToLocal("agricraft_tooltip.material")+": "+material);
                //show contents
                int contents = channel.getFluidLevel();
                int capacity = 500;
                list.add(StatCollector.translateToLocal("agricraft_tooltip.waterLevel")+": "+contents+"/"+capacity);
            }
            return list;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
            return list;
        }
}
