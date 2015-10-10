package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.api.v2.IClipper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.items.ItemClipping;
import com.InfinityRaider.AgriCraft.items.ItemTrowel;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@SideOnly(Side.CLIENT)
public class ItemToolTipHandler {
    @SubscribeEvent
    public void addSeedStatsTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if(stack==null || stack.getItem()==null) {
            return;
        }
        if(stack.getItem() instanceof ItemClipping) {
            stack = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        }
        if(stack==null || stack.getItem()==null) {
            return;
        }
        if(CropPlantHandler.isValidSeed(stack) && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength) && tag.hasKey(Names.NBT.analyzed)) {
                if(tag.getBoolean(Names.NBT.analyzed)) {
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + tag.getInteger(Names.NBT.growth));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + tag.getInteger(Names.NBT.gain));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - "+StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + tag.getInteger(Names.NBT.strength));
                }
                else {
                    event.toolTip.add(" "+ StatCollector.translateToLocal("agricraft_tooltip.unidentified"));
                }
            }
        }
    }

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
