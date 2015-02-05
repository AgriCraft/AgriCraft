package com.InfinityRaider.AgriCraft.gui;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSeedStorage extends GuiContainer {
    public GuiSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(new ContainerSeedStorage(inventory, te));
        this.xSize = 250;
        this.ySize = 176;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

    }
}
