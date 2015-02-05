package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerSeedStorage extends ContainerAgricraft {
    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory,  6, 49);
        this.te = te;
    }
}
