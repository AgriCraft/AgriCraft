package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.MutationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class TileEntityCrop extends TileEntityAgricraft {
    public int growth=0;
    public int gain=0;
    public int strength=0;
    public boolean analyzed=false;
    public boolean crossCrop=false;
    public IPlantable seed = null;
    public int seedMeta = 0;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setShort(Names.NBT.growth, (short) growth);
        tag.setShort(Names.NBT.gain, (short) gain);
        tag.setShort(Names.NBT.strength, (short) strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
        tag.setBoolean("crossCrop",crossCrop);
        if(this.seed!=null) {
            tag.setString(Names.Objects.seed, this.getSeedString());
            tag.setShort(Names.NBT.meta, (short) seedMeta);
        }
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.growth=tag.getInteger(Names.NBT.growth);
        this.gain=tag.getInteger(Names.NBT.gain);
        this.strength=tag.getInteger(Names.NBT.strength);
        this.analyzed=tag.hasKey(Names.NBT.analyzed) && tag.getBoolean(Names.NBT.analyzed);
        this.crossCrop=tag.getBoolean("crossCrop");
        if(tag.hasKey(Names.Objects.seed) && tag.hasKey(Names.NBT.meta)) {
            this.setSeed(tag.getString(Names.Objects.seed));
            this.seedMeta = tag.getInteger(Names.NBT.meta);
        }
        super.readFromNBT(tag);
    }

    //the code that makes the crop cross with neighboring crops
    public void crossOver(World world, int x, int y, int z) {
        if(!world.isRemote) {
            //flag to check if the crop needs to update
            boolean change = false;
            //possible new plant
            ItemSeeds result=null;
            int resultMeta=0;
            int mutationId=0;
            Block req=null;
            int reqMeta = 0;
            double chance=0;
            //find neighbours
            TileEntityCrop[] neighbours = new TileEntityCrop[4];
            neighbours[0] = (world.getTileEntity(x - 1, y, z) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x - 1, y, z) : null;
            neighbours[1] = (world.getTileEntity(x + 1, y, z) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x + 1, y, z) : null;
            neighbours[2] = (world.getTileEntity(x, y, z - 1) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x, y, z - 1) : null;
            neighbours[3] = (world.getTileEntity(x, y, z + 1) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x, y, z + 1) : null;
            //find out the new plant
            if (Math.random() > ConfigurationHandler.mutationChance) {
                int index = (int) Math.floor(Math.random() * neighbours.length);
                if (neighbours[index]!=null && neighbours[index].seed!=null && neighbours[index].isMature()) {
                    result = (ItemSeeds) neighbours[index].seed;
                    resultMeta = neighbours[index].seedMeta;
                    chance = SeedHelper.getSpreadChance(result, resultMeta);
                }
            } else {
                MutationHandler.Mutation[] crossOvers = MutationHandler.getCrossOvers(neighbours);
                if (crossOvers!=null && crossOvers.length>0) {
                    int index = (int) Math.floor(Math.random()*crossOvers.length);
                    if(crossOvers[index].result.getItem()!=null) {
                        result = (ItemSeeds) crossOvers[index].result.getItem();
                        resultMeta = crossOvers[index].result.getItemDamage();
                        mutationId = crossOvers[index].id;
                        req = crossOvers[index].requirement;
                        reqMeta = crossOvers[index].requirementMeta;
                        chance = crossOvers[index].chance;
                    }
                }
            }
            //try to set the new plant
            if(result!=null && SeedHelper.isValidSeed(result, resultMeta) && this.canMutate(result, resultMeta, mutationId, req, reqMeta)) {
                if(Math.random()<chance) {
                    this.crossCrop = false;
                    int[] stats = MutationHandler.getStats(neighbours);
                    this.setPlant(stats[0], stats[1], stats[2], false, result, resultMeta);
                    change = true;
                }
            }
            //update the tile entity on a change
            if (change) {
                world.addBlockEvent(x, y, z, world.getBlock(x, y, z), 1, 0);    //lets the tile entity know it has been updated
                this.markDirty();                                               //lets Minecraft know the tile entity has changed
                world.notifyBlockChange(x, y, z, world.getBlock(x, y, z));      //lets the neighbors know this has been updated
            }
        }
    }

    //checks if a plant can mutate
    private boolean canMutate(ItemSeeds seed, int seedMeta, int id, Block req, int reqMeta) {
        if(this.canGrow(seed)) {
            //id = 0: no requirement
            //id = 1: block below farmland has to be the req block
            //id = 2: block near has to be the req block
            switch(id) {
                case 0: return true;
                case 1: return (this.worldObj.getBlock(this.xCoord, this.yCoord-2, this.zCoord)==req && this.worldObj.getBlockMetadata(this.xCoord, this.yCoord-2, this.zCoord)==reqMeta);
                case 2: return isBlockNear(req, reqMeta);
            }
        }
        return false;
    }

    //checks if a given block is near
    private boolean isBlockNear(Block block, int meta) {
        for(int x=-3;x<=3;x++) {
            for(int y=0;y<=3;y++) {
                for(int z=-3;z<=3;z++) {
                    if(this.worldObj.getBlock(this.xCoord+x, this.yCoord+y, this.zCoord+z)==block && this.worldObj.getBlockMetadata(this.xCoord+x, this.yCoord+y, this.zCoord+z)==meta) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //sets the plant in the crop
    public void setPlant(int growth, int gain, int strength, boolean analyzed, IPlantable seed, int seedMeta) {
        if( (!this.crossCrop) && (!this.hasPlant())) {
            this.growth = growth;
            this.gain = gain;
            this.strength = strength;
            this.seed = seed;
            this.analyzed = analyzed;
            this.seedMeta = seedMeta;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 2);
        }
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

    //check to see if there is a plant here
    public boolean hasPlant() {
        return ((this.seed!=null)&&(this.seed.getPlant(this.worldObj, this.xCoord, this.yCoord, this.zCoord)!=null));
    }

    //check if the crop is fertile
    public boolean isFertile() {
        return this.canGrow((ItemSeeds) this.seed);
    }

    //check the block if the plant is mature
    public boolean isMature() {
        return !this.worldObj.isRemote && this.worldObj.getBlock(xCoord, yCoord, zCoord) != null && this.worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockCrop && ((BlockCrop) this.worldObj.getBlock(xCoord, yCoord, zCoord)).isMature(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    //check if the seed can grow
    private boolean canGrow(ItemSeeds seed) {
        BlockBush plant = SeedHelper.getPlant(seed);
        if(this.worldObj.getBlock(this.xCoord,this.yCoord-1,this.zCoord) == net.minecraft.init.Blocks.farmland && this.worldObj.getBlockLightValue(this.xCoord,this.yCoord+1,this.zCoord)>8) {
            if(plant instanceof BlockModPlant) {
                BlockModPlant blockModPlant = (BlockModPlant) plant;
                return blockModPlant.base == null || OreDictHelper.isSameOre(blockModPlant.base, blockModPlant.baseMeta, this.worldObj.getBlock(this.xCoord, this.yCoord - 2, this.zCoord), this.worldObj.getBlockMetadata(this.xCoord, this.yCoord-2, this.zCoord));
            }
            return true;
        }
        return false;
    }

    public ItemStack getSeedStack() {
        ItemStack seed = new ItemStack((ItemSeeds) this.seed, 1, this.seedMeta);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(Names.NBT.growth, this.growth);
        tag.setInteger(Names.NBT.gain, this.gain);
        tag.setInteger(Names.NBT.strength, this.strength);
        tag.setBoolean(Names.NBT.analyzed, this.analyzed);
        seed.setTagCompound(tag);
        return seed;
    }

    //a helper method for ItemSeed <-> String conversion for storing seed as a string in NBT
    public String getSeedString() {
        return this.seed==null?"none":Item.itemRegistry.getNameForObject(this.seed);
    }

    //a helper method for ItemSeed <-> String conversion for storing seed as a string in NBT
    public void setSeed(String input) {
        this.seed = input.equalsIgnoreCase("none")?null:(ItemSeeds) Item.itemRegistry.getObject(input);
    }
}
