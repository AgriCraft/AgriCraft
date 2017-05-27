package com.infinityraider.agricraft.tiles.analyzer;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.block.tile.TileEntityRotatableBase;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TileEntitySeedAnalyzer extends TileEntityRotatableBase implements ISidedInventory, ITickable, IAgriDisplayable {

    public static final int SPECIMEN_SLOT_ID = 36;
    public static final int JOURNAL_SLOT_ID = 37;

    private static final int[] SLOTS = new int[]{
        SPECIMEN_SLOT_ID,
        JOURNAL_SLOT_ID
    };

    /**
     * The SEED that the SEED analyzer contains.
     *
     * Defaults to null, for empty.
     */
    private ItemStack specimen = null;

    /**
     * The journal that the SEED analyzer contains.
     *
     * Defaults to null, for empty.
     */
    private ItemStack journal = null;

    /**
     * The current progress of the SEED analyzer.
     */
    private int progress = 0;

    @Override
    protected void writeRotatableTileNBT(NBTTagCompound tag) {
        if (this.specimen != null && this.specimen.getItem() != null) {
            NBTTagCompound seedTag = new NBTTagCompound();
            this.specimen.writeToNBT(seedTag);
            tag.setTag(AgriNBT.SEED, seedTag);
        }
        if (this.journal != null && this.journal.getItem() != null) {
            NBTTagCompound journalTag = new NBTTagCompound();
            this.journal.writeToNBT(journalTag);
            tag.setTag(AgriItems.getInstance().JOURNAL.getUnlocalizedName(), journalTag);
        }
        tag.setInteger("progress", this.progress);
    }

    @Override
    protected void readRotatableTileNBT(NBTTagCompound tag) {
        if (tag.hasKey(AgriNBT.SEED)) {
            this.specimen = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriNBT.SEED));
        } else {
            //Not certain this is required... Unsure if networking thing?
            this.specimen = null;
        }
        if (tag.hasKey(AgriItems.getInstance().JOURNAL.getUnlocalizedName())) {
            this.journal = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriItems.getInstance().JOURNAL.getUnlocalizedName()));
        } else {
            this.journal = null;
        }
        this.progress = tag.getInteger("progress");
    }

    /**
     * Determines if the SEED analyzer contains a SEED or trowel in its analyze
     * slot. A null check on {@link #getSpecimen()} should return the same.
     *
     * @return if a SEED or trowel is present.
     */
    public final boolean hasSpecimen() {
        return this.hasSeed();
    }

    /**
     * Retrieves the item in the analyzer's analyze slot. (Does not remove). May
     * be either a SEED or a trowel.
     *
     * @return the item in the analyze slot.
     */
    public final ItemStack getSpecimen() {
        return this.specimen;
    }

    /**
     * Determines if the analyzer has a <em>valid</em> SEED in its analyze slot.
     *
     * @return if the analyze slot contains a <em>valid</em> SEED.
     */
    public final boolean hasSeed() {
        return SeedRegistry.getInstance().hasAdapter(specimen);
    }

    public final void setProgress(int value) {
        this.progress = value;
    }

    public final int getProgress() {
        return this.progress;
    }

    /**
     * Calculates the number of ticks it takes to analyze the SEED.
     *
     * @return ticks to analyze SEED.
     */
    public final int maxProgress() {
        return 100;
    }

    /**
     * Determines if a stack is valid for analyzation.
     *
     * @param stack the stack to check.
     * @return if the stack is valid.
     */
    public static boolean isValid(ItemStack stack) {
        return SeedRegistry.getInstance().hasAdapter(stack);
    }

    /**
     * Determines if a contained specimen has already been ANALYZED.
     *
     * @return if the specimen has been ANALYZED.
     */
    public final boolean isSpecimenAnalyzed() {
        if (this.specimen != null) {
            Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(specimen);
            return seed.isPresent() && seed.get().getStat().isAnalyzed();
        }
        return false;
    }

    /**
     * Called every tick.
     *
     * Used to update the progress counter.
     */
    @Override
    public void update() {
        boolean change = false;
        if (this.isAnalyzing()) {
            //increment progress counter
            this.progress = progress < this.maxProgress() ? progress + 1 : this.maxProgress();
            //if progress is complete analyze the SEED
            if (progress == this.maxProgress() && !worldObj.isRemote) {
                this.analyze();
                change = true;
            }
        }
        if (change) {
            this.markForUpdate();
            this.worldObj.addBlockEvent(this.getPos(), this.worldObj.getBlockState(getPos()).getBlock(), 0, 0);
            this.worldObj.notifyBlockOfStateChange(getPos(), this.getBlockType());
        }
    }

    /**
     * Analyzes the current SEED.
     *
     * Marked for cleanup.
     */
    public void analyze() {
        //analyze the SEED
        final Optional<AgriSeed> wrapper = SeedRegistry.getInstance().valueOf(specimen);
        if (wrapper.isPresent()) {
            AgriSeed seed = wrapper.get();
            seed = seed.withStat(seed.getStat().withAnalyzed(true));
            seed.getStat().writeToNBT(StackHelper.getTag(specimen));
            if (this.hasJournal()) {
                ((ItemJournal) journal.getItem()).addEntry(journal, seed.getPlant());
            }
        }
    }

    /**
     * Checks if the analyzer is analyzing.
     *
     * @return if the analyzer is analyzing.
     */
    public final boolean isAnalyzing() {
        return this.specimen != null && !this.isSpecimenAnalyzed() && progress < maxProgress();
    }

    /**
     * checks if there is a journal in the analyzer.
     *
     * @return if the analyzer contains a journal.
     */
    public final boolean hasJournal() {
        return (this.journal != null && this.journal.getItem() != null);
    }

    /**
     * Retrieves the journal from the analyzer. (Does not remove).
     *
     * @return the journal from the analyzer.
     */
    public final ItemStack getJournal() {
        return this.journal;
    }

    /**
     * Returns the scaled progress percentage. Rounds the progress up.
     *
     * @param scale ???
     * @return the scaled progress percentage.
     */
    public final int getProgressScaled(int scale) {
        return Math.round(((float) this.progress * scale) / ((float) this.maxProgress()));
    }

    //Inventory methods
    //-----------------
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    //check if item can be inserted
    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                return isValid(stack);
            case JOURNAL_SLOT_ID:
                return this.journal == null && this.isItemValidForSlot(slot, stack);
            default:
                return false;
        }
    }

    //check if an item can be extracted
    @Override
    public boolean canExtractItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
        if (slot == SPECIMEN_SLOT_ID && this.specimen != null && this.specimen.hasTagCompound()) {
            return this.isSpecimenAnalyzed();
        }
        return false;
    }

    //returns the INVENTORY SIZE
    @Override
    public int getSizeInventory() {
        return 2;
    }

    //returns the stack in the slot
    @Override
    public ItemStack getStackInSlot(int slot) {
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                return this.specimen;
            case JOURNAL_SLOT_ID:
                return this.journal;
            default:
                return null;
        }
    }

    //decreases the stack in a slot by an amount and returns that amount as an itemstack
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack output = null;
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                if (this.specimen != null) {
                    if (amount < this.specimen.stackSize) {
                        output = this.specimen.splitStack(amount);
                    } else {
                        output = this.specimen.copy();
                        this.specimen = null;
                        this.markForUpdate();
                    }
                }
                break;
            case JOURNAL_SLOT_ID:
                if (this.journal != null) {
                    output = this.journal.copy();
                    this.journal = null;
                    this.markForUpdate();
                }
                break;
        }
        this.progress = 0;
        return output;
    }

    //gets item stack in the slot when closing the INVENTORY
    @Override
    public ItemStack removeStackFromSlot(int slot) {
        ItemStack result;
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                result = this.specimen;
                this.specimen = null;
                this.progress = 0;
                break;
            case JOURNAL_SLOT_ID:
                result = this.journal;
                this.journal = null;
                break;
            default:
                return null;
        }
        this.markForUpdate();
        return result;
    }

    //sets the items in a slot to this stack
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                this.specimen = stack;
                if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                    stack.stackSize = getInventoryStackLimit();
                }
                this.progress = isSpecimenAnalyzed() ? maxProgress() : 0;
                this.markForUpdate();
                return;
            case JOURNAL_SLOT_ID:
                this.journal = stack;
                this.markForUpdate();
                return;
        }
    }

    //returns the maximum stacksize
    @Override
    public final int getInventoryStackLimit() {
        return 64;
    }

    //if this is usable by a player
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(pos) == this
                && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64.0;
    }

    /**
     * Opens the INVENTORY. (Empty method).
     *
     * @param player
     */
    @Override
    public void openInventory(EntityPlayer player) {
    }

    /**
     * Closes the INVENTORY. (Empty method).
     *
     * @param player
     */
    @Override
    public void closeInventory(EntityPlayer player) {
    }

    /**
     * Checks if a stack is valid for a slot.
     *
     * @param slot
     * @param stack
     * @return if the item is valid.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SPECIMEN_SLOT_ID:
                return TileEntitySeedAnalyzer.isValid(stack);
            case JOURNAL_SLOT_ID:
                return StackHelper.isValid(stack, ItemJournal.class);
            default:
                return false;
        }
    }

    @Override
    public String getName() {
        return "container.agricraft:seedAnalyzer";
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("container.agricraft:seedAnalyzer");
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
        this.specimen = null;
        this.journal = null;
    }

    @Override
    public void addDisplayInfo(Consumer<String> information) {
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.analyzer") + ": " + (this.hasSpecimen() ? specimen.getDisplayName() : AgriCore.getTranslator().translate("agricraft_tooltip.none")));
    }
}
