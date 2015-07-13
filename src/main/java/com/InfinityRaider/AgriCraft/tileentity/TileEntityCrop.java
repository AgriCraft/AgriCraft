package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import com.InfinityRaider.AgriCraft.apiimpl.v1.PlantStats;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.CrossOverResult;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationEngine;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TileEntityCrop extends TileEntityAgricraft implements IDebuggable{
    private PlantStats stats = new PlantStats();
    private boolean analyzed=false;
    private boolean crossCrop=false;
    private boolean weed=false;
    private CropPlant plant;

    private final MutationEngine mutationEngine;

    public TileEntityCrop() {
        this.mutationEngine = new MutationEngine(this);
    }

    public CropPlant getPlant() {return plant;}

    public short getGrowth() {return stats.getGrowth();}

    public short getGain() {return stats.getGain();}

    public short getStrength() {return stats.getStrength();}

    public boolean isAnalyzed() {return analyzed;}

    public boolean hasWeed() {return weed;}

    public boolean isCrossCrop() {return crossCrop;}

    public void setCrossCrop(boolean status) {
        if(status!=this.crossCrop) {
            this.crossCrop = status;
            this.markForUpdate();
        }
    }

    /** get growthrate */
    public int getGrowthRate() {return plant.getGrowthRate();}

    /** check to see if there is a plant here */
    public boolean hasPlant() {return this.plant!=null;}

    /** sets the plant in the crop */
    public void setPlant(int growth, int gain, int strength, boolean analyzed, CropPlant plant) {
        if( (!this.crossCrop) && (!this.hasPlant())) {
            if(plant!=null) {
                this.plant = plant;
                this.stats = new PlantStats(growth, gain, strength);
                this.analyzed = analyzed;
                this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 3);
                this.markForUpdate();
                plant.onSeedPlanted(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    /** sets the plant in the crop */
    public void setPlant(int growth, int gain, int strength, boolean analyzed, Item seed, int seedMeta) {
        this.setPlant(growth, gain, strength, analyzed, CropPlantHandler.getPlantFromStack(new ItemStack(seed, 1, seedMeta)));
    }

    /** clears the plant in the crop */
    public void clearPlant() {
        CropPlant oldPlant = getPlant();
        this.stats = new PlantStats();
        this.plant = null;
        this.analyzed = false;
        this.weed = false;
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 3);
        this.markForUpdate();
        if (oldPlant != null) {
            oldPlant.onPlantRemoved(worldObj, xCoord, yCoord, zCoord);
        }
    }

    /** check if the crop is fertile */
    public boolean isFertile() {
        return worldObj.isAirBlock(xCoord, yCoord +1, zCoord) && plant.isFertile(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    /** gets the height of the crop */
    public float getCropHeight() {
        return hasPlant()?plant.getHeight(getBlockMetadata()):Constants.unit*13;
    }

    /** check if bonemeal can be applied */
    public boolean canBonemeal() {
        if(this.crossCrop) {
            return ConfigurationHandler.bonemealMutation;
        }
        if(this.hasPlant()) {
            if(this.isMature()) {
                return false;
            }
            if(!this.isFertile()) {
                return false;
            }
            return plant.canBonemeal();
        }
        return false;
    }

    /** check the block if the plant is mature */
    public boolean isMature() {
        return this.hasPlant() && this.plant.isMature(worldObj, xCoord, yCoord, zCoord);
    }

    /** gets the fruits for this plant */
    public ArrayList<ItemStack> getFruits() {return plant.getFruitsOnHarvest(getGain(), worldObj.rand);}

    /** allow harvesting */
    public boolean allowHarvest(EntityPlayer player) {
        return hasPlant() && isMature() && plant.onHarvest(worldObj, xCoord, yCoord, zCoord, player);
    }

    public ItemStack getSeedStack() {
        ItemStack seed = plant.getSeed();
        NBTTagCompound tag = new NBTTagCompound();
        SeedHelper.setNBT(tag, getGrowth(), getGain(), getStrength(), this.analyzed);
        seed.setTagCompound(tag);
        return seed;
    }

    /** spawns weed in the crop */
    public void spawnWeed() {
        this.crossCrop=false;
        this.clearPlant();
        this.weed=true;
        this.markForUpdate();
    }

    /** spread the weed */
    public void spreadWeed() {
        List<TileEntityCrop> neighbours = this.getNeighbours();
        for(TileEntityCrop crop:neighbours) {
            if(crop!=null && (!crop.weed) && Math.random()<crop.getWeedSpawnChance()) {
                crop.spawnWeed();
                break;
            }
        }
    }

    public void updateWeed(int growthStage) {
        if(this.hasWeed()) {
            if (growthStage == 0) {
                this.weed = false;
            }
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, growthStage, 3);
            this.markForUpdate();
        }
    }

    //clear the weed
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

    //is fertiliser allowed
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
    public void applyFertiliser(IFertiliser fertiliser, Random rand) {
        if(fertiliser.hasSpecialBehaviour()) {
            fertiliser.onFertiliserApplied(this.getWorldObj(), this.xCoord, this.yCoord, this.zCoord, rand);
        }
        if(this.hasPlant()) {
            ((BlockCrop) Blocks.blockCrop).func_149853_b(this.worldObj, rand, this.xCoord, this.yCoord, this.zCoord);
            this.markForUpdate();
        }
        else if(this.isCrossCrop() && ConfigurationHandler.bonemealMutation) {
            this.crossOver();
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
        tag.setBoolean(Names.NBT.analyzed, analyzed);
        tag.setBoolean(Names.NBT.crossCrop,crossCrop);
        tag.setBoolean(Names.NBT.weed, weed);
        if(this.plant!=null) {
            tag.setTag(Names.NBT.seed, CropPlantHandler.writePlantToNBT(plant));
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.stats = PlantStats.readFromNBT(tag);
        this.analyzed=tag.hasKey(Names.NBT.analyzed) && tag.getBoolean(Names.NBT.analyzed);
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
    }

    //This is to load NBT from crops prior to this update
    @Deprecated
    private void loadPlantFromOldVersion(NBTTagCompound tag) {
        String name = tag.getString(Names.NBT.seed);
        Item seed = name.equalsIgnoreCase("none")?null: (Item) Item.itemRegistry.getObject(name);
        int meta = tag.getInteger(Names.NBT.meta);
        CropPlant plant = CropPlantHandler.getPlantFromStack(new ItemStack(seed, 1, meta));
        if(plant!=null) {
            this.plant = plant;
        } else {
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
        markForUpdate();
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
            if (!crop.isMature()) {
                iterator.remove();
            }
        }
        return neighbours;
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if(worldObj.isRemote && id == 1) {
            this.worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
            Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        return true;
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

    //get the rendertype
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        int type = -1;
        if(this.hasPlant()) {
                type = plant.renderAsFlower()?1:6;
        }
        else if(this.weed) {
            type = 6;
        }
        return type;
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
}
