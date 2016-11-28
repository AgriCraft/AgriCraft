package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.blocks.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorageController;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class ContainerSeedStorageController extends ContainerSeedStorageBase {

    public TileEntitySeedStorageController te;
    private static final int invOffsetX = 82;
    private static final int invOffsetY = 94;

    public ContainerSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(inventory, invOffsetX, invOffsetY);
        this.te = te;
    }

    @Override
    public boolean addSeedToStorage(ItemStack stack) {
        return this.te.addStackToInventory(stack);
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return this.te.getControlledSeeds();
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(AgriSeed seed) {
        final ItemStack stack = seed.toStack();
        return this.te.getSlots(stack.getItem(), stack.getItemDamage());
    }

    @Override
    public TileEntity getTileEntity() {
        return this.te;
    }
}
