package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerPeripheral;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPeripheral extends GuiContainer {
    public GuiPeripheral(InventoryPlayer inventory, TileEntityPeripheral peripheral) {
        super(new ContainerPeripheral(inventory, peripheral));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

    }
}
