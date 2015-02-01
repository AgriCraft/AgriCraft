package com.InfinityRaider.AgriCraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class ContainerAgricraft extends Container {
    public int PLAYER_INVENTORY_SIZE = 36;

    public ContainerAgricraft(InventoryPlayer inventory, int xOffset, int yOffset) {
        //add player's inventory to the container
        for(int i=0;i<3;i++) {
            for(int j=0;j<9;j++) {
                //new Slot(inventory, slot index, x coordinate, y coordinate)
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, xOffset+j*18, yOffset+i*18));
            }
        }
        //add player's hot bar to the container
        for(int i=0;i<9;i++) {
            //new Slot(inventory, slot index, x coordinate, y coordinate)
            this.addSlotToContainer(new Slot(inventory, i, xOffset + i*18, 58+yOffset));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
