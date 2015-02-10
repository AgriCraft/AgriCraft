package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerSeedStorageController extends ContainerSeedStorageDummy {
    public TileEntitySeedStorageController te;
    private static final int invOffsetX = 82;
    private static final int invOffsetY = 94;

    public ContainerSeedStorageController(InventoryPlayer inventory, TileEntitySeedStorageController te) {
        super(inventory, invOffsetX, invOffsetY);
    }


    /**
     * tries to add a stack to the storage, return true on success
     */
    @Override
    //TODO: rewrite this
    public boolean addSeedToStorage(ItemStack stack) {
        boolean success = false;

        return success;
    }

    @Override
    public List<ItemStack> getSeedEntries() {
        return null;
    }

    @Override
    public List<SeedStorageSlot> getSeedSlots(ItemSeeds seed, int meta) {
        return this.te.getSlots(seed, meta);
    }

    /**
     * places item stacks in the first x slots, x being itemstack.length
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] stackArray) {
        for (int i=0;i<Math.min(stackArray.length,this.PLAYER_INVENTORY_SIZE);++i) {
            this.getSlot(i).putStack(stackArray[i]);
        }
        for(int i=this.PLAYER_INVENTORY_SIZE;i<stackArray.length;i++) {
            this.addSeedToStorage(stackArray[i]);
        }
    }
}
