package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.PacketHandler;
import com.InfinityRaider.AgriCraft.network.MessageTileEntityCrop;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.CrossCropHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class TileEntityCrop extends TileEntity {
    public int growth=0;
    public int gain=0;
    public int strength=0;
    public boolean crossCrop=false;
    public IPlantable seed = null;
    public int seedMeta = 0;

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setShort(Names.growth, (short) growth);
        tag.setShort(Names.gain, (short) gain);
        tag.setShort(Names.strength, (short) strength);
        tag.setBoolean("crossCrop",crossCrop);
        tag.setString("seed",this.getSeedString());
        tag.setShort("seedMeta",(short) seedMeta);
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.growth=tag.getInteger(Names.growth);
        this.gain=tag.getInteger(Names.gain);
        this.strength=tag.getInteger(Names.strength);
        this.crossCrop=tag.getBoolean("crossCrop");
        this.setSeed(tag.getString("seed"));
        this.seedMeta=tag.getInteger("seedMeta");
        super.readFromNBT(tag);
    }

    //the code that makes the crop cross with neighboring crops
    public void crossOver(World world, int x, int y, int z) {
        if(!world.isRemote) {
            boolean change = false;
            TileEntityCrop[] neighbours = new TileEntityCrop[4];
            neighbours[0] = (world.getTileEntity(x - 1, y, z) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x - 1, y, z) : null;
            neighbours[1] = (world.getTileEntity(x + 1, y, z) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x + 1, y, z) : null;
            neighbours[2] = (world.getTileEntity(x, y, z - 1) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x, y, z - 1) : null;
            neighbours[3] = (world.getTileEntity(x, y, z + 1) instanceof TileEntityCrop) ? (TileEntityCrop) world.getTileEntity(x, y, z + 1) : null;
            if (Math.random() > ConfigurationHandler.mutationChance) {
                LogHelper.debug("Trying to set crosscrop to a neighbour");
                int index = (int) Math.floor(Math.random() * neighbours.length);
                if (neighbours[index]!=null && neighbours[index].seed!=null && neighbours[index].isMature()) {
                    this.crossCrop = false;
                    int[] stats = CrossCropHelper.getStats(neighbours);
                    this.setPlant(stats[0], stats[1] ,stats[2], neighbours[index].seed, neighbours[index].seedMeta);
                    change = true;
                    LogHelper.debug("Set crosscrop to "+Item.itemRegistry.getNameForObject(neighbours[index].seed));
                }
            } else {
                LogHelper.debug("Trying to set crosscrop to a mutation");
                ItemStack[] crossOvers = CrossCropHelper.getCrossOvers(neighbours);
                if (crossOvers != null && crossOvers.length>0) {
                    int index = (int) Math.floor(Math.random()*crossOvers.length);
                    if(crossOvers[index].getItem()!=null) {
                        int[] stats = CrossCropHelper.getStats(neighbours);
                        this.crossCrop = false;
                        this.setPlant(stats[0], stats[1], stats[2], (ItemSeeds) crossOvers[index].getItem(), crossOvers[index].getItemDamage());
                        change = true;
                        LogHelper.debug("Set crosscrop to "+Item.itemRegistry.getNameForObject(crossOvers[index].getItem()));
                    }
                }
            }
            if (change) {
                world.addBlockEvent(x, y, z, world.getBlock(x, y, z), 1, 0);    //lets the tile entity know it has been updated
                this.markDirty();                                               //lets Minecraft know the tile entity has changed
                world.notifyBlockChange(x, y, z, world.getBlock(x, y, z));      //lets the neighbors know this has been updated
            }
        }
    }

    //sets the plant in the crop
    public void setPlant(int growth, int gain, int strength, IPlantable seed, int seedMeta) {
        if( (!this.crossCrop) && (!this.hasPlant())) {
            this.growth = growth;
            this.gain = gain;
            this.strength = strength;
            this.seed = seed;
            this.seedMeta = seedMeta;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 0, 2);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if(worldObj.isRemote && id == 1) {
            LogHelper.debug("Marking crop for update");
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
        BlockBush plant = SeedHelper.getPlant((ItemSeeds) this.seed);
        if(this.worldObj.getBlock(this.xCoord,this.yCoord-1,this.zCoord) == net.minecraft.init.Blocks.farmland && this.worldObj.getBlockLightValue(this.xCoord,this.yCoord+1,this.zCoord)>8) {
            if(plant instanceof BlockModPlant) {
                BlockModPlant blockModPlant = (BlockModPlant) plant;
                return blockModPlant.base == null || OreDictHelper.isSameOre(blockModPlant.base, blockModPlant.baseMeta, this.worldObj.getBlock(this.xCoord, this.yCoord - 2, this.zCoord), this.worldObj.getBlockMetadata(this.xCoord, this.yCoord-2, this.zCoord));
            }
            return true;
        }
        return false;
    }

    //check the block if the plant is mature
    public boolean isMature() {
        if(!this.worldObj.isRemote) {
            return this.worldObj.getBlock(xCoord, yCoord, zCoord) != null && this.worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockCrop && ((BlockCrop) this.worldObj.getBlock(xCoord, yCoord, zCoord)).isMature(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        return false;
    }

    //a helper method for ItemSeed <-> String conversion for storing seed as a string in NBT
    public String getSeedString() {
        return this.seed==null?"none":Item.itemRegistry.getNameForObject(this.seed);
    }

    //a helper method for ItemSeed <-> String conversion for storing seed as a string in NBT
    public void setSeed(String input) {
        this.seed = input.equalsIgnoreCase("none")?null:(ItemSeeds) Item.itemRegistry.getObject(input);
    }

    //uses the packet handler to create a packet with the data contained in the tile entity
    @Override
    public Packet getDescriptionPacket() {
       return PacketHandler.instance.getPacketFrom(new MessageTileEntityCrop(this));
    }

    //this gets called when the tile entity should get updated on the client (sort of)
    @Override
    public void markDirty() {
        PacketHandler.instance.sendToAllAround(new MessageTileEntityCrop(this),new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId,(double) this.xCoord,(double) this.yCoord, (double) this.zCoord,128d));
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);     //update lighting (just in case the plant emits light)
        super.markDirty();
    }

}
