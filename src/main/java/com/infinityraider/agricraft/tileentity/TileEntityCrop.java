package com.infinityraider.agricraft.tileentity;

import com.infinityraider.agricraft.api.v1.IDebuggable;
import com.infinityraider.agricraft.api.v1.IFertiliser;
import com.infinityraider.agricraft.api.v1.IAdditionalCropData;
import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.api.v1.ICrop;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.CrossOverResult;
import com.infinityraider.agricraft.farming.mutation.MutationEngine;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.statstringdisplayer.StatStringDisplayer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;

public class TileEntityCrop extends TileEntityBase implements ICrop, IDebuggable{
	
	public static final String NAME = "crops";
	
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
    public CropPlant getPlant() {return this.hasPlant() ? plant : null;}

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
                worldObj.playSoundEffect((double)((float) xCoord() + 0.5F), (double)((float) yCoord() + 0.5F), (double)((float) zCoord() + 0.5F), net.minecraft.init.Blocks.planks.stepSound.soundName, (net.minecraft.init.Blocks.leaves.stepSound.getVolume() + 1.0F) / 2.0F, net.minecraft.init.Blocks.leaves.stepSound.getFrequency() * 0.8F);
            }
            this.markForUpdate();
        }
    }

    /** get growthrate */
    public int getGrowthRate() {return this.weed ? ConfigurationHandler.weedGrowthRate : plant.getGrowthRate();}

    /** check to see if there is a plant here */
    @Override
    public boolean hasPlant() {return this.plant!=null;}

    @Override
    public int getGrowthStage() {
        return worldObj.getBlockState(getPos()).getValue(AgriCraftProperties.GROWTHSTAGE);
    }

    @Override
    public void setGrowthStage(int stage) {
        if(this.hasPlant() || this.hasWeed()) {
            stage &= Constants.MATURE;
            IBlockState state = worldObj.getBlockState(pos);
            state.withProperty(AgriCraftProperties.GROWTHSTAGE, stage);
            this.worldObj.setBlockState(pos, state, 3);
        }
    }

    /** check to see if a SEED can be planted */
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
                this.setGrowthStage(0);
                plant.onSeedPlanted(worldObj, pos);
                IAdditionalCropData data = plant.getInitialCropData(worldObj, getPos(), this);
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
        this.setGrowthStage(0);
        this.stats = new PlantStats();
        this.plant = null;
        this.markForUpdate();
        if (oldPlant != null) {
            oldPlant.onPlantRemoved(worldObj, pos);
        }
        this.data = null;
    }

    /** check if the crop is fertile */
    @Override
    public boolean isFertile() {
        return this.weed || worldObj.isAirBlock(this.getPos().add(0, 1, 0)) && plant.getGrowthRequirement().canGrow(this.worldObj, pos);
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
        return getGrowthStage() >= Constants.MATURE;
    }

    /** gets the fruits for this plant */
    public ArrayList<ItemStack> getFruits() {return plant.getFruitsOnHarvest(getGain(), worldObj.rand);}

    /** allow harvesting */
    public boolean allowHarvest(EntityPlayer player) {
        return hasPlant() && isMature() && plant.onHarvest(worldObj, pos, worldObj.getBlockState(getPos()), player);
    }

    /** returns an ItemStack holding the SEED currently planted, initialized with an AgriCraftNBT TAG holding the stats */
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

    @Override
    public BlockState getPlantBlockState() {
        return plant==null?null:new BlockState(plant.getBlock());
    }

    /** spawns WEED in the crop */
    @Override
    public void spawnWeed() {
        this.crossCrop=false;
        this.weed=true;
        this.clearPlant();
    }

    /** spread the WEED */
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
            this.setGrowthStage(growthStage);
            if (growthStage == 0) {
                this.weed = false;
            }
            markForUpdate();
        }
    }

    //clear the WEED
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
                trowel.setSeed(trowelStack, this.getSeedStack(), getGrowthStage());
                this.clearPlant();
            }
        } else if(!this.hasWeed() && !this.crossCrop){
            if(trowel.hasSeed(trowelStack)) {
                ItemStack seed = trowel.getSeed(trowelStack);
                int growthStage = trowel.getGrowthStage(trowelStack);
                NBTTagCompound tag = seed.getTagCompound();
                short growth = tag.getShort(AgriCraftNBT.GROWTH);
                short gain = tag.getShort(AgriCraftNBT.GAIN);
                short strength = tag.getShort(AgriCraftNBT.STRENGTH);
                boolean analysed = tag.getBoolean(AgriCraftNBT.ANALYZED);
                this.setPlant(growth, gain, strength, analysed, seed.getItem(), seed.getItemDamage());
                this.setGrowthStage(growthStage);
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
            fertiliser.onFertiliserApplied(getWorld(), getPos(), rand);
        }
        if(this.hasPlant() || this.hasWeed()) {
            ((BlockCrop) AgriCraftBlocks.blockCrop).grow(getWorld(), rand, getPos(), getWorld().getBlockState(getPos()));
        }
        else if(this.isCrossCrop() && ConfigurationHandler.bonemealMutation) {
            this.crossOver();
        }
    }

    @Override
    public boolean harvest(@Nullable EntityPlayer player) {
        return ((BlockCrop) getWorld().getBlockState(pos).getBlock()).harvest(getWorld(), getPos(), getWorld().getBlockState(getPos()), player, this);
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
            plant.onValidate(worldObj, pos, this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(this.hasPlant()) {
            plant.onInvalidate(worldObj, pos, this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if(this.hasPlant()) {
            plant.onChunkUnload(worldObj, pos, this);
        }
    }

    //this saves the data on the tile entity
    @Override
    public void writeTileNBT(NBTTagCompound tag) {
        stats.writeToNBT(tag);
        tag.setBoolean(AgriCraftNBT.CROSS_CROP,crossCrop);
        tag.setBoolean(AgriCraftNBT.WEED, weed);
        if(this.plant != null) {
            tag.setTag(AgriCraftNBT.SEED, CropPlantHandler.writePlantToNBT(plant));
            if(getAdditionalCropData() != null) {
                tag.setTag(AgriCraftNBT.INVENTORY, getAdditionalCropData().writeToNBT());
            }
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readTileNBT(NBTTagCompound tag) {
        this.stats = PlantStats.readFromNBT(tag);
        this.crossCrop=tag.getBoolean(AgriCraftNBT.CROSS_CROP);
        this.weed=tag.getBoolean(AgriCraftNBT.WEED);
        if(tag.hasKey(AgriCraftNBT.SEED) && !tag.hasKey(AgriCraftNBT.META)) {
            this.plant = CropPlantHandler.readPlantFromNBT(tag.getCompoundTag(AgriCraftNBT.SEED));
        }
        else {
            this.plant=null;
        }
        if(tag.hasKey(AgriCraftNBT.INVENTORY) && this.plant != null) {
            this.data = plant.readCropDataFromNBT(tag.getCompoundTag(AgriCraftNBT.INVENTORY));
        }
    }

    /** Apply a GROWTH increment */
    public void applyGrowthTick() {
        int meta = getGrowthStage();
        if(hasPlant()) {
            plant.onAllowedGrowthTick(worldObj, pos, meta);
        }
        IBlockState state = getWorld().getBlockState(getPos());
        if (hasWeed() || !plant.isMature(getWorld(), pos, state)) {
            setGrowthStage(meta + 1);
            CompatibilityHandler.getInstance().announceGrowthTick(getWorld(), getPos(), state);
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
          NORTH, SOUTH, EAST and WEST DIRECTION
     */
    public List<TileEntityCrop> getNeighbours() {
        List<TileEntityCrop> neighbours = new ArrayList<>();
        addNeighbour(neighbours, AgriForgeDirection.NORTH);
        addNeighbour(neighbours, AgriForgeDirection.SOUTH);
        addNeighbour(neighbours, AgriForgeDirection.EAST);
        addNeighbour(neighbours, AgriForgeDirection.WEST);
        return neighbours;
    }

    private void addNeighbour(List<TileEntityCrop> neighbours, AgriForgeDirection direction) {
        TileEntity te = worldObj.getTileEntity(getPos().add(direction.offsetX, direction.offsetY, direction.offsetZ));
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

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getPlantIcon(IBlockState state) {
        return plant.getPrimaryPlantTexture(state.getValue(AgriCraftProperties.GROWTHSTAGE));
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
    		//Add the SEED name.
    		information.add(StatCollector.translateToLocal("agricraft_tooltip.seed") + ": " + this.getSeedStack().getDisplayName());
    		//Add the GROWTH.
    		if(this.isMature()) {
    			information.add(StatCollector.translateToLocal("agricraft_tooltip.growthStage")+": "+StatCollector.translateToLocal("agricraft_tooltip.mature"));
    		} else {
    			information.add(StatCollector.translateToLocal("agricraft_tooltip.growthStage")+": "+((int) ( (100*(this.getBlockMetadata()+0.00F))/7.00F)+"%" ));
    		}
    		//Add the ANALYZED data.
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
