package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrier;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.network.MessageSeedAnalyzerUpdate;
import com.infinityraider.agricraft.render.blocks.TileEntitySeedAnalyzerSeedRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.utility.inventory.IInventoryItemHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySeedAnalyzer extends TileEntityBase implements ISidedInventory, IInventoryItemHandler, IDynamicCameraController {
    public static final int SLOT_SEED = 0;
    public static final int SLOT_JOURNAL = 1;
    private static final int[] SLOTS = new int[]{SLOT_SEED, SLOT_JOURNAL};

    public static final int TRANSITION_DURATION = 15;

    private final AutoSyncedField<ItemStack> seed;
    private final AutoSyncedField<ItemStack> journal;
    private final LazyOptional<TileEntitySeedAnalyzer> capability;

    private PlayerEntity observer;
    private Vector3d observerStart;

    private Vector3d observerPosition;
    private Vector2f observerOrientation;

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
            // Add the seed to the journal
            addSeedToJournal(seed, this.getJournal()).ifPresent(this::setJournal);
            // Tell clients that are watching this that the seed has changed
            new MessageSeedAnalyzerUpdate(this).sendToAllAround(
                    this.getWorld(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 16);
            // Update the block state
            BlockState state = this.getBlockState();
            if(hadSeed != BlockSeedAnalyzer.SEED.fetch(state)) {
                this.getWorld().setBlockState(this.getPos(), BlockSeedAnalyzer.SEED.apply(state, !seed.isEmpty()));
            } else if(prevSeed != this.getSeed().getItem()) {
                this.forceRenderUpdate();
            }
        }
    }

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
        // Add the current seed to the journal
        journal = addSeedToJournal(this.getSeed(), journal).orElse(journal);
        this.journal.set(journal);
        if(this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPos(), BlockSeedAnalyzer.JOURNAL.apply(this.getBlockState(), !journal.isEmpty()));
        }
    }

    @Nonnull
    public ItemStack getJournal() {
        return this.journal.get();
    }

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
    public int getTransitionDuration() {
        return TRANSITION_DURATION;
    }

    @Override
    public void setObserving(PlayerEntity player, boolean value) {
        if (this.getWorld() != null && this.getWorld().isRemote()) {
            if (this.observer != null) {
                this.observerStart = null;
                this.observer = null;
                ModuleDynamicCamera.getInstance().stopObserving();
            }
            if(value) {
                this.observer = player;
                this.observerStart = player.getPositionVec();
                ModuleDynamicCamera.getInstance().startObserving(this);
            }
        }
    }

    @Override
    public boolean continueObserving() {
        return this.observer != null && this.observerStart != null && !this.observerStart.equals(this.observer.getPositionVec());
    }

    @Override
    public Vector3d getObserverPosition() {
        if(this.observerPosition == null) {
            BlockState state = this.getBlockState();
            Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);
            this.observerPosition = new Vector3d(
                    this.getPos().getX() + 0.5 + dir.getXOffset()/2.0,
                    this.getPos().getY() + 1,
                    this.getPos().getZ() + 0.5 + dir.getZOffset()/2.0
            );
        }
        return this.observerPosition;
    }

    @Override
    public Vector2f getObserverOrientation() {
        if(this.observerOrientation == null) {
            BlockState state = this.getBlockState();
            Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);
            // The analyzer looking glass is tilted 22.5 degrees, therefore we have to pitch down 67.5 degrees
            this.observerOrientation = new Vector2f(67.5F, dir.getOpposite().getHorizontalAngle());
        }
        return this.observerOrientation;
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
                    return stack.isEmpty() || stack.getItem() instanceof IAgriGeneCarrierItem;
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
                    this.setSeed(stack);
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

    public static Optional<ItemStack> addSeedToJournal(ItemStack seed, ItemStack journal) {
        // Check if the items are a seed and a journal respectively
        if(!seed.isEmpty() && !journal.isEmpty()
                && seed.getItem() instanceof IAgriGeneCarrier
                && journal.getItem() instanceof IAgriJournalItem) {
            // Fetch plant from seed
            IAgriJournalItem journalItem = (IAgriJournalItem) journal.getItem();
            IAgriPlant plant = ((IAgriGeneCarrierItem) seed.getItem()).getPlant(seed);
            // If the plant is not yet discovered, add it to the journal
            if (!journalItem.isPlantDiscovered(journal, plant)) {
                ItemStack newJournal = journal.copy();
                journalItem.addEntry(newJournal, plant);
                return Optional.of(newJournal);
            }
        }
        return Optional.empty();
    }

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntitySeedAnalyzer> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntitySeedAnalyzerSeedRenderer createRenderer() {
            return new TileEntitySeedAnalyzerSeedRenderer();
        }
    }
}
