package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerSeedStorage extends ContainerSeedStorageDummy {
    private TileEntitySeedStorage te;
    private List<SlotSeedStorage> slots;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory,  6, 49, 0, 14);
        this.te = te;
        this.initSeedSlots();
    }

    @Override
    protected void initSeedSlots() {
        this.slots = te.getInventorySlots();
        for(SlotSeedStorage slot:this.slots) {
            slot.addActiveContainer(this);
        }
    }

    @Override
    protected List<SlotSeedStorage> getAllSlots() {
        return this.slots;
    }

    @Override
    public boolean addSeedToStorage(ItemStack seedStack) {
        boolean success = false;
        if(SeedHelper.isAnalyzedSeed(seedStack)) {
            for(SlotSeedStorage slot:te.getInventorySlots()) {
                if(ItemStack.areItemStackTagsEqual(slot.getStack(), seedStack)) {
                    slot.putStack(seedStack);
                    success = true;
                    break;
                }
            }
            if(!success) {
                SlotSeedStorage newSlot = new SlotSeedStorage(te, slots.size(), seedStack);
                newSlot.addActiveContainer(this);
                te.getInventorySlots().add(newSlot);
                success = true;
            }
        }
        if(success) {
            this.resetActiveEntries();
        }
        return success;
    }

    @Override
    public ItemStack getActiveSeed() {
        ItemStack seed = null;
        if(this.te.hasLockedSeed()) {
            seed = this.te.getLockedSeed();
        }
        return seed;
    }

    @Override
    public void setActiveEntries(ItemStack stack, int offset) {
        int xOffset = 6;
        int yOffset = 7;
        int stopIndex = Math.min(te.getInventorySlots().size(), offset + this.maxNrHorizontalSeeds);
        List<SlotSeedStorage> activeEntries = te.getInventorySlots();
        for (int i = offset; i < stopIndex; i++) {
            SlotSeedStorage slot = activeEntries.get(i);
            slot.set(xOffset + 16 * i, yOffset, this.PLAYER_INVENTORY_SIZE + i);
            this.inventorySlots.add(slot);
            this.inventoryItemStacks.add(slot.getStack());
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide()== Side.SERVER) {
            this.te.setInventory(this.slots);
        }
        super.onContainerClosed(player);
    }
}
