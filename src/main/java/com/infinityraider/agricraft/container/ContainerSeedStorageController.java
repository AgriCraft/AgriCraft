package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorageController;
import java.util.List;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerSeedStorageController extends ContainerSeedStorageBase<TileEntitySeedStorageController> {

    private static final int invOffsetX = 82;
    private static final int invOffsetY = 94;

    public ContainerSeedStorageController(TileEntitySeedStorageController tile, InventoryPlayer inventory) {
        super(tile, inventory, invOffsetX, invOffsetY);
    }

    @Override
    public boolean addSeedToStorage(ItemStack stack) {
        return this.getTile().addStackToInventory(stack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return this.getTile().getControlledSeeds();
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(AgriSeed seed) {
        final ItemStack stack = seed.toStack();
        return this.getTile().getSlots(stack.getItem(), stack.getItemDamage());
    }

}
