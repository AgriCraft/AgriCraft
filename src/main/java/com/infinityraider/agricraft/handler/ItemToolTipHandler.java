package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.api.v1.IClipper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.ItemClipping;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.statstringdisplayer.StatStringDisplayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ItemToolTipHandler {
    /** Adds tooltips for SEED stats */
    @SubscribeEvent
    public void addSeedStatsTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if(stack==null || stack.getItem()==null) {
            return;
        }
        if(stack.getItem() instanceof ItemClipping) {
            if(!stack.hasTagCompound()) {
                return;
            }
            stack = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        }
        if(stack==null || stack.getItem()==null) {
            return;
        }
        if(CropPlantHandler.isValidSeed(stack) && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag.hasKey(AgriCraftNBT.GROWTH) && tag.hasKey(AgriCraftNBT.GAIN) && tag.hasKey(AgriCraftNBT.STRENGTH) && tag.hasKey(AgriCraftNBT.ANALYZED)) {
                if(tag.getBoolean(AgriCraftNBT.ANALYZED)) {
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(AgriCraftNBT.GROWTH), AgriCraftConfig.cropStatCap));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(AgriCraftNBT.GAIN), AgriCraftConfig.cropStatCap));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(AgriCraftNBT.STRENGTH), AgriCraftConfig.cropStatCap));
                }
                else {
                    event.toolTip.add(" "+ StatCollector.translateToLocal("agricraft_tooltip.unidentified"));
                }
            }
        }
    }

    /** Adds tooltips to items that are trowels (implementing ITrowel) */
    @SubscribeEvent
    public void addTrowelTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if(stack==null || stack.getItem()==null || !(stack.getItem() instanceof ITrowel)) {
            return;
        }
        ITrowel trowel = (ITrowel) stack.getItem();
        if(stack.getItemDamage()==0) {
            event.toolTip.add(StatCollector.translateToLocal("agricraft_tooltip.trowel"));
        }
        else if(trowel.hasSeed(stack)) {
            ItemStack seed = trowel.getSeed(stack);
            event.toolTip.add(StatCollector.translateToLocal("agricraft_tooltip.seed") + ": " + seed.getItem().getItemStackDisplayName(seed));
        }
    }

    /** Adds tooltips to items that are clippers (implementing IClipper) */
    @SubscribeEvent
    public void addClipperTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof IClipper)) {
            return;
        }
        event.toolTip.add(StatCollector.translateToLocal("agricraft_tooltip.clipper1"));
        event.toolTip.add(StatCollector.translateToLocal("agricraft_tooltip.clipper2"));
        event.toolTip.add(StatCollector.translateToLocal("agricraft_tooltip.clipper3"));
    }
}
