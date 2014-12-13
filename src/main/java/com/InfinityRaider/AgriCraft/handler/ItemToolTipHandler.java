package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemToolTipHandler {
    @SubscribeEvent
    public void onToolTipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if(stack.getItem() instanceof ItemSeeds && stack.hasTagCompound()) {
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
}
