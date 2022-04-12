package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.content.core.BlockCropPlant;
import com.infinityraider.agricraft.reference.AgriToolTips;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AgriWailaCropBlockInfoProvider extends AgriWailaBlockInfoProviderAbstract {
    protected AgriWailaCropBlockInfoProvider(IElementHelper helper) {
        super(helper);
    }

    public ItemStack getStack(BlockAccessor accessor) {
        if(accessor.getBlock() instanceof BlockCropPlant) {
            BlockEntity tile = accessor.getBlockEntity();
            if(tile instanceof IAgriCrop) {
                IAgriCrop crop = (IAgriCrop) tile;
                IAgriPlant plant = crop.getPlant();
                if(plant.isPlant()) {
                    return plant.toItemStack();
                }
            } else {
                return tile.getCapability(CropCapability.getCapability())
                        .map(crop -> crop.getPlant())
                        .map(plant -> plant.isPlant() ? plant.toItemStack() : ItemStack.EMPTY)
                        .orElse(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // Head
        if(accessor.getBlock() instanceof BlockCropPlant) {
            tooltip.clear();
            tooltip.add(this.getStack(accessor).getDisplayName());
        }
        // Body
        AgriApi.getCrop(accessor.getLevel(), accessor.getPosition()).ifPresent(crop -> {
            Player player = accessor.getPlayer();
            // Add data including full genome if in creative mode
            if(player.getMainHandItem().getItem() == AgriItemRegistry.debugger) {
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
        AgriApi.getSoil(accessor.getLevel(), accessor.getPosition()).ifPresent(soil ->
                soil.addDisplayInfo(tooltip::add));
    }

    protected boolean shouldAddInfo(Player player) {
        if (AgriCraft.instance.getConfig().doesMagnifyingGlassControlTOP()) {
            return AgriCraft.instance.proxy().isMagnifyingGlassObserving(player);
        }
        return true;
    }
}
