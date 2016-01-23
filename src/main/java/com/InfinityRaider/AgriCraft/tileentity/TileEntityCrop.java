package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import com.InfinityRaider.AgriCraft.api.v2.IAdditionalCropData;
import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.CrossOverResult;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationEngine;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.statstringdisplayer.StatStringDisplayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TileEntityCrop extends TileEntityAgricraft implements ICrop, IDebuggable{
    private PlantStats stats = new PlantStats();
    private boolean crossCrop=false;
    private boolean weed=false;
    private CropPlant plant;
    private IAdditionalCropData data;

    private final MutationEngine mutationEngine;

    public TileEntityCrop() {
        this.mutationEngine = new MutationEngine(this);
    }

    @Override
    public boolean isRotatable() {
        return false;
    }

    @Override
    public CropPlant getPlant() {return plant;}

    @Override
    public ISeedStats getStats() {
        return this.hasPlant()?stats.copy():new PlantStats(-1, -1, -1);
    }

    @Override
    public short getGrowth() {return this.weed ? (short)ConfigurationHandler.weedGrowthMultiplier : stats.getGrowth();} //Pardon the cast.

    @Override
    public short getGain() {return stats.getGain();}

    @Override
    public short getStrength() {return stats.getStrength();}

    @Override
    public boolean isAnalyzed() {return stats.isAnalyzed();}

    @Override
    public boolean hasWeed() {return weed;}

    @Override
    public boolean isCrossCrop() {return crossCrop;}

    @Override
    public void setCrossCrop(boolean status) {
        if(status!=this.crossCrop) {
            this.crossCrop = status;
            if(!worldObj.isRemote && crossCrop) {
                worldObj.playSoundEffect((double)((float) xCoord + 0.5F), (double)((float) yCoord + 0.5F), (double)((float) zCoord + 0.5F), net.minecraft.init.Blocks.planks.stepSound.func_150496_b(), (net.minecraft.init.Blocks.leaves.stepSound.getVolume() + 1.0F) / 2.0F, net.minecraft.init.Blocks.leaves.stepSound.getPitch() * 0.8F);
            }
            this.markForUpdate();
        }
    }

    /** get growthrate */
    public int getGrowthRate() {return this.weed ? ConfigurationHandler.weedGrowthRate : plant.getGrowthRate();}

    /** check to see if there is a plant here */
    @Override
    public boolean hasPlant() {return this.plant!=null;}

    /** check to see if a seed can be planted */
    @Override
    public boolean canPlant() {
        return !this.hasPlant() && !this.hasWeed() && !this.isCrossCrop();
    }

    /** sets the plant in the crop */
    public void setPlant(int growth, int gain, int strength, boolean analyzed, CropPlant plant) {
        if( (!this.crossCrop) && (!this.hasPlant())) {
            if(plant!=null) {
                this.plant = plant;
                this.stats = new PlantStats(growth, gain, strength, analyzed);
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 3);
                plant.onSeedPlanted(worldObj, xCoord, yCoord, zCoord);
                IAdditionalCropData data = plant.getInitialCropData(worldObj, xCoord, yCoord, zCoord, this);
                if(data != null) {
                    this.data = data;
                }
                this.markForUpdate();
            }
        }
    }

    /** sets the plant in the crop */
    @Override
    public void setPlant(int growth, int gain, int strength, boolean analyzed, Item seed, int seedMeta) {
        this.setPlant(growth, gain, strength, analyzed, CropPlantHandler.getPlantFromStack(new ItemStack(seed, 1, seedMeta)));
    }

    /** clears the plant in the crop */
    @Override
    public void clearPlant() {
        CropPlant oldPlant = getPlant();
        this.stats = new PlantStats();
        this.plant = null;
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 3);
        this.markForUpdate();
        if (oldPlant != null) {
            oldPlant.onPlantRemoved(worldObj, xCoord, yCoord, zCoord);
        }
        this.data = null;
    }

    /** check if the crop is fertile */
    @Override
    public boolean isFertile() {
        return this.weed || worldObj.isAirBlock(xCoord, yCoord +1, zCoord) && plant.getGrowthRequirement().canGrow(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    /** gets the height of the crop */
    @SideOnly(Side.CLIENT)
    public float getCropHeight() {
        return hasPlant()?plant.getHeight(getBlockMetadata()):Constants.UNIT*13;
    }

    /** check if bonemeal can be applied */
    @Override
    public boolean canBonemeal() {
        if(this.crossCrop) {
            return ConfigurationHandler.bonemealMutation;
        }
        if(this.hasPlant() && !this.isMature()) {
            if(!this.isFertile()) {
                return false;
            }
            return plant.canBonemeal();
        }
        if(this.hasWeed() && !this.isMature()) {
        	return true;
        }
        return false;
    }

    /** check the block if the plant is mature */
    @Override
    public boolean isMature() {
        return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) >= Constants.MATURE;
    }

    /** gets the fruits for this plant */
    public ArrayList<ItemStack> getFruits() {return plant.getFruitsOnHarvest(getGain(), worldObj.rand);}

    /** allow harvesting */
    public boolean allowHarvest(EntityPlayer player) {
        return hasPlant() && isMature() && plant.onHarvest(worldObj, xCoord, yCoord, zCoord, player);
    }

    /** returns an ItemStack holding the seed currently planted, initialized with an NBT tag holding the stats */
    @Override
    public ItemStack getSeedStack() {
        if(plant == null) {
            return null;
        }
        ItemStack seed = plant.getSeed().copy();
        NBTTagCompound tag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(tag, getGrowth(), getGain(), getStrength(), stats.isAnalyzed());
        seed.setTagCompound(tag);
        return seed;
    }

    /** returns the Block instance of the plant currently planted on the crop */
    @Override
    public Block getPlantBlock() {
        return plant==null?null:plant.getBlock();
    }

    /** spawns weed in the crop */
    @Override
    public void spawnWeed() {
        this.crossCrop=false;
        this.weed=true;
        this.clearPlant();
    }

    /** spread the weed */
    @Override
    public void spreadWeed() {
        List<TileEntityCrop> neighbours = this.getNeighbours();
        for(TileEntityCrop crop:neighbours) {
            if(crop!=null && (!crop.weed) && Math.random()<crop.getWeedSpawnChance()) {
                crop.spawnWeed();
                break;
            }
        }
    }

    @Override
    public void updateWeed(int growthStage) {
        if(this.hasWeed()) {
            growthStage = growthStage>7?7:growthStage<0?0:growthStage;
            if (growthStage == 0) {
                this.weed = false;
            }
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, growthStage, 3);
        }
    }

    //clear the weed
    @Override
    public void clearWeed() {updateWeed(0);}

    //weed spawn chance
    private double getWeedSpawnChance() {
        if(this.hasPlant()) {
            return ConfigurationHandler.weedsWipePlants ? ((double) (10 - getStrength())) / 10 : 0;
        }
        else {
            return this.weed ? 0 : 1;
        }
    }

    //trowel usage
    public void onTrowelUsed(ITrowel trowel, ItemStack trowelStack) {
        if(this.hasPlant()) {
            if(!trowel.hasSeed(trowelStack)) {
                trowel.setSeed(trowelStack, this.getSeedStack(), worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
                this.clearPlant();
            }
        } else if(!this.hasWeed() && !this.crossCrop){
            if(trowel.hasSeed(trowelStack)) {
                ItemStack seed = trowel.getSeed(trowelStack);
                int growthStage = trowel.getGrowthStage(trowelStack);
                NBTTagCompound tag = seed.getTagCompound();
                short growth = tag.getShort(Names.NBT.growth);
                short gain = tag.getShort(Names.NBT.gain);
                short strength = tag.getShort(Names.NBT.strength);
                boolean analysed = tag.getBoolean(Names.NBT.analyzed);
                this.setPlant(growth, gain, strength, analysed, seed.getItem(), seed.getItemDamage());
                this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, growthStage, 3);
                trowel.clearSeed(trowelStack);
            }
        }
    }

    //is fertilizer allowed
    @Override
    public boolean allowFertiliser(IFertiliser fertiliser) {
        if(this.crossCrop) {
            return ConfigurationHandler.bonemealMutation && fertiliser.canTriggerMutation();
        }
        if(this.hasWeed()) {
            return true;
        }
        if(this.hasPlant()) {
            return fertiliser.isFertiliserAllowed(plant.getTier());
        }
        return false;
    }

    //when fertiliser is applied
    @Override
    public void applyFertiliser(IFertiliser fertiliser, Random rand) {
        if(fertiliser.hasSpecialBehaviour()) {
            fertiliser.onFertiliserApplied(this.getWorldObj(), this.xCoord, this.yCoord, this.zCoord, rand);
        }
        if(this.hasPlant() || this.hasWeed()) {
            ((BlockCrop) Blocks.blockCrop).func_149853_b(this.worldObj, rand, this.xCoord, this.yCoord, this.zCoord);
        }
        else if(this.isCrossCrop() && ConfigurationHandler.bonemealMutation) {
            this.crossOver();
        }
    }

    @Override
    public boolean harvest(@Nullable EntityPlayer player) {
        return ((BlockCrop) worldObj.getBlock(xCoord, yCoord, zCoord)).harvest(worldObj, xCoord, yCoord, zCoord, player, this);
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public IAdditionalCropData getAdditionalCropData() {
        return this.data;
    }

    @Override
    public void validate() {
        super.validate();
        if(this.hasPlant()) {
            plant.onValidate(worldObj, xCoord, yCoord, zCoord, this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(this.hasPlant()) {
            plant.onInvalidate(worldObj, xCoord, yCoord, zCoord, this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if(this.hasPlant()) {
            plant.onChunkUnload(worldObj, xCoord, yCoord, zCoord, this);
        }
    }

    //TileEntity is just to store data on the crop
    @Override
    public boolean canUpdate() {
        return false;
    }

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        stats.writeToNBT(tag);
        tag.setBoolean(Names.NBT.crossCrop,crossCrop);
        tag.setBoolean(Names.NBT.weed, weed);
        if(this.plant != null) {
            tag.setTag(Names.NBT.seed, CropPlantHandler.writePlantToNBT(plant));
            if(getAdditionalCropData() != null) {
                tag.setTag(Names.NBT.inventory, getAdditionalCropData().writeToNBT());
            }
        }
    }

    //this loads the saved data for the tile entity
    @Override
    @SuppressWarnings("deprecation")
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.stats = PlantStats.readFromNBT(tag);
        this.crossCrop=tag.getBoolean(Names.NBT.crossCrop);
        this.weed=tag.getBoolean(Names.NBT.weed);
        if(tag.hasKey(Names.NBT.seed) && !tag.hasKey(Names.NBT.meta)) {
            this.plant = CropPlantHandler.readPlantFromNBT(tag.getCompoundTag(Names.NBT.seed));
        }
        //This is to load NBT from crops prior to this update
        else if(tag.hasKey(Names.NBT.seed) && tag.hasKey(Names.NBT.meta)) {
            this.loadPlantFromOldVersion(tag);
        }
        else {
            this.plant=null;
        }
        if(tag.hasKey(Names.NBT.inventory) && this.plant != null) {
            this.data = plant.readCropDataFromNBT(tag.getCompoundTag(Names.NBT.inventory));
        }
    }

    //This is to load NBT from crops prior to this update
    @Deprecated
    private void loadPlantFromOldVersion(NBTTagCompound tag) {
        String name = tag.getString(Names.NBT.seed);
        Item seed = name.equalsIgnoreCase("none")?null: (Item) Item.itemRegistry.getObject(name);
        int meta = tag.getInteger(Names.NBT.meta);
        ItemStack stack = new ItemStack(seed, 1, meta);
        CropPlant plant = CropPlantHandler.getPlantFromStack(stack);
        if(plant!=null) {
            this.plant = plant;
        } else {
            LogHelper.info("Couldn't find plant for " + stack.getUnlocalizedName() + " at (" + xCoord + "," + yCoord + "," + zCoord + "), plant has been removed");
            this.clearPlant();
        }
    }

    /** Apply a growth increment */
    public void applyGrowthTick() {
        int flag = 2;
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if(hasPlant()) {
            flag = plant.onAllowedGrowthTick(worldObj, xCoord, yCoord, zCoord, meta) ? 2 : 6;
        }
        if(ConfigurationHandler.renderCropPlantsAsTESR) {
            flag = 6;
        }
        if (hasWeed() || !plant.isMature(worldObj, xCoord, yCoord, zCoord)) {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 1, flag);
            AppleCoreHelper.announceGrowthTick(this.getBlockType(), worldObj, xCoord, yCoord, zCoord);
        }
    }

    /** the code that makes the crop cross with neighboring crops */
    public void  crossOver() {mutationEngine.executeCrossOver();}

    /** Called by the mutation engine to apply the result of a cross over */
    public void applyCrossOverResult(CrossOverResult result) {
        crossCrop = false;
        setPlant(result.getGrowth(), result.getGain(), result.getStrength(), false, result.getSeed(), result.getMeta());
    }

    /**
     * @return a list with all neighbours of type <code>TileEntityCrop</code> in the
     *          NORTH, SOUTH, EAST and WEST direction
     */
    public List<TileEntityCrop> getNeighbours() {
        List<TileEntityCrop> neighbours = new ArrayList<TileEntityCrop>();
        addNeighbour(neighbours, ForgeDirection.NORTH);
        addNeighbour(neighbours, ForgeDirection.SOUTH);
        addNeighbour(neighbours, ForgeDirection.EAST);
        addNeighbour(neighbours, ForgeDirection.WEST);
        return neighbours;
    }

    private void addNeighbour(List<TileEntityCrop> neighbours, ForgeDirection direction) {
        TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
        if (te == null || !(te instanceof TileEntityCrop)) {
            return;
        }
        neighbours.add((TileEntityCrop) te);
    }

    /** @return a list with only mature neighbours of type <code>TileEntityCrop</code> */
    public List<TileEntityCrop> getMatureNeighbours() {
        List<TileEntityCrop> neighbours = getNeighbours();
        for (Iterator<TileEntityCrop> iterator = neighbours.iterator(); iterator.hasNext(); ) {
            TileEntityCrop crop = iterator.next();
            if (!crop.hasPlant() || !crop.isMature()) {
                iterator.remove();
            }
        }
        return neighbours;
    }

    //get the plant icon
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon() {
        IIcon icon = null;
        if(this.hasPlant()) {
            icon = plant.getPlantIcon(this.getBlockMetadata());
        }
        else if(this.weed) {
            icon = ((BlockCrop) this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord)).getWeedIcon(this.getBlockMetadata());
        }
        return icon;
    }

    @Override
    public void addDebugInfo(List<String> list) {
        list.add("CROP:");
        if(this.crossCrop) {
            list.add(" - This is a crosscrop");
        }
        else if(this.hasPlant()) {
            list.add(" - This crop has a plant");
            list.add(" - Seed: " + (this.plant.getSeed().getItem()).getUnlocalizedName());
            list.add(" - Seed class: "+this.plant.getSeed().getItem().getClass().getName());
            list.add(" - RegisterName: " + Item.itemRegistry.getNameForObject((this.plant.getSeed().getItem())) + ":" + this.plant.getSeed().getItemDamage());
            list.add(" - Tier: "+plant.getTier());
            list.add(" - Meta: " + this.getBlockMetadata());
            list.add(" - Growth: " + getGrowth());
            list.add(" - Gain: " + getGain());
            list.add(" - Strength: " + getStrength());
            list.add(" - Fertile: " + this.isFertile());
            list.add(" - Mature: " + this.isMature());
        }
        else if(this.weed) {
            list.add(" - This crop has weeds");
            list.add(" - Meta: " + this.getBlockMetadata());
        }
        else {
            list.add(" - This crop has no plant");
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
    	if(this.hasPlant()) {
    		//Add the seed name.
    		information.add(StatCollector.translateToLocal("agricraft_tooltip.seed") + ": " + this.getSeedStack().getDisplayName());
    		//Add the growth.
    		if(this.isMature()) {
    			information.add(StatCollector.translateToLocal("agricraft_tooltip.growthStage")+": "+StatCollector.translateToLocal("agricraft_tooltip.mature"));
    		} else {
    			information.add(StatCollector.translateToLocal("agricraft_tooltip.growthStage")+": "+((int) ( (100*(this.getBlockMetadata()+0.00F))/7.00F)+"%" ));
    		}
    		//Add the analyzed data.
    		if(this.isAnalyzed()) {
    			information.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + StatStringDisplayer.instance().getStatDisplayString(this.getGrowth(), ConfigurationHandler.cropStatCap));
                information.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + StatStringDisplayer.instance().getStatDisplayString(this.getGain(), ConfigurationHandler.cropStatCap));
    			information.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + StatStringDisplayer.instance().getStatDisplayString(this.getStrength(), ConfigurationHandler.cropStatCap));
    		}
    		else {
    			information.add(StatCollector.translateToLocal("agricraft_tooltip.analyzed"));
    		}
    		//Add the fertility information.
    		information.add(StatCollector.translateToLocal(this.isFertile()?"agricraft_tooltip.fertile":"agricraft_tooltip.notFertile"));
    	}
    	else if(this.hasWeed()) {
    		information.add(StatCollector.translateToLocal("agricraft_tooltip.weeds"));
    	}
    	else {
    		information.add(StatCollector.translateToLocal("agricraft_tooltip.empty"));
        }
    }
}
