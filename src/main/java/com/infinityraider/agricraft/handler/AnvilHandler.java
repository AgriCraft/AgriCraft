package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
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
        if(event.getLeft().getItem() == AgriApi.getAgriContent().getItems().getMagnifyingGlassItem()) {
            armor = event.getRight();
        } else if(event.getRight().getItem() == AgriApi.getAgriContent().getItems().getMagnifyingGlassItem()) {
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
                    output.resetHoverName();
                } else {
                    output.setHoverName(new TextComponent(inputName));
                }
                // set output and cost
                event.setOutput(output);
                event.setCost(1);
            }
        }
    }
}
