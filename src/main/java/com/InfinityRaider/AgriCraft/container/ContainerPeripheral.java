package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPeripheral extends ContainerSeedAnalyzer {
    private static final int xOffset = 7;
    private static final int yOffset = 80;

    public ContainerPeripheral(InventoryPlayer inventory, TileEntityPeripheral peripheral) {
        super(inventory, peripheral, xOffset, yOffset);
    }
}
