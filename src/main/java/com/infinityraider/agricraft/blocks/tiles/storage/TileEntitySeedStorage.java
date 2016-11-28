package com.infinityraider.agricraft.blocks.tiles.storage;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.network.MessageTileEntitySeedStorage;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.NBTHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import java.util.Optional;
import javax.annotation.Nullable;

public class TileEntitySeedStorage extends TileEntityCustomWood implements ISeedStorageControllable, IDebuggable, ISidedInventory {

    private AgriSeed lockedSeed;

    /**
     * Slots stored in a HashMap to use with Container and with Controller
     */
    private Map<Integer, SeedStorageSlot> slots = new HashMap<>();
    /**
     * Slots also stored in an ArrayList to use with ISidedInventory
     */
    private ArrayList<SeedStorageSlot> slotsList = new ArrayList<>();
    private ISeedStorageController controller;

    public TileEntitySeedStorage() {
        super();
    }

    @Override
    protected void writeNBT(NBTTagCompound tag) {
        if (this.lockedSeed != null) {
            //add the locked SEED
            tag.setTag(AgriNBT.SEED, this.lockedSeed.toStack().writeToNBT(new NBTTagCompound()));
            if (this.slots != null) {
                //add the slots
                NBTTagList tagList = new NBTTagList();
                for (Map.Entry<Integer, SeedStorageSlot> entry : slots.entrySet()) {
                    if (entry != null && entry.getValue() != null) {
                        NBTTagCompound slotTag = new NBTTagCompound();
                        entry.getValue().writeToNbt(slotTag);
                        tagList.appendTag(slotTag);
                    }
                }
                tag.setTag(AgriNBT.INVENTORY, tagList);
            }
        }
        if (this.hasController()) {
            NBTHelper.addCoordsToNBT(this.controller.getCoordinates(), tag);
        }
    }

    @Override
    protected void readNBT(NBTTagCompound tag) {
        this.slots = new HashMap<>();
        this.slotsList = new ArrayList<>();
        if (tag.hasKey(AgriNBT.SEED)) {
            //read the locked SEED
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriNBT.SEED));
            this.lockedSeed = SeedRegistry.getInstance().valueOf(seedStack).orElse(null);
            if (tag.hasKey(AgriNBT.INVENTORY)) {
                //read the slots
                NBTTagList tagList = tag.getTagList(AgriNBT.INVENTORY, 10);
                int invId = this.getControllableID();
                for (int i = 0; i < tagList.tagCount(); i++) {
                    SeedStorageSlot slot = SeedStorageSlot.readFromNbt(tagList.getCompoundTagAt(i), invId).orElse(null);
                    if (slot != null) {
                        slots.put(slot.getId(), slot);
                        slotsList.add(slot);
                    }
                }
            }
        } else {
            this.lockedSeed = null;
        }
        int[] coords = NBTHelper.getCoordsFromNBT(tag);
        if (coords != null && coords.length == 3) {
            this.controller = (ISeedStorageController) worldObj.getTileEntity(getPos());
        }
    }

    public void syncSlotToClient(SeedStorageSlot slot) {
        new MessageTileEntitySeedStorage(this.getPos(), slot).sendToDimension(this.worldObj.provider.getDimension());
        this.worldObj.getChunkFromBlockCoords(this.getPos()).setChunkModified();
    }

    //Debug method
    @Override
    public void addServerDebugInfo(List<String> list) {
        final String info = this.getLockedSeed().map(s -> s.getPlant().getPlantName()).orElse("null");
        final int mapSize = this.slots == null ? 0 : this.slots.size();
        final int listSize = this.slotsList == null ? 0 : this.slotsList.size();
        list.add("Locked Seed: " + info);
        list.add("Nr of map entries: " + mapSize);
        list.add("Nr of list entries: " + listSize);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addDisplayInfo(List information) {
        final String tool = AgriCore.getTranslator().translate("agricraft_tooltip.storage");
        final String none = AgriCore.getTranslator().translate("agricraft_tooltip.none");
        information.add(tool + ": " + this.getLockedSeed().map(s -> s.getPlant().getPlantName()).orElse(none));
        super.addDisplayInfo(information);
    }

    //SEED STORAGE METHODS
    //--------------------
    @Override
    public boolean addStackToInventory(ItemStack stack) {

        // Fetch the seed.
        final AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).filter(s -> s.getStat().isAnalyzed()).orElse(null);
        if (seed == null || worldObj.isRemote) {
            return false;
        }

        // Set the locked seed if no locked seed is present.
        if (this.lockedSeed == null) {
            this.lockedSeed = seed;
            this.setSlotContents(0, stack);
            return true;
        }

        // Determine if the seed matches this chest's type.
        if (this.lockedSeed.getPlant() == seed.getPlant()) {
            for (Map.Entry<Integer, SeedStorageSlot> entry : this.slots.entrySet()) {
                if (entry.getValue() != null) {
                    if (ItemStack.areItemStackTagsEqual(lockedSeed.toStack(), stack)) {
                        ItemStack newStack = stack.copy();
                        newStack.stackSize = newStack.stackSize + entry.getValue().count;
                        this.setSlotContents(entry.getKey(), newStack);
                        return true;
                    }
                }
            }

            // If no slot to combine with was found, add to a new slot.
            int slotId = getFirstFreeSlot();
            if (slotId >= 0) {
                this.setSlotContents(slotId, stack);
                return true;
            }
        }

        // The seed could not be added to the storage unit.
        return false;

    }

    private int getFirstFreeSlot() {
        for (int i = 0; i < slots.size(); i++) {
            if (!slots.containsKey(i)) {
                return i;
            }
        }
        return slots.size();
    }

    @Override
    @Nullable
    public ItemStack getStackForSlotId(int slotId) {
        final SeedStorageSlot slot = slots.get(slotId);
        if (slot != null) {
            return slot.toStack();
        } else {
            return null;
        }
    }

    @Override
    public void setSlotContents(int realSlotId, ItemStack inputStack) {
        if (realSlotId < 0) {
            this.addStackToInventory(inputStack);
            return;
        }
        if (this.isValidForSlot(realSlotId, inputStack)) {
            SeedStorageSlot slotAt = this.slots.get(realSlotId);
            if (slotAt != null) {
                slotAt.count = inputStack.stackSize;
                if (slotAt.count <= 0) {
                    slots.remove(realSlotId);
                    slotsList.remove(slotAt);
                }
            } else {
                final AgriSeed seed = SeedRegistry.getInstance().valueOf(inputStack).get();
                slotAt = new SeedStorageSlot(seed, inputStack.stackSize, realSlotId, this.getControllableID());
                if (slotAt.count > 0) {
                    this.slots.put(realSlotId, slotAt);
                    this.slotsList.add(slotAt);
                }
            }
            if (!this.worldObj.isRemote) {
                this.syncSlotToClient(slotAt);
            } else {
                this.markForUpdate();
            }
        }
    }

    private boolean isValidForSlot(int realSlot, ItemStack stack) {

        // Fetch the seed.
        AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).orElse(null);

        // If the seed is not analyzed then it is not valid.
        if (seed == null || !seed.getStat().isAnalyzed()) {
            return false;
        }

        // If there is no locked seed, then this seed is valid.
        if (this.lockedSeed == null) {
            return true;
        }

        // If the seed type matches the locked seed type, then the seed is valid.
        if (this.lockedSeed.getPlant() == seed.getPlant()) {
            final SeedStorageSlot slotAt = this.slots.get(realSlot);
            return slotAt == null || ItemStack.areItemStackTagsEqual(stack, slotAt.getSeed().toStack());
        }

        return false;
    }

    @Override
    public ItemStack decreaseStackSizeInSlot(int realSlotId, int amount) {
        if (!worldObj.isRemote) {
            ItemStack stackInSlot = null;
            if (this.slots != null) {
                SeedStorageSlot slotAt = this.slots.get(realSlotId);
                if (slotAt != null) {
                    stackInSlot = slotAt.toStack();
                    stackInSlot.stackSize = Math.min(amount, slotAt.count);
                    if (slotAt.count <= amount) {
                        this.slots.remove(realSlotId);
                        this.slotsList.remove(slotAt);
                        slotAt.count = 0;
                    } else {
                        slotAt.count = slotAt.count - amount;
                    }
                }
                this.syncSlotToClient(slotAt);
            }
            return stackInSlot;
        }
        return null;
    }

    @Override
    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if (this.hasLockedSeed()) {
            for (Map.Entry<Integer, SeedStorageSlot> entries : slots.entrySet()) {
                if (entries != null && entries.getValue() != null) {
                    stacks.add(entries.getValue().toStack());
                }
            }
        }
        return stacks;
    }

    @Override
    public List<SeedStorageSlot> getSlots() {
        if (slotsList == null) {
            slotsList = new ArrayList<>();
        }
        return slotsList;
    }

    @Override
    public int[] getControllerCoords() {
        return this.controller != null ? this.controller.getCoordinates() : null;
    }

    @Override
    public int[] getCoords() {
        return new int[]{this.xCoord(), this.yCoord(), this.zCoord()};
    }

    @Override
    public ISeedStorageController getController() {
        return this.controller;
    }

    @Override
    public boolean hasController() {
        return this.controller != null;
    }

    @Override
    public boolean hasLockedSeed() {
        return this.lockedSeed != null;
    }

    @Override
    public boolean setLockedSeed(AgriSeed seed) {
        if (!this.hasLockedSeed()) {
            this.lockedSeed = seed;
            this.markForUpdate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearLockedSeed() {
        if (this.slots.isEmpty()) {
            this.lockedSeed = null;
            this.markForUpdate();
        }
    }

    @Override
    public Optional<AgriSeed> getLockedSeed() {
        return Optional.ofNullable(this.lockedSeed);
    }

    @Override
    public int getControllableID() {
        int id = -1;
        if (this.hasController()) {
            id = this.getController().getControllableID(this);
        }
        return id;
    }

    //INVENTORY METHODS
    //-----------------
    @Override
    public int getSizeInventory() {
        //One extra for the 'fake' input only slot
        return this.slots.size() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        return slotsList.get(slot).toStack();
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        if (!worldObj.isRemote) {
            ItemStack stackInSlot = null;
            SeedStorageSlot slotAt = this.slotsList.get(slot);
            if (slotAt != null) {
                stackInSlot = slotAt.toStack();
                stackInSlot.stackSize = Math.min(amount, slotAt.count);
                if (slotAt.count <= amount) {
                    this.slots.remove(slotAt.getId());
                    this.slotsList.remove(slotAt);
                    slotAt.count = 0;
                } else {
                    slotAt.count = slotAt.count - amount;
                }
            }
            this.syncSlotToClient(slotAt);
            return stackInSlot;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if (slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        SeedStorageSlot slotAt = slotsList.get(slot);
        ItemStack stackInSlot = slotAt.toStack();
        this.slots.remove(slotAt.getId());
        this.slotsList.remove(slotAt);
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        if (slotsList == null || slot >= slotsList.size()) {
            this.addStackToInventory(inputStack);
            return;
        }
        if (inputStack == null) {
            inputStack = slotsList.get(slot).toStack();
            inputStack.stackSize = 0;
        }
        if (this.isItemValidForSlot(slot, inputStack)) {
            SeedStorageSlot slotAt = this.slotsList.get(slot);
            if (slotAt != null) {
                slotAt.count = inputStack.stackSize;
                if (slotAt.count <= 0) {
                    slots.remove(slotAt.getId());
                    slotsList.remove(slotAt);
                }
                if (!this.worldObj.isRemote) {
                    this.syncSlotToClient(slotAt);
                }
            } else {
                this.addStackToInventory(inputStack);
            }
        }
    }

    /**
     * TODO: FIXME!
     *
     * @return
     */
    @Override
    public String getName() {
        return Reference.MOD_ID + ":seed_storage";
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.slotsList = new ArrayList<>();
        this.slots = new HashMap<>();
        this.lockedSeed = null;
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).orElse(null);
        if (seed == null || !seed.getStat().isAnalyzed()) {
            return false;
        }
        if (this.hasLockedSeed()) {
            if (this.lockedSeed.getPlant() == seed.getPlant()) {
                if (slot >= slotsList.size()) {
                    return true;
                }
                SeedStorageSlot slotAt = this.slotsList.get(slot);
                return slotAt == null || ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] array = new int[slotsList.size() + 1];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        return array;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        if (worldObj.isRemote) {
            return false;
        }
        if (slot >= slotsList.size()) {
            //0 is a virtual slot only used for inputs, this is a workaround to prevent derpy behaviour with code which modifies stacks in slots directly
            return isItemValidForSlot(slot, stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return this.slots.containsKey(slot) && this.isItemValidForSlot(slot, stack) && this.slots.get(slot).count > 0;
    }
}
