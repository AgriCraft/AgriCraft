package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.network.MessageContainerSeedStorage;
import com.infinityraider.agricraft.tiles.storage.ISeedStorageControllable;
import com.infinityraider.agricraft.tiles.storage.ISeedStorageController;
import com.infinityraider.agricraft.tiles.storage.SeedStorageSlot;
import java.util.List;
import java.util.Optional;

import com.infinityraider.infinitylib.container.ContainerTileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public abstract class ContainerSeedStorageBase<T extends TileEntity> extends ContainerTileBase<T> {

    public ContainerSeedStorageBase(T tile, InventoryPlayer inventory, int xOffset, int yOffset) {
        super(tile, inventory, xOffset, yOffset);
    }

    /**
     * tries to add a stack to the storage, return true on success
     */
    public abstract boolean addSeedToStorage(ItemStack stack);

    /**
     * Gets a list off all the different kinds of seeds in the storage
     */
    public abstract List<ItemStack> getSeedEntries();

    /**
     * Gets a list off all the slots corresponding to this seed and meta
     */
    public abstract List<SeedStorageSlot> getSeedSlots(AgriSeed seed);

    public Optional<ISeedStorageControllable> getControllable(ItemStack stack) {
        if (this.getTile() instanceof ISeedStorageController) {
            return ((ISeedStorageController) this.getTile()).getControllable(stack);
        } else if (this.getTile() instanceof ISeedStorageControllable) {
            return Optional.of((ISeedStorageControllable) getTile());
        } else {
            return Optional.empty();
        }
    }

    /**
     * returns a list if itemStacks, for each slot.
     */
    @Override
    public NonNullList<ItemStack> getInventory() {
        return super.getInventory();
    }

    /**
     * Tries to move an item stack form the correct tile entity to the player's inventory
     */
    public void moveStackFromTileEntityToPlayer(int slotId, ItemStack stack) {
        ISeedStorageControllable controllable = this.getControllable(stack).orElse(null);
        if (controllable == null) {
            return;
        }
        ItemStack stackToMove = controllable.getStackForSlotId(slotId);
        if (stack.isEmpty()) {
            return;
        }
        if (stackToMove.isEmpty()) {
            return;
        }
        stackToMove.setCount(stack.getCount() > stackToMove.getCount() ? stackToMove.getCount() : stack.getCount());
        stackToMove.setTagCompound(controllable.getStackForSlotId(slotId).getTagCompound());
        if (this.mergeItemStack(stackToMove, 0, PLAYER_INVENTORY_SIZE, false)) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                //this method is only called form the gui client side, so we need to manually tell the server to execute it there
                new MessageContainerSeedStorage(stack, slotId).sendToServer();
            } else {
                //on the server decrease the size of the stack, where it is synced to the client
                controllable.decreaseStackSizeInSlot(slotId, stack.getCount() - stackToMove.getCount());
            }
        }
    }

    /**
     * Handles shift clicking in the inventory, return the stack that was transferred
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack originalStackInSlot = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack notMergedStack = slot.getStack();
            originalStackInSlot = notMergedStack.copy();
            //try to move item from the player's inventory into the container
            AgriSeed seed = AgriApi.getSeedRegistry().valueOf(notMergedStack).orElse(null);
            if (seed != null && seed.getStat().isAnalyzed()) {
                ISeedStorageControllable controllable = this.getControllable(notMergedStack).orElse(null);
                if (controllable != null && controllable.hasLockedSeed()) {
                    ItemStack locked = controllable.getLockedSeed().map(AgriSeed::toStack).orElse(ItemStack.EMPTY);
                    if (notMergedStack.getItem() != locked.getItem() || notMergedStack.getItemDamage() != locked.getItemDamage()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (this.addSeedToStorage(notMergedStack)) {
                    notMergedStack.setCount(0);
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (notMergedStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (notMergedStack.getCount() == originalStackInSlot.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, notMergedStack);
        }
        return originalStackInSlot;
    }

    /**
     * Tries to merge an itemstack into a range of slots, return true if the stack was (partly)
     * merged
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean iterateBackwards) {
        boolean flag = false;
        int k = iterateBackwards ? endSlot - 1 : startSlot;
        Slot currentSlot;
        ItemStack currentStack;
        //look for identical stacks to merge with
        while (stack.getCount() > 0 && (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot)) {
            currentSlot = this.inventorySlots.get(k);
            currentStack = currentSlot.getStack();
            if (currentStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == currentStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, currentStack)) {
                int l = currentStack.getCount() + stack.getCount();
                //total stacksize is smaller than the limit: merge entire stack into this stack
                if (l <= stack.getMaxStackSize()) {
                    stack.setCount(0);
                    currentStack.setCount(l);
                    currentSlot.onSlotChanged();
                    flag = true;
                } //total stacksize exceeds the limit: merge part of the stack into this stack
                else if (currentStack.getCount() < stack.getMaxStackSize()) {
                    stack.setCount(stack.getCount() - stack.getMaxStackSize() - currentStack.getCount());
                    currentStack.setCount(stack.getMaxStackSize());
                    currentSlot.onSlotChanged();
                    flag = true;
                }
            }
            k = iterateBackwards ? k - 1 : k + 1;
        }
        //couldn't completely merge stack with an existing slot, find the first empty slot to put the rest of the stack in
        if (stack.getCount() > 0) {
            k = iterateBackwards ? endSlot - 1 : startSlot;
            while (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot) {
                currentSlot = this.inventorySlots.get(k);
                currentStack = currentSlot.getStack();
                if (currentStack.isEmpty()) {
                    currentSlot.putStack(stack.copy());
                    currentSlot.onSlotChanged();
                    stack.setCount(0);
                    flag = true;
                    break;
                }
                k = iterateBackwards ? k - 1 : k + 1;
            }
        }
        return flag;
    }

}
