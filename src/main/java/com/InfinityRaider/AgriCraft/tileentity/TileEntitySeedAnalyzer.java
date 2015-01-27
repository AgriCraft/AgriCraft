package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySeedAnalyzer extends TileEntityAgricraft implements ISidedInventory {
    public ItemStack seed = null;
    public ItemStack journal = null;
    public int progress = 0;
    public ForgeDirection direction;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.seed!=null && this.seed.getItem()!=null) {
            NBTTagCompound seedTag = new NBTTagCompound();
            this.seed.writeToNBT(seedTag);
            tag.setTag(Names.Objects.seed, seedTag);
        }
        if(this.journal!=null && this.journal.getItem()!=null) {
            NBTTagCompound journalTag = new NBTTagCompound();
            this.journal.writeToNBT(journalTag);
            tag.setTag(Names.Objects.journal, journalTag);
        }
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        tag.setInteger("progress", this.progress);
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey(Names.Objects.seed)) {
            this.seed = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.seed));
        }
        else {
            this.seed = null;
        }
        if(tag.hasKey(Names.Objects.journal)) {
            this.journal = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.Objects.journal));
        }
        else {
            this.journal = null;
        }
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        this.progress = tag.getInteger("progress");
        super.readFromNBT(tag);
    }

    //sets the direction based on an int
    public void setDirection(int direction) {
        this.direction = ForgeDirection.getOrientation(direction);
    }

    //returns the seed currently being processed
    public boolean hasSeed() {
        return this.seed!=null && this.seed.getItem() instanceof ItemSeeds;
    }

    //calculates the number of ticks it takes to analyze the seed
    public int maxProgress() {
        return seed==null || !(seed.getItem() instanceof ItemSeeds)?0: SeedHelper.getSeedTier((ItemSeeds) seed.getItem(), seed.getItemDamage())*20;
    }

    public static boolean isValid(ItemStack stack) {
        if(stack!=null && stack.getItem() instanceof ItemSeeds) {
            if(!SeedHelper.isValidSeed((ItemSeeds) stack.getItem(), stack.getItemDamage())) {
                return false;
            }
            if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey(Names.NBT.analyzed)) {
                return !stack.stackTagCompound.getBoolean(Names.NBT.analyzed);
            }
            else {
                return true;
            }
        }
        return false;
    }

    //gets called every tick
    @Override
    public void updateEntity() {
        boolean change = false;
        if(!this.worldObj.isRemote && this.isAnalyzing()) {
            //increment progress counter
            this.progress=progress<this.maxProgress()?progress+1:this.maxProgress();
            //if progress is complete analyze the seed
            if(progress == this.maxProgress()) {
                this.analyze();
            }
            change = true;
        }
        if(change) {
            this.markDirtyAndMarkForUpdate();
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), 0, 0);
            this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        }
    }

    //analyzes the current seed
    public void analyze() {
        //analyze the seed
        if(this.seed.hasTagCompound()) {
            NBTTagCompound tag = this.seed.getTagCompound();
            if(tag.hasKey(Names.NBT.growth) && tag.hasKey(Names.NBT.gain) && tag.hasKey(Names.NBT.strength)) {
                tag.setBoolean(Names.NBT.analyzed, true);
            } else {
                SeedHelper.setNBT(tag, (short) 0, (short) 0, (short) 0, true);
            }
        }
        else {
            this.seed.setTagCompound(new NBTTagCompound());
            SeedHelper.setNBT(this.seed.stackTagCompound, (short) 0, (short) 0, (short) 0, true);
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
            }
            else {
                list = new NBTTagList();
            }
            //add the analyzed seed to the NBT tag list if it doesn't already have it
            if(!NBTHelper.listContainsStack(list, this.seed)) {
                NBTTagCompound seedTag = new NBTTagCompound();
                ItemStack write = this.seed.copy();
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
        if(this.hasSeed() && this.seed.hasTagCompound() && this.seed.stackTagCompound.hasKey(Names.NBT.analyzed)) {
            return (this.progress<this.maxProgress()) && !this.seed.stackTagCompound.getBoolean(Names.NBT.analyzed);
        }
        else {
            return this.hasSeed() && progress < maxProgress();
        }
    }

    //checks if there is a journal in the analyzer
    public boolean hasJournal() {
        return (this.journal!=null && this.journal.getItem()!=null);
    }

    //returns the scaled progress percentage
    public int getProgressScaled(int scale) {
        return (int) Math.round(((float) this.progress*scale)/((float) this.maxProgress()));
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
            if (stack.getItem() instanceof ItemSeeds) {
                if (!SeedHelper.isValidSeed((ItemSeeds) stack.getItem(), stack.getItemDamage())) {
                    return false;
                }
                return (this.seed == null || this.canStack(stack)) && this.isItemValidForSlot(slot, stack);
            }
        }
        else if(slot==ContainerSeedAnalyzer.journalSlotId) {
            return (this.journal==null && this.isItemValidForSlot(slot, stack));
        }
        return false;
    }

    //check if an item can be extracted
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if(slot==ContainerSeedAnalyzer.seedSlotId &&this.seed !=null && this.seed.hasTagCompound()) {
            NBTTagCompound tag = this.seed.getTagCompound();
            return tag.hasKey("analyzed") && tag.getBoolean("analyzed");
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
            case ContainerSeedAnalyzer.seedSlotId: return this.seed;
            case ContainerSeedAnalyzer.journalSlotId: return this.journal;
            default: return null;
        }
    }

    //decreases the stack in a slot by an amount and returns that amount as an itemstack
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack output = null;
        if(slot==ContainerSeedAnalyzer.seedSlotId && this.seed!=null) {
            if(amount<this.seed.stackSize) {
                output = this.seed.splitStack(amount);
            } else {
                output = this.seed.copy();
                this.seed = null;
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
            case ContainerSeedAnalyzer.seedSlotId: stackInSlot = this.seed; break;
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
            this.seed = stack;
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
        return this.seed.getItem()==stack.getItem()&&this.seed.getItemDamage()==stack.getItemDamage()&&this.seed.stackTagCompound==stack.stackTagCompound&&(this.seed.stackSize+stack.stackSize<=64);
    }
}
