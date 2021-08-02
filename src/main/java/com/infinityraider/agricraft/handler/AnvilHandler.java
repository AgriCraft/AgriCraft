package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnvilHandler {
    private static final AnvilHandler INSTANCE = new AnvilHandler();

    public static AnvilHandler getInstance() {
        return INSTANCE;
    }

    private AnvilHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack armor = ItemStack.EMPTY;
        if(event.getLeft().getItem() == AgriCraft.instance.getModItemRegistry().magnifying_glass) {
            armor = event.getRight();
        } else if(event.getRight().getItem() == AgriCraft.instance.getModItemRegistry().magnifying_glass) {
            armor = event.getLeft();
        }
        if(CapabilityGeneInspector.getInstance().shouldApplyCapability(armor)) {
            if(CapabilityGeneInspector.getInstance().hasInspectionCapability(armor)) {
                return;
            }
            ItemStack output = armor.copy();
            if(CapabilityGeneInspector.getInstance().applyInspectionCapability(output)) {
                // Set the name
                String inputName = event.getName();
                if(inputName == null || inputName.isEmpty()) {
                    output.clearCustomName();
                } else {
                    output.setDisplayName(new StringTextComponent(inputName));
                }
                // set output and cost
                event.setOutput(output);
                event.setCost(1);
            }
        }
    }
}
