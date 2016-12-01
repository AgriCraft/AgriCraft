package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.blocks.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class ContainerSeedStorage extends ContainerSeedStorageBase {
    private static final int invOffsetX = 6;
    private static final int invOffsetY = 49;
    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, invOffsetX, invOffsetY);
        this.te = te;
    }

	@Override
    public boolean addSeedToStorage(ItemStack seedStack) {
        return this.te.addStackToInventory(seedStack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ItemStack> getSeedEntries() {
        return this.te.getInventory();
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(AgriSeed seed) {
        return this.te.getSlots();
    }

    @Override
    public TileEntity getTileEntity() {
        return this.te;
    }
}