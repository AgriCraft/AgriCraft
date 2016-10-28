package com.infinityraider.agricraft.container;

import net.minecraft.entity.player.InventoryPlayer;

import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;

public class ContainerPeripheral extends ContainerSeedAnalyzer {

    private static final int xOffset = 5;
    private static final int yOffset = 94;

    public ContainerPeripheral(InventoryPlayer inventory, TileEntityPeripheral peripheral) {
        super(inventory, peripheral, xOffset, yOffset);
    }

    @Override
    protected void addSlots() {
        //add seed slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerSeed(seedAnalyzer, seedSlotId, 77, 40));
        //add journal slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerJournal(seedAnalyzer, journalSlotId, 149, 68));
    }

}
