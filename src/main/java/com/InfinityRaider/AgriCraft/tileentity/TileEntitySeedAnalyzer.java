package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySeedAnalyzer extends TileEntityAgricraft implements ISidedInventory {
    private ItemStack specimen = null;
    private ItemStack journal = null;
    private int progress = 0;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
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

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.Objects.seed)) {
            this.specimen = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.seed));
        }
        else {
            this.specimen = null;
        }
        if(tag.hasKey(Names.Objects.journal)) {
            this.journal = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.journal));
        }
        else {
            this.journal = null;
        }
        if(tag.hasKey("direction")) {
            this.setOrientation(tag.getByte("direction"));
        }
        this.progress = tag.getInteger("progress");
    }

    //returns true if a seed currently being processed
    public boolean hasSeed() {
        return CropPlantHandler.isValidSeed(this.specimen);
    }

    //returns true if a trowel with a plant is currently being processed
    public boolean hasTrowel() {
        if(this.specimen==null || this.specimen.getItem()==null) {
            return false;
        }
        if(this.specimen.getItem() instanceof ITrowel) {
            return ((ITrowel) specimen.getItem()).hasSeed(this.specimen);
        }
        return false;
    }

    public void setProgress(int value) {
        this.progress = value;
    }

    public int getProgress() {
        return this.progress;
    }

    //calculates the number of ticks it takes to analyze the seed
    public int maxProgress() {
        ItemStack seed = this.specimen;
        if(this.hasTrowel()) {
            seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
        }
        return seed==null?0:CropPlantHandler.getPlantFromStack(seed).getTier()*20;
    }

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

    public boolean isSpecimenAnalysed() {
        if(this.hasTrowel()) {
            return ((ITrowel) this.specimen.getItem()).isSeedAnalysed(this.specimen);
        }
        if(this.hasSeed()) {
            return this.specimen.hasTagCompound() && this.specimen.stackTagCompound.getBoolean(Names.NBT.analyzed);
        }
        return false;
    }

    //gets called every tick
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

    //analyzes the current seed
    public void analyze() {
        //analyze the seed
        if(this.hasSeed()) {
            if (this.specimen.hasTagCompound()) {
                NBTTagCompound tag = this.specimen.getTagCompound();
                if (tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                    tag.setBoolean(Names.NBT.analyzed, true);
                } else {
                    SeedHelper.setNBT(tag, (short) 0, (short) 0, (short) 0, true);
                }
            } else {
                this.specimen.setTagCompound(new NBTTagCompound());
                SeedHelper.setNBT(this.specimen.stackTagCompound, (short) 0, (short) 0, (short) 0, true);
            }
        }
        else if(this.hasTrowel()) {
            ((ITrowel) this.specimen.getItem()).analyze(this.specimen);
        }
        //register the seed in the journal if there is a journal present
        if(this.hasJournal()) {
            //check if the journal has NBT and if it doesn't, create a new one
            if(!this.journal.hasTagCompound()) {
                this.journal.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound tag = this.journal.stackTagCompound;
            //check if the NBT tag has a list of discovered seeds and if it doesn't, create a new one
            NBTTagList list;
            if(tag.hasKey(Names.NBT.discoveredSeeds)) {
                list = tag.getTagList(Names.NBT.discoveredSeeds, 10);
                NBTHelper.clearEmptyStacksFromNBT(list);
            }
            else {
                list = new NBTTagList();
            }
            //add the analyzed seed to the NBT tag list if it doesn't already have it
            ItemStack newEntry = this.hasSeed()?this.specimen:((ITrowel) this.specimen.getItem()).getSeed(this.specimen);
            if(!NBTHelper.listContainsStack(list, newEntry)) {
                NBTTagCompound seedTag = new NBTTagCompound();
                ItemStack write = newEntry.copy();
                write.stackSize = 1;
                write.stackTagCompound = null;
                write.writeToNBT(seedTag);
                list.appendTag(seedTag);
            }
            NBTHelper.sortStacks(list);
            //add the NBT tag to the journal
            tag.setTag(Names.NBT.discoveredSeeds, list);
        }
    }

    //checks if the analyzer is analyzing
    public boolean isAnalyzing() {
        return this.specimen!=null && !this.isSpecimenAnalysed() && progress < maxProgress();
    }

    //checks if there is a journal in the analyzer
    public boolean hasJournal() {
        return (this.journal!=null && this.journal.getItem()!=null);
    }

    //returns the scaled progress percentage
    public int getProgressScaled(int scale) {
        return Math.round(((float) this.progress*scale)/((float) this.maxProgress()));
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        this.worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        return true;
    }


    //Inventory methods
    //-----------------
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[]{ContainerSeedAnalyzer.seedSlotId,ContainerSeedAnalyzer.journalSlotId};
    }

    //check if item can be inserted
    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if(slot==ContainerSeedAnalyzer.seedSlotId) {
            return isValid(stack);
        }
        else if(slot==ContainerSeedAnalyzer.journalSlotId) {
            return (this.journal==null && this.isItemValidForSlot(slot, stack));
        }
        return false;
    }

    //check if an item can be extracted
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if(slot==ContainerSeedAnalyzer.seedSlotId &&this.specimen !=null && this.specimen.hasTagCompound()) {
            return this.isSpecimenAnalysed();
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
        switch(slot) {
            case ContainerSeedAnalyzer.seedSlotId: return this.specimen;
            case ContainerSeedAnalyzer.journalSlotId: return this.journal;
            default: return null;
        }
    }

    //decreases the stack in a slot by an amount and returns that amount as an itemstack
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack output = null;
        if(slot==ContainerSeedAnalyzer.seedSlotId && this.specimen !=null) {
            if(amount<this.specimen.stackSize) {
                output = this.specimen.splitStack(amount);
            } else {
                output = this.specimen.copy();
                this.specimen = null;
            }
        }
        else if(slot==ContainerSeedAnalyzer.journalSlotId && this.journal!=null) {
            output = this.journal.copy();
            this.journal = null;
        }
        this.progress = 0;
        return output;
    }

    //gets item stack in the slot when closing the inventory
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stackInSlot;
        switch(slot) {
            case ContainerSeedAnalyzer.seedSlotId: stackInSlot = this.specimen; break;
            case ContainerSeedAnalyzer.journalSlotId: stackInSlot = this.journal; break;
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
        if(slot==ContainerSeedAnalyzer.seedSlotId) {
            this.specimen = stack;
            if(stack!=null && stack.stackSize>getInventoryStackLimit()) {
                stack.stackSize = getInventoryStackLimit();
            }
            progress = 0;
        }
        else if(slot==ContainerSeedAnalyzer.journalSlotId) {
            this.journal = stack;
        }
    }

    //returns the unlocalized inventory name
    @Override
    public String getInventoryName() {
        return "container.agricraft:seedAnalyzer";
    }

    //if this has a custom inventory name
    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    //returns the maximum stacksize
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    //if this is usable by a player
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    //opens the inventory
    @Override
    public void openInventory() {

    }

    //closes the inventory
    @Override
    public void closeInventory() {

    }

    //checks if a stack is valid for a slot
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case ContainerSeedAnalyzer.seedSlotId: return TileEntitySeedAnalyzer.isValid(stack);
            case ContainerSeedAnalyzer.journalSlotId: return (stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemJournal);
            default: return false;
        }
    }

    //check if a stack can stack with the current seed stack
    public boolean canStack(ItemStack stack) {
        return this.specimen.getItem()==stack.getItem()&&this.specimen.getItemDamage()==stack.getItemDamage()&&this.specimen.stackTagCompound==stack.stackTagCompound&&(this.specimen.stackSize+stack.stackSize<=64);
    }

    @Override
    public boolean isRotatable() {
        return true;
    }
}
