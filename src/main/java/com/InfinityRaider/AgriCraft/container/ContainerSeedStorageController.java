package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

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
    public boolean addSeedToStorage(ItemStack stack) {
        return this.te.addStackToInventory(stack);
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
