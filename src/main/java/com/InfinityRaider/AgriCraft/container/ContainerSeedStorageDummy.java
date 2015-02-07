package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.network.MessageContainerSeedStorage;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public abstract class ContainerSeedStorageDummy extends ContainerAgricraft {
    protected int activeSlotOffsetX;
    protected int activeSlotOffsetY;

    public ContainerSeedStorageDummy(InventoryPlayer inventory, int xOffset, int yOffset) {
        super(inventory, xOffset, yOffset);
    }

    public ItemStack getActiveSeed() {
        ItemStack seed = null;
        if(this.inventorySlots.size()>36) {
            seed = ((SlotSeedStorage) this.inventorySlots.get(36)).getStack().copy();
            seed.stackSize = 1;
        }
        return seed;
    }

    public void resetActiveEntries() {
        this.resetActiveEntries(this.getActiveSeed(), 0);
    }

    public void resetActiveEntries(ItemStack stack, int offset) {
        this.clearActiveEntries();
        this.setActiveEntries(stack, offset);
        if(FMLCommonHandler.instance().getEffectiveSide()== Side.CLIENT) {
            NetworkWrapperAgriCraft.wrapper.sendToServer(new MessageContainerSeedStorage(Minecraft.getMinecraft().thePlayer, stack.getItem(), stack.getItemDamage(), offset));
        }
    }

    public abstract void setActiveEntries(ItemStack stack, int offset);

    public void clearActiveEntries() {
        for(int i=this.inventoryItemStacks.size()-1;i>=this.PLAYER_INVENTORY_SIZE;i--) {
            ((SlotSeedStorage) this.inventorySlots.get(i)).reset();
            this.inventorySlots.remove(i);
            this.inventoryItemStacks.remove(i);
        }
    }
}
