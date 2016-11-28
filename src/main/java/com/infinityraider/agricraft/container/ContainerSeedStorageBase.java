package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.network.MessageContainerSeedStorage;
import com.infinityraider.agricraft.blocks.tiles.storage.ISeedStorageControllable;
import com.infinityraider.agricraft.blocks.tiles.storage.ISeedStorageController;
import com.infinityraider.agricraft.blocks.tiles.storage.SeedStorageSlot;
import com.infinityraider.infinitylib.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Optional;

public abstract class ContainerSeedStorageBase extends ContainerBase {

    public ContainerSeedStorageBase(InventoryPlayer inventory, int xOffset, int yOffset) {
        super(inventory, xOffset, yOffset);
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

    /**
     * Gets a list off all the slots corresponding to this seed and meta
     */
    public abstract TileEntity getTileEntity();

    public Optional<ISeedStorageControllable> getControllable(ItemStack stack) {
        TileEntity te = this.getTileEntity();
        if (te instanceof ISeedStorageController) {
            return ((ISeedStorageController) te).getControllable(stack);
        } else if (te instanceof ISeedStorageControllable) {
            return Optional.of((ISeedStorageControllable) te);
        } else {
            return Optional.empty();
        }
    }

    /**
     * returns a list if itemStacks, for each slot.
     */
    @Override
    public List<ItemStack> getInventory() {
        return super.getInventory();
    }

    /**
     * Tries to move an item stack form the correct tile entity to the player's
     * inventory
     */
    public void moveStackFromTileEntityToPlayer(int slotId, ItemStack stack) {
        ISeedStorageControllable controllable = this.getControllable(stack).orElse(null);
        if (controllable == null) {
            return;
        }
        ItemStack stackToMove = controllable.getStackForSlotId(slotId);
        if (stack == null) {
            return;
        }
        if (stackToMove == null || stackToMove.getItem() == null) {
            return;
        }
        stackToMove.stackSize = stack.stackSize > stackToMove.stackSize ? stackToMove.stackSize : stack.stackSize;
        stackToMove.setTagCompound(controllable.getStackForSlotId(slotId).getTagCompound());
        if (this.mergeItemStack(stackToMove, 0, PLAYER_INVENTORY_SIZE, false)) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                //this method is only called form the gui client side, so we need to manually tell the server to execute it there
                new MessageContainerSeedStorage(stack, slotId).sendToServer();
            } else {
                //on the server decrease the size of the stack, where it is synced to the client
                controllable.decreaseStackSizeInSlot(slotId, stack.stackSize - stackToMove.stackSize);
            }
        }
    }

    /**
     * Handles shift clicking in the inventory, return the stack that was
     * transferred
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack originalStackInSlot = null;
        Slot slot = this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack notMergedStack = slot.getStack();
            originalStackInSlot = notMergedStack.copy();
            //try to move item from the player's inventory into the container
            AgriSeed seed = SeedRegistry.getInstance().valueOf(notMergedStack).orElse(null);
            if (seed != null && seed.getStat().isAnalyzed()) {
                ISeedStorageControllable controllable = this.getControllable(notMergedStack).orElse(null);
                if (controllable != null && controllable.hasLockedSeed()) {
                    ItemStack locked = controllable.getLockedSeed().map(s -> s.toStack()).orElse(null);
                    if (notMergedStack.getItem() != locked.getItem() || notMergedStack.getItemDamage() != locked.getItemDamage()) {
                        return null;
                    }
                }
                if (this.addSeedToStorage(notMergedStack)) {
                    notMergedStack.stackSize = 0;
                } else {
                    return null;
                }
            }
            if (notMergedStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (notMergedStack.stackSize == originalStackInSlot.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, notMergedStack);
        }
        return originalStackInSlot;
    }

    /**
     * Tries to merge an itemstack into a range of slots, return true if the
     * stack was (partly) merged
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean iterateBackwards) {
        boolean flag = false;
        int k = iterateBackwards ? endSlot - 1 : startSlot;
        Slot currentSlot;
        ItemStack currentStack;
        //look for identical stacks to merge with
        while (stack.stackSize > 0 && (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot)) {
            currentSlot = this.inventorySlots.get(k);
            currentStack = currentSlot.getStack();
            if (currentStack != null && currentStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == currentStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, currentStack)) {
                int l = currentStack.stackSize + stack.stackSize;
                //total stacksize is smaller than the limit: merge entire stack into this stack
                if (l <= stack.getMaxStackSize()) {
                    stack.stackSize = 0;
                    currentStack.stackSize = l;
                    currentSlot.onSlotChanged();
                    flag = true;
                } //total stacksize exceeds the limit: merge part of the stack into this stack
                else if (currentStack.stackSize < stack.getMaxStackSize()) {
                    stack.stackSize -= stack.getMaxStackSize() - currentStack.stackSize;
                    currentStack.stackSize = stack.getMaxStackSize();
                    currentSlot.onSlotChanged();
                    flag = true;
                }
            }
            k = iterateBackwards ? k - 1 : k + 1;
        }
        //couldn't completely merge stack with an existing slot, find the first empty slot to put the rest of the stack in
        if (stack.stackSize > 0) {
            k = iterateBackwards ? endSlot - 1 : startSlot;
            while (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot) {
                currentSlot = this.inventorySlots.get(k);
                currentStack = currentSlot.getStack();
                if (currentStack == null) {
                    currentSlot.putStack(stack.copy());
                    currentSlot.onSlotChanged();
                    stack.stackSize = 0;
                    flag = true;
                    break;
                }
                k = iterateBackwards ? k - 1 : k + 1;
            }
        }
        return flag;
    }

}
