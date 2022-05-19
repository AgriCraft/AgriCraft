package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.render.blocks.TileEntitySeedAnalyzerSeedRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import com.infinityraider.infinitylib.utility.inventory.IContainerItemHandler;
import com.infinityraider.infinitylib.utility.inventory.IWorldlyContainerWrapped;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySeedAnalyzer extends TileEntityBase implements IWorldlyContainerWrapped, IContainerItemHandler, IDynamicCameraController, IDebuggable {
    public static final int SLOT_SEED = 0;
    public static final int SLOT_JOURNAL = 1;
    private static final int[] SLOTS = new int[]{SLOT_SEED, SLOT_JOURNAL};

    private final AutoSyncedField<ItemStack> seed;
    private final AutoSyncedField<ItemStack> journal;
    private final LazyOptional<TileEntitySeedAnalyzer> capability;

    private Player observer;
    private Vec3 observerStart;

    private Vec3 observerPosition;
    private Vec2 observerOrientation;

    private List<IAgriGenePair<?>> genesToRender;

    public TileEntitySeedAnalyzer(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.getInstance().seed_analyzer.get(), pos, state);
        this.seed = getAutoSyncedFieldBuilder(ItemStack.EMPTY)
                .withCallBack(seed -> addSeedToJournal(seed, this.getJournal()).ifPresent(this::setJournal))
                .build();
        this.journal = getAutoSyncedFieldBuilder(ItemStack.EMPTY)
                .withCallBack(journal -> {
                    if (this.getLevel() != null) {
                        this.getLevel().setBlock(this.getBlockPos(), BlockSeedAnalyzer.JOURNAL.apply(this.getBlockState(), !journal.isEmpty()), 3);
                    }})
                .withRenderUpdate()
                .build();
        this.capability = LazyOptional.of(() -> this);
    }

    public Direction getOrientation() {
        return BlockSeedAnalyzer.ORIENTATION.fetch(this.getBlockState());
    }

    public float getHorizontalAngle() {
        Direction dir = this.getOrientation();
        // For some reason Z axis directions have to be inverted
        return (dir.getAxis() == Direction.Axis.Z ? dir.getOpposite() : dir).toYRot();
    }

    public boolean isObserved() {
        return this.observer != null;
    }

    public boolean canProvideGenesForObserver() {
        return this.getGenesToRender() != null;
    }

    @Nullable
    public List<IAgriGenePair<?>> getGenesToRender() {
        return this.genesToRender;
    }

    @Override
    public int getTransitionDuration() {
        return AgriCraft.instance.getConfig().seedAnalyzerAnimationDuration();
    }

    public boolean setObserving(boolean value) {
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            return AgriCraft.instance.proxy().toggleDynamicCamera(this, value);
        }
        return false;
    }

    @Override
    public void onCameraActivated() {
        this.observer = AgriCraft.instance.getClientPlayer();
        this.observerStart = this.observer == null ? null : this.observer.position();
        AgriCraft.instance.proxy().toggleSeedAnalyzerActive(true);
    }

    @Override
    public void onObservationStart() {
        if(this.getSeed().getItem() instanceof IAgriGeneCarrierItem) {
            this.genesToRender = ((IAgriGeneCarrierItem) this.getSeed().getItem()).getGenome(this.getSeed())
                    .map(IAgriGenome::getGeneList)
                    .orElse(Collections.emptyList());
        } else {
            this.genesToRender = Collections.emptyList();
        }
        AgriCraft.instance.proxy().toggleSeedAnalyzerObserving(true);
    }

    @Override
    public void onObservationEnd() {
        this.genesToRender = null;
        AgriCraft.instance.proxy().toggleSeedAnalyzerObserving(false);
    }

    @Override
    public void onCameraDeactivated() {
        this.observerStart = null;
        this.observer = null;
        AgriCraft.instance.proxy().toggleSeedAnalyzerActive(false);
    }

    @Override
    public boolean shouldContinueObserving() {
        return this.observer != null && this.observerStart != null && this.observerStart.equals(this.observer.position());
    }

    @Override
    public Vec3 getObserverPosition() {
        if(this.observerPosition == null) {
            this.observerPosition = this.calculateObserverPosition(AgriCraft.instance.proxy().getFieldOfView());
        }
        return this.observerPosition;
    }

    protected Vec3 calculateObserverPosition(double fov) {
        // calculate offset from the center of the looking glass based on fov
        double d = 0.75 * (0.5/Math.tan(Math.PI * fov / 360));
        double dy = d* Mth.sin((float) Math.PI * 67.5F / 180);
        double dx = d*Mth.cos((float) Math.PI * 67.5F / 180);
        // fetch orientation, to determine the center of the looking glass
        BlockState state = this.getBlockState();
        Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);
        // apply observer position (center of looking glass + fov offset)
        return this.observerPosition = new Vec3(
                this.getBlockPos().getX() + 0.5 + (dx + 0.3125) * dir.getStepX(),
                this.getBlockPos().getY() + 0.6875 + dy,
                this.getBlockPos().getZ() + 0.5 + (dx + 0.3125) * dir.getStepZ()
        );
    }

    @Override
    public Vec2 getObserverOrientation() {
        if(this.observerOrientation == null) {
            BlockState state = this.getBlockState();
            Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);
            // The analyzer looking glass is tilted 22.5 degrees, therefore we have to pitch down 67.5 degrees
            this.observerOrientation = new Vec2(67.5F, dir.getOpposite().toYRot());
        }
        return this.observerOrientation;
    }

    @Override
    public void onFieldOfViewChanged(double fov) {
        this.observerPosition = this.calculateObserverPosition(fov);
    }

    public boolean hasSeed() {
        return !this.getSeed().isEmpty();
    }

    @Nonnull
    public ItemStack getSeed() {
        return this.seed.get();
    }

    protected void setSeed(ItemStack seed) {
        this.seed.set(seed);
    }

    public boolean canInsertSeed(ItemStack seed) {
        return this.canPlaceItem(SLOT_SEED, seed);
    }

    @Nonnull
    public ItemStack insertSeed(ItemStack seed) {
        return this.insertItem(SLOT_SEED, seed, false);
    }

    @Nonnull
    public ItemStack extractSeed() {
        return this.getItem(SLOT_SEED);
    }

    public boolean hasJournal() {
        return !this.getJournal().isEmpty();
    }

    protected void setJournal(ItemStack journal) {
        this.journal.set(addSeedToJournal(this.getSeed(), journal).orElse(journal));
    }

    @Nonnull
    public ItemStack getJournal() {
        return this.journal.get();
    }

    public boolean canInsertJournal(ItemStack journal) {
        return this.canPlaceItem(SLOT_JOURNAL, journal);
    }

    @Nonnull
    public ItemStack insertJournal(ItemStack journal) {
        return this.insertItem(SLOT_JOURNAL, journal, false);
    }

    @Nonnull
    public ItemStack extractJournal() {
        return this.getItem(SLOT_JOURNAL);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundTag tag) {
        // NOOP (everything is handled by auto synced fields)
    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {
        // NOOP (everything is handled by auto synced fields)
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        switch (index) {
            case SLOT_SEED:
                if(this.getSeed().isEmpty()) {
                    return stack.isEmpty() || stack.getItem() instanceof IAgriGeneCarrierItem;
                } else {
                    if(ItemStack.matches(stack, this.getSeed()) && ItemStack.tagMatches(stack, this.getSeed())) {
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
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if(stack.isEmpty()) {
            // Doesn't make sense extracting empty stacks
            return false;
        }
        switch(index) {
            case SLOT_SEED:
                if(this.getSeed().isEmpty() || this.getSeed().getCount() < stack.getCount()) {
                    return false;
                }
                return ItemStack.matches(stack, this.getSeed()) && ItemStack.tagMatches(stack, this.getSeed());
            case SLOT_JOURNAL:
                // Do not allow automated extraction of the journal
            default: return false;
        }
    }

    @Override
    public int getContainerSize() {
        return SLOTS.length;
    }

    @Override
    public boolean isEmpty() {
        return this.getSeed().isEmpty() && this.getJournal().isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.getStackInInvSlot(index);
    }

    @Override
    public ItemStack getStackInInvSlot(int index) {
        switch (index) {
            case SLOT_SEED: return this.getSeed();
            case SLOT_JOURNAL: return this.getJournal();
            default: return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeItem(int index, int count) {
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
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.getItem(index);
        if(stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.removeItem(index, stack.getCount());
    }

    @Override
    public ItemStack getItem(int index) {
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
    public void setItem(int index, ItemStack stack) {
        if(this.canPlaceItem(index, stack)) {
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
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return this.canPlaceItemThroughFace(index, stack, null);
    }

    @Override
    public void clearContent() {
        this.setSeed(ItemStack.EMPTY);
        this.setJournal(ItemStack.EMPTY);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (!this.isRemoved() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.capability.cast();
        }
        return super.getCapability(capability, facing);
    }

    public static Optional<ItemStack> addSeedToJournal(ItemStack seed, ItemStack journal) {
        // Check if the items are a seed and a journal respectively
        if(!seed.isEmpty() && !journal.isEmpty()
                && seed.getItem() instanceof IAgriGeneCarrierItem
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

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        this.addDebugInfo(consumer);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addClientDebugInfo(Consumer<String> consumer) {
        this.addDebugInfo(consumer);
    }

    protected void addDebugInfo(Consumer<String> consumer) {
        ItemStack journalStack = this.getJournal();
        if(journalStack.isEmpty() || !(journalStack.getItem() instanceof IAgriJournalItem)) {
            consumer.accept("No journal present");
        } else {
            IAgriJournalItem journalItem = (IAgriJournalItem) journalStack.getItem();
            consumer.accept("Journal ("+ journalItem.getDiscoveredSeeds(journalStack).size() + " plants):");
            for(IAgriPlant plant : journalItem.getDiscoveredSeeds(journalStack)) {
                consumer.accept(" - " + plant.getId());
            }
        }
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntitySeedAnalyzer> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntitySeedAnalyzerSeedRenderer createRenderer() {
            return new TileEntitySeedAnalyzerSeedRenderer();
        }
    }
}
