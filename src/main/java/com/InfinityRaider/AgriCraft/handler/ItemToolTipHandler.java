package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.api.v2.IClipper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.items.ItemClipping;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.statstringdisplayer.StatStringDisplayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ItemToolTipHandler {
    /** Adds tooltips for seed stats */
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
            if(tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength) && tag.hasKey(Names.NBT.analyzed)) {
                if(tag.getBoolean(Names.NBT.analyzed)) {
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(Names.NBT.growth), ConfigurationHandler.cropStatCap));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(Names.NBT.gain), ConfigurationHandler.cropStatCap));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + StatStringDisplayer.instance().getStatDisplayString(tag.getInteger(Names.NBT.strength), ConfigurationHandler.cropStatCap));
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
