package com.infinityraider.agricraft.tiles;

import com.infinityraider.agricraft.api.v1.items.ITrowel;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.util.ITickable;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

public class TileEntitySeedAnalyzer extends TileEntityBase implements ISidedInventory, ITickable {

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
	public void writeTileNBT(NBTTagCompound tag) {
		if (this.specimen != null && this.specimen.getItem() != null) {
			NBTTagCompound seedTag = new NBTTagCompound();
			this.specimen.writeToNBT(seedTag);
			tag.setTag(AgriCraftNBT.SEED, seedTag);
		}
		if (this.journal != null && this.journal.getItem() != null) {
			NBTTagCompound journalTag = new NBTTagCompound();
			this.journal.writeToNBT(journalTag);
			tag.setTag(AgriCraftItems.journal.getUnlocalizedName(), journalTag);
		}
		tag.setInteger("progress", this.progress);
	}

	@Override
	public void readTileNBT(NBTTagCompound tag) {
		if (tag.hasKey(AgriCraftNBT.SEED)) {
			this.specimen = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriCraftNBT.SEED));
		} else {
			//Not certain this is required... Unsure if networking thing?
			this.specimen = null;
		}
		if (tag.hasKey(AgriCraftItems.journal.getUnlocalizedName())) {
			this.journal = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriCraftItems.journal.getUnlocalizedName()));
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
		return this.hasSeed() || this.hasTrowel();
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
		return CropPlantHandler.isValidSeed(this.specimen);
	}

	/**
	 * Determines if the analyzer has a trowel with a plant in its analyze slot.
	 *
	 * @return if the analyze slot contains a trowel with a plant.
	 */
	public final boolean hasTrowel() {
		if (this.specimen == null || this.specimen.getItem() == null) {
			return false;
		}
		if (this.specimen.getItem() instanceof ITrowel) {
			return ((ITrowel) specimen.getItem()).hasSeed(this.specimen);
		}
		return false;
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
		ItemStack seed;

		if (this.hasTrowel()) {
			seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
		} else {
			seed = this.specimen;
		}

		if (seed != null) {
			IAgriPlant plant = CropPlantHandler.getPlantFromStack(seed);
			return plant == null ? 0 : plant.getTier() * 20;
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
		if (stack == null || stack.getItem() == null) {
			return false;
		}
		if (stack.getItem() instanceof ITrowel) {
			return ((ITrowel) stack.getItem()).hasSeed(stack);
		}
		if (!CropPlantHandler.isValidSeed(stack)) {
			return false;
		}
		return true;
	}

	/**
	 * Determines if a contained specimen has already been ANALYZED.
	 *
	 * @return if the specimen has been ANALYZED.
	 */
	public final boolean isSpecimenAnalyzed() {
		if (this.specimen != null) {
			return new PlantStats(this.specimen).isAnalyzed();
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
			if (!this.specimen.hasTagCompound()) {
				this.specimen.setTagCompound(new NBTTagCompound());
			}
			PlantStats stats = new PlantStats(this.specimen);
			stats.analyze();
			stats.writeToNBT(this.specimen.getTagCompound());
		} else if (this.hasTrowel()) {
			((ITrowel) this.specimen.getItem()).analyze(this.specimen);
		}
		//register the SEED in the journal if there is a journal present
		if (this.hasJournal()) {
			IAgriPlant plant = CropPlantHandler.getPlantFromStack(this.hasSeed() ? this.specimen : ((ITrowel) this.specimen.getItem()).getSeed(this.specimen));
			((ItemJournal) journal.getItem()).addEntry(journal, plant);
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
	 */
	@Override
	public void openInventory(EntityPlayer player) {
	}

	/**
	 * Closes the INVENTORY. (Empty method).
	 */
	@Override
	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Checks if a stack is valid for a slot.
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

	@Override
	public boolean isRotatable() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public void addWailaInformation(List information) {
		information.add(I18n.translateToLocal("agricraft_tooltip.analyzer") + ": " + (this.hasSpecimen() ? specimen.getDisplayName() : I18n.translateToLocal("agricraft_tooltip.none")));
	}
}
