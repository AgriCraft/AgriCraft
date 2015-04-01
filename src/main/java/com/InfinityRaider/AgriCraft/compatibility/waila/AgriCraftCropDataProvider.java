package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
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

import java.util.List;

public class AgriCraftCropDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        Block block = dataAccessor.getBlock();
        if(block instanceof BlockCrop) {
            return new ItemStack(Items.crops, 1, 0);
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return list;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        Block block = dataAccessor.getBlock();
        TileEntity te = dataAccessor.getTileEntity();
        if(block!=null && block instanceof BlockCrop && te!=null && te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if(crop.hasPlant()) {
                int growth = crop.getGrowth();
                int gain = crop.getGain();
                int strength = crop.getStrength();
                boolean analyzed = crop.isAnalyzed();
                String seedName = crop.getSeedStack().getDisplayName();
                list.add(StatCollector.translateToLocal("agricraft_tooltip.seed") + ": " + seedName);
                if(analyzed) {
                    list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + growth);
                    list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + gain);
                    list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + strength);
                }
                else {
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.analyzed"));
                }
                list.add(StatCollector.translateToLocal(crop.isFertile()?"agricraft_tooltip.fertile":"agricraft_tooltip.notFertile"));
            }
            else if(crop.hasWeed()) {
                list.add(StatCollector.translateToLocal("agricraft_tooltip.weeds"));
            }
            else {
                list.clear();
                list.add(StatCollector.translateToLocal("agricraft_tooltip.empty"));
            }
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor dataAccessor, IWailaConfigHandler configHandler) {
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        return null;
    }
}
