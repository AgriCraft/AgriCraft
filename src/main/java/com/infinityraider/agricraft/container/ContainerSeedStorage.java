package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSeedStorage extends ContainerSeedStorageBase<TileEntitySeedStorage> {

    private static final int invOffsetX = 6;
    private static final int invOffsetY = 49;

    public ContainerSeedStorage(TileEntitySeedStorage tile, InventoryPlayer inventory) {
        super(tile, inventory, invOffsetX, invOffsetY);
    }

    @Override
    public boolean addSeedToStorage(ItemStack seedStack) {
        return this.getTile().addStackToInventory(seedStack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return this.getTile().getInventory();
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(AgriSeed seed) {
        return this.getTile().getSeedSlots();
    }

}
