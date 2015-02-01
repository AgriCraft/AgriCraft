package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotSeedStorage extends Slot {
    public boolean active = false;
    private ItemStack seed;
    public int index;
    public int count;
    public ContainerSeedStorage container;

    public SlotSeedStorage(ContainerSeedStorage container, IInventory inventory, int id, int xOffset, int yOffset, ItemStack stack) {
        super(inventory, id, xOffset, yOffset);
        this.container = container;
        this.index = id;
        this.seed = stack.copy();
        this.count = stack.stackSize;
        this.seed.stackSize = 1;
    }

    /**
     * Check if the stack is a valid item for this slot. Only allow analyzed seeds.
     */
    @Override
    public boolean isItemValid(ItemStack stack) {
        if(stack.getItem() instanceof ItemSeeds) {
            if(stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if(tag.hasKey(Names.NBT.analyzed)) {
                    return tag.getBoolean(Names.NBT.analyzed);
                }
            }
        }
        return false;
    }

    /**
     * Helper function to get the stack in the slot.
     */
    @Override
    public ItemStack getStack() {
        ItemStack stack = this.seed.copy();
        stack.stackSize = this.count;
        return stack;
    }

    /**
     * Returns if this slot contains a stack.
     */
    @Override
    public boolean getHasStack() {
        ItemStack stack = this.getStack();
        return stack!=null && stack.getItem()!=null;
    }

    /**
     * Helper method to put a stack in the slot.
     */
    @Override
    public void putStack(ItemStack stack) {
        if(this.seed==null) {
            this.seed = stack.copy();
            this.count = 0;
        }
        this.count = count + stack.stackSize;
        this.onSlotChanged();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return this.count+64;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack result = this.seed.copy();
        if(amount==count) {
            result.stackSize = amount;
            this.count = 0;
            this.seed = null;
        }
        else if(amount>count) {
            result.stackSize = count;
            this.count = 0;
            this.seed = null;
        }
        else {
            result.stackSize = count;
            this.count = count - amount;
        }
        return result;
    }

    /** The index of the slot in the inventory. */
    @Override
    public int getSlotIndex() {
        return this.index;
    }

    public boolean isActive() {
        return  this.active;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_111238_b() {
        return this.active;
    }



}
