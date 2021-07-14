package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.BlockCropPlant;
import com.infinityraider.agricraft.content.core.TileEntityCropPlant;
import com.infinityraider.agricraft.reference.AgriToolTips;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class AgriWailaCropBlockInfoProvider implements IComponentProvider {
    private static final AgriWailaCropBlockInfoProvider INSTANCE = new AgriWailaCropBlockInfoProvider();

    public static AgriWailaCropBlockInfoProvider getInstance() {
        return INSTANCE;
    }

    private AgriWailaCropBlockInfoProvider() {}

    @Override
    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
        if(accessor.getBlock() instanceof BlockCropPlant) {
            TileEntity tile = accessor.getTileEntity();
            if(tile instanceof TileEntityCropPlant) {
                TileEntityCropPlant crop = (TileEntityCropPlant) tile;
                IAgriPlant plant = crop.getPlant();
                if(plant.isPlant()) {
                    return plant.toItemStack();
                }
            }
        }
        return ItemStack.EMPTY;
    }



    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if(accessor.getBlock() instanceof BlockCropPlant) {
            tooltip.clear();
            tooltip.add(this.getStack(accessor, config).getDisplayName());
        }
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        AgriApi.getCrop(accessor.getWorld(), accessor.getPosition()).ifPresent(crop -> {
            PlayerEntity player = accessor.getPlayer();
            // Add data including full genome if in creative mode
            if(player.getHeldItemMainhand().getItem() == AgriCraft.instance.getModItemRegistry().debugger) {
                crop.addDisplayInfo(tooltip::add);
                tooltip.add(AgriToolTips.GENOME);
                crop.getGenome().map(genome -> {
                    genome.addDisplayInfo(tooltip::add);
                    return true;
                }).orElseGet(() ->{
                    tooltip.add(AgriToolTips.UNKNOWN);
                    return false;
                });
            } else {
                // add crop data
                if(this.shouldAddInfo(player)) {
                    crop.addDisplayInfo(tooltip::add);
                }
            }
        });
        AgriApi.getSoil(accessor.getWorld(), accessor.getPosition()).ifPresent(soil ->
                soil.addDisplayInfo(tooltip::add));
    }

    protected boolean shouldAddInfo(PlayerEntity player) {
        if (AgriCraft.instance.getConfig().doesMagnifyingGlassControlTOP()) {
            return AgriCraft.instance.proxy().isMagnifyingGlassObserving(player);
        }
        return true;
    }
}
