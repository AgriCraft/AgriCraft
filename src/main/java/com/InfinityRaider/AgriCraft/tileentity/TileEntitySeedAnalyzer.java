package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TileEntitySeedAnalyzer extends TileEntityAgricraft implements ISidedInventory {
    private static final int[] SLOTS = new int[] {0, 1};
	
    /**
     * The seed that the seed analyzer contains.
     * 
     * Defaults to null, for empty.
     */
    private ItemStack specimen = null;
    
    /**
     * The journal that the seed analyzer contains.
     * 
     * Defaults to null, for empty.
     */
    private ItemStack journal = null;
    
    /**
     * The current progress of the seed analyzer.
     */
    private int progress = 0;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
    	//Mandatory call to super().
        super.writeToNBT(tag);
        if(this.specimen !=null && this.specimen.getItem()!=null) {
            NBTTagCompound seedTag = new NBTTagCompound();
            this.specimen.writeToNBT(seedTag);
            tag.setTag(Names.Objects.seed, seedTag);
        }
        if(this.journal!=null && this.journal.getItem()!=null) {
            NBTTagCompound journalTag = new NBTTagCompound();
            this.journal.writeToNBT(journalTag);
            tag.setTag(Names.Objects.journal, journalTag);
        }
        tag.setInteger("progress", this.progress);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
    	//Mandatory call to super().
        super.readFromNBT(tag);
        if(tag.hasKey(Names.Objects.seed)) {
            this.specimen = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.seed));
        }
        else {
        	//Not certain this is required... Unsure if networking thing?
            this.specimen = null;
        }
        if(tag.hasKey(Names.Objects.journal)) {
            this.journal = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.journal));
        }
        else {
            this.journal = null;
        }
        this.progress = tag.getInteger("progress");
    }

    /**
     * Determines if the seed analyzer contains a seed or trowel in its analyze slot.
     * A null check on {@link #getSpecimen()} should return the same.
     * 
     * @return if a seed or trowel is present.
     */
    public final boolean hasSpecimen() {
        return this.hasSeed() || this.hasTrowel();
    }

    /**
     * Retrieves the item in the analyzer's analyze slot. (Does not remove).
     * May be either a seed or a trowel.
     * 
     * @return the item in the analyze slot.
     */
    public final ItemStack getSpecimen() {
        return this.specimen;
    }

    /**
     * Determines if the analyzer has a <em>valid</em> seed in its analyze slot.
     * 
     * @return if the analyze slot contains a <em>valid</em> seed.
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
        if(this.specimen==null || this.specimen.getItem()==null) {
            return false;
        }
        if(this.specimen.getItem() instanceof ITrowel) {
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
     * Calculates the number of ticks it takes to analyze the seed.
     * 
     * @return ticks to analyze seed.
     */
    public final int maxProgress() {
        ItemStack seed;
        
        if(this.hasTrowel()) {
            seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
        } else {
        	seed = this.specimen;
        }
        
        if (seed != null) {
            CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        	return plant==null?0:plant.getTier()*20;
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
        if(stack==null || stack.getItem()==null) {
            return false;
        }
        if(stack.getItem() instanceof ITrowel) {
            return ((ITrowel) stack.getItem()).hasSeed(stack);
        }
        if (!CropPlantHandler.isValidSeed(stack)) {
            return false;
        }
        return true;
    }

    /**
     * Determines if a contained specimen has already been analyzed.
     * 
     * @return if the specimen has been analyzed.
     */
    public final boolean isSpecimenAnalyzed() {
        if(this.hasTrowel()) {
            return ((ITrowel) this.specimen.getItem()).isSeedAnalysed(this.specimen);
        }
        if(this.hasSeed()) {
            return this.specimen.hasTagCompound() && this.specimen.stackTagCompound.getBoolean(Names.NBT.analyzed);
        }
        return false;
    }

    /**
     * Called every tick.
     * 
     * Used to update the progress counter.
     */
    @Override
    public void updateEntity() {
        boolean change = false;
        if(this.isAnalyzing()) {
            //increment progress counter
            this.progress=progress<this.maxProgress()?progress+1:this.maxProgress();
            //if progress is complete analyze the seed
            if(progress == this.maxProgress() && !worldObj.isRemote) {
                this.analyze();
                change = true;
            }
        }
        if(change) {
            this.markForUpdate();
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), 0, 0);
            this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        }
    }

    /**
     * Analyzes the current seed.
     * 
     * Marked for cleanup.
     */
    public void analyze() {
        //analyze the seed
        if(this.hasSeed()) {
            if (this.specimen.hasTagCompound()) {
                NBTTagCompound tag = this.specimen.getTagCompound();
                if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                    tag.setBoolean(Names.NBT.analyzed, true);
                } else {
                    CropPlantHandler.setSeedNBT(tag, (short) 0, (short) 0, (short) 0, true);
                }
            } else {
                this.specimen.setTagCompound(new NBTTagCompound());
                CropPlantHandler.setSeedNBT(this.specimen.stackTagCompound, (short) 0, (short) 0, (short) 0, true);
            }
        }
        else if(this.hasTrowel()) {
            ((ITrowel) this.specimen.getItem()).analyze(this.specimen);
        }
        //register the seed in the journal if there is a journal present
        if(this.hasJournal()) {
            ((ItemJournal) journal.getItem()).addEntry(journal, this.hasSeed() ? this.specimen : ((ITrowel) this.specimen.getItem()).getSeed(this.specimen));
        }
    }

    /**
     * Checks if the analyzer is analyzing.
     * 
     * @return if the analyzer is analyzing.
     */
    public final boolean isAnalyzing() {
        return this.specimen!=null && !this.isSpecimenAnalyzed() && progress < maxProgress();
    }

    /**
     * checks if there is a journal in the analyzer.
     * 
     * @return if the analyzer contains a journal.
     */
    public final boolean hasJournal() {
        return (this.journal!=null && this.journal.getItem()!=null);
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
     * Returns the scaled progress percentage.
     * Rounds the progress up.
     * 
     * @param scale ???
     * @return the scaled progress percentage.
     */
    public final int getProgressScaled(int scale) {
        return Math.round(((float) this.progress*scale)/((float) this.maxProgress()));
    }


    //Inventory methods
    //-----------------
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    //check if item can be inserted
    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        slot = slot%2;
        if(slot == 0) {
            return isValid(stack);
        }
        else if(slot == 1) {
            return (this.journal==null && this.isItemValidForSlot(slot, stack));
        }
        return false;
    }

    //check if an item can be extracted
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        slot = slot%2;
        if(slot == 0 && this.specimen != null && this.specimen.hasTagCompound()) {
            return this.isSpecimenAnalyzed();
        }
        return false;
    }

    //returns the inventory size
    @Override
    public int getSizeInventory() {
        return 2;
    }

    //returns the stack in the slot
    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = slot%2;
        switch(slot) {
            case 0: return this.specimen;
            case 1: return this.journal;
            default: return null;
        }
    }

    //decreases the stack in a slot by an amount and returns that amount as an itemstack
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        slot = slot%2;
        ItemStack output = null;
        if(slot == 0 && this.specimen != null) {
            if(amount<this.specimen.stackSize) {
                output = this.specimen.splitStack(amount);
            } else {
                output = this.specimen.copy();
                this.specimen = null;
                this.markForUpdate();
            }
        }
        else if(slot == 1 && this.journal != null) {
            output = this.journal.copy();
            this.journal = null;
            this.markForUpdate();
        }
        this.progress = 0;
        return output;
    }

    //gets item stack in the slot when closing the inventory
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        slot = slot%2;
        ItemStack stackInSlot;
        switch(slot) {
            case 0: stackInSlot = this.specimen; break;
            case 1: stackInSlot = this.journal; break;
            default: return null;
        }
        if(stackInSlot!=null) {
            setInventorySlotContents(slot, null);
        }
        return stackInSlot;
    }

    //sets the items in a slot to this stack
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        slot = slot%2;
        if(slot == 0) {
            this.specimen = stack;
            if(stack!=null && stack.stackSize>getInventoryStackLimit()) {
                stack.stackSize = getInventoryStackLimit();
            }
            progress = 0;
        }
        else if(slot == 1) {
            this.journal = stack;
        }
        this.markForUpdate();
    }

    //returns the unlocalized inventory name
    @Override
    public String getInventoryName() {
        return "container.agricraft:seedAnalyzer";
    }

    //if this has a custom inventory name
    @Override
    public final boolean hasCustomInventoryName() {
        return true;
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
     * Opens the inventory. (Empty method).
     */
    @Override
    public void openInventory() {

    }

    /**
     * Closes the inventory. (Empty method).
     */
    @Override
    public void closeInventory() {

    }

    /**
     * Checks if a stack is valid for a slot.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        slot = slot%2;
        switch (slot) {
            case 0: return TileEntitySeedAnalyzer.isValid(stack);
            case 1: return (stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemJournal);
            default: return false;
        }
    }

    @Override
    public boolean isRotatable() {
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
    	information.add(StatCollector.translateToLocal("agricraft_tooltip.analyzer")+": "+(this.hasSpecimen()?specimen.getDisplayName():StatCollector.translateToLocal("agricraft_tooltip.none")));
    }
}
