package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.inventory.IInventoryItemHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySeedAnalyzer extends TileEntityBase implements ISidedInventory, IInventoryItemHandler {
    public static final int SLOT_SEED = 0;
    public static final int SLOT_JOURNAL = 1;
    private static final int[] SLOTS = new int[]{SLOT_SEED, SLOT_JOURNAL};

    private final AutoSyncedField<ItemStack> seed;
    private final AutoSyncedField<ItemStack> journal;
    private final LazyOptional<TileEntitySeedAnalyzer> capability;

    public TileEntitySeedAnalyzer() {
        super(AgriCraft.instance.getModTileRegistry().seed_analyzer);
        this.seed = createField(ItemStack.EMPTY, ItemStack::write, ItemStack::read);
        this.journal = createField(ItemStack.EMPTY, ItemStack::write, ItemStack::read);
        this.capability = LazyOptional.of(() -> this);
    }

    public boolean hasSeed() {
        return !this.getSeed().isEmpty();
    }

    @Nonnull
    public ItemStack getSeed() {
        return this.seed.get();
    }

    protected void setSeed(ItemStack seed) {
        boolean hadSeed = this.hasSeed();
        Item prevSeed = this.getSeed().getItem();
        this.seed.set(seed);
        if(this.getWorld() != null) {
            BlockState state = this.getBlockState();
            if(hadSeed != BlockSeedAnalyzer.SEED.fetch(state)) {
                this.getWorld().setBlockState(this.getPos(), BlockSeedAnalyzer.SEED.apply(state, !seed.isEmpty()));
            } else if(prevSeed != this.getSeed().getItem()) {
                this.forceRenderUpdate();
            }
        }
    }

    @Nonnull
    public boolean canInsertSeed(ItemStack seed) {
        return this.isItemValidForSlot(SLOT_SEED, seed);
    }

    @Nonnull
    public ItemStack insertSeed(ItemStack seed) {
        return this.insertItem(SLOT_SEED, seed, false);
    }

    @Nonnull
    public ItemStack extractSeed() {
        return this.removeStackFromSlot(SLOT_SEED);
    }

    public boolean hasJournal() {
        return !this.getJournal().isEmpty();
    }

    protected void setJournal(ItemStack journal) {
        this.journal.set(journal);
        if(this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPos(), BlockSeedAnalyzer.JOURNAL.apply(this.getBlockState(), !journal.isEmpty()));
        }
    }

    @Nonnull
    public ItemStack getJournal() {
        return this.journal.get();
    }

    @Nonnull
    public boolean canInsertJournal(ItemStack journal) {
        return this.isItemValidForSlot(SLOT_JOURNAL, journal);
    }

    @Nonnull
    public ItemStack insertJournal(ItemStack journal) {
        return this.insertItem(SLOT_JOURNAL, journal, false);
    }

    @Nonnull
    public ItemStack extractJournal() {
        return this.removeStackFromSlot(SLOT_JOURNAL);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {
        // NOOP (everything is handled by auto synced fields)
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        // NOOP (everything is handled by auto synced fields)
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, @Nullable Direction direction) {
        switch (index) {
            case SLOT_SEED:
                if(this.getSeed().isEmpty()) {
                    return stack.isEmpty() || stack.getItem() instanceof IAgriSeedItem;
                } else {
                    if(ItemStack.areItemsEqual(stack, this.getSeed()) && ItemStack.areItemStackTagsEqual(stack, this.getSeed())) {
                        return this.getSeed().getCount() + stack.getCount() <= stack.getMaxStackSize();
                    } else {
                        return false;
                    }
                }
            case SLOT_JOURNAL:
                if(this.getJournal().isEmpty()) {
                    return stack.isEmpty() || stack.getItem() instanceof IAgriJournalItem;
                } else {
                    return stack.isEmpty();
                }
            default: return false;
        }
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        if(stack.isEmpty()) {
            // Doesn't make sense extracting empty stacks
            return false;
        }
        switch(index) {
            case SLOT_SEED:
                if(this.getSeed().isEmpty() || this.getSeed().getCount() < stack.getCount()) {
                    return false;
                }
                return ItemStack.areItemsEqual(stack, this.getSeed()) && ItemStack.areItemStackTagsEqual(stack, this.getSeed());
            case SLOT_JOURNAL:
                // Do not allow automated extraction of the journal
            default: return false;
        }
    }

    @Override
    public int getSizeInventory() {
        return SLOTS.length;
    }

    @Override
    public boolean isEmpty() {
        return this.getSeed().isEmpty() && this.getJournal().isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        switch (index) {
            case SLOT_SEED: return this.getSeed();
            case SLOT_JOURNAL: return this.getJournal();
            default: return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStack.EMPTY;
        if(count <= 0) {
            return stack;
        }
        switch (index) {
            case SLOT_SEED:
                stack = this.getSeed().copy();
                if(stack.getCount() > count) {
                    stack.setCount(count);
                    ItemStack seed = this.getSeed();
                    seed.setCount(seed.getCount() - count);
                    this.setSeed(seed);
                } else {
                    this.setSeed(ItemStack.EMPTY);
                }
                return stack;
            case SLOT_JOURNAL:
                stack = this.getJournal().copy();
                this.setJournal(ItemStack.EMPTY);
                return stack;
            default:
                return stack;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStack.EMPTY;
        switch (index) {
            case SLOT_SEED:
                stack = this.getSeed().copy();
                this.setSeed(ItemStack.EMPTY);
                return stack;
            case SLOT_JOURNAL:
                stack = this.getJournal().copy();
                this.setJournal(ItemStack.EMPTY);
                return stack;
            default:
                return stack;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(this.isItemValidForSlot(index, stack)) {
            switch (index) {
                case SLOT_SEED:
                    if(stack.getItem() instanceof IAgriSeedItem) {
                        if (this.getSeed().isEmpty() && !this.getJournal().isEmpty()) {
                            // should always be the case, but it's still modded minecraft
                            if (this.getJournal().getItem() instanceof IAgriJournalItem) {
                                // Add seed to journal if not yet discovered
                                IAgriJournalItem journal = (IAgriJournalItem) this.getJournal().getItem();
                                IAgriPlant plant = ((IAgriSeedItem) stack.getItem()).getPlant(stack);
                                if(!journal.isPlantDiscovered(this.getJournal(), plant)) {
                                    ItemStack newJournal = this.getJournal();
                                    journal.addEntry(newJournal, plant);
                                    this.setJournal(newJournal);
                                }
                            }
                        }
                        this.setSeed(stack);
                    }
                    break;
                case SLOT_JOURNAL:
                    this.setJournal(stack);
                    break;
            }
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.canInsertItem(index, stack, null);
    }

    @Override
    public void clear() {
        this.setSeed(ItemStack.EMPTY);
        this.setJournal(ItemStack.EMPTY);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.removed && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.capability.cast();
        }
        return super.getCapability(capability, facing);
    }
}
