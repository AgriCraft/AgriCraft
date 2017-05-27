package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.seed.AgriSeed;
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
        return this.tile.addStackToInventory(seedStack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return this.tile.getInventory();
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(AgriSeed seed) {
        return this.tile.getSlots();
    }

}
