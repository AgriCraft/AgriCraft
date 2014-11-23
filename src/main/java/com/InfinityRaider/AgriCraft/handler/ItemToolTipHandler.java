package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemToolTipHandler {
    @SubscribeEvent
    public void onToolTipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if(stack.getItem() instanceof ItemSeeds && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag.hasKey(Names.growth) && tag.hasKey(Names.gain) && tag.hasKey(Names.strength) && tag.hasKey(Names.analyzed)) {
                if(tag.getBoolean(Names.analyzed)) {
                    event.toolTip.add(EnumChatFormatting.GREEN + " - Growth: " + tag.getInteger(Names.growth));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - Gain: " + tag.getInteger(Names.gain));
                    event.toolTip.add(EnumChatFormatting.GREEN + " - Strength: " + tag.getInteger(Names.strength));
                }
                else {
                    event.toolTip.add(" Unidentified");
                }
            }
        }
    }
}
