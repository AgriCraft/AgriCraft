package com.infinityraider.agricraft.blocks.tiles.analyzer;

import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.infinitylib.block.tile.TileEntityRotatableBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.util.ITickable;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.utility.StackHelper;

public class TileEntitySeedAnalyzer extends TileEntityRotatableBase implements ISidedInventory, ITickable {

	private static final int[] SLOTS = new int[]{0, 1};

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
		if (this.specimen != null) {
			AgriSeed seed = SeedRegistry.getInstance().getValue(specimen);
			return seed == null ? 0 : seed.getPlant().getTier() * 20;
		} else {
			return 0;
		}
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
			AgriSeed seed = SeedRegistry.getInstance().getValue(specimen);
			return seed != null && seed.getStat().isAnalyzed();
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
		if (this.hasSeed()) {
			AgriSeed seed = SeedRegistry.getInstance().getValue(specimen);
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
		slot = slot % 2;
		if (slot == 0) {
			return isValid(stack);
		} else if (slot == 1) {
			return (this.journal == null && this.isItemValidForSlot(slot, stack));
		}
		return false;
	}

	//check if an item can be extracted
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
		slot = slot % 2;
		if (slot == 0 && this.specimen != null && this.specimen.hasTagCompound()) {
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
		slot = slot % 2;
		switch (slot) {
			case 0:
				return this.specimen;
			case 1:
				return this.journal;
			default:
				return null;
		}
	}

	//decreases the stack in a slot by an amount and returns that amount as an itemstack
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		slot = slot % 2;
		ItemStack output = null;
		if (slot == 0 && this.specimen != null) {
			if (amount < this.specimen.stackSize) {
				output = this.specimen.splitStack(amount);
			} else {
				output = this.specimen.copy();
				this.specimen = null;
				this.markForUpdate();
			}
		} else if (slot == 1 && this.journal != null) {
			output = this.journal.copy();
			this.journal = null;
			this.markForUpdate();
		}
		this.progress = 0;
		return output;
	}

	//gets item stack in the slot when closing the INVENTORY
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		slot = slot % 2;
		ItemStack stackInSlot;
		switch (slot) {
			case 0:
				stackInSlot = this.specimen;
				break;
			case 1:
				stackInSlot = this.journal;
				break;
			default:
				return null;
		}
		if (stackInSlot != null) {
			setInventorySlotContents(slot, null);
		}
		return stackInSlot;
	}

	//sets the items in a slot to this stack
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		slot = slot % 2;
		if (slot == 0) {
			this.specimen = stack;
			if (stack != null && stack.stackSize > getInventoryStackLimit()) {
				stack.stackSize = getInventoryStackLimit();
			}
			progress = 0;
		} else if (slot == 1) {
			this.journal = stack;
		}
		this.markForUpdate();
	}

	//returns the maximum stacksize
	@Override
	public final int getInventoryStackLimit() {
		return 64;
	}

	//if this is usable by a player
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
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
		slot = slot % 2;
		switch (slot) {
			case 0:
				return TileEntitySeedAnalyzer.isValid(stack);
			case 1:
				return (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemJournal);
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

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addDisplayInfo(List information) {
		information.add(AgriCore.getTranslator().translate("agricraft_tooltip.analyzer") + ": " + (this.hasSpecimen() ? specimen.getDisplayName() : AgriCore.getTranslator().translate("agricraft_tooltip.none")));
	}
}
