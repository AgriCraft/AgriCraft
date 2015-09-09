package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * This class represents the root tile entity for all AgriCraft custom wood blocks.
 * Through this class, the custom woods are remembered for the blocks.
 * 
 * Todo:
 *  - Track material name.
 *
 */
public class TileEntityCustomWood extends TileEntityAgricraft implements IDebuggable {
	
	/**
     * The default material to use. Currently is wood planks.
     */
    private static final Block DEFAULT_MATERIAL = Blocks.planks;
    
    /**
     * The default metadata to use. Currently is set to Oak(0) for Planks.
     */
    private static final int DEFAULT_META = 0;
	
	/**
     * A pointer to the the block the CustomWoodBlock is imitating.
     * 
     * Defaults to {@link #DEFAULT_MATERIAL}.
     */
    private Block material = Blocks.planks;
    
    /**
     * The metadata of the block the CustomWoodBlock is imitating.
     * 
     * Defaults to {@link #DEFAULT_META}.
     */
    private int materialMeta = 0;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
    	//Can never be null. (Has to be made out of something... lol).
    	tag.setString(Names.NBT.material, Block.blockRegistry.getNameForObject(this.material));
        tag.setInteger(Names.NBT.materialMeta, this.materialMeta);
        //Required call to super.
        super.writeToNBT(tag);
    }

    /**
     * Loads the CustomWood entity from a NBTTag, as to load from a savefile.
     * 
     * @param tag the tag to load the entity data from.
     */
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setMaterial(tag);
    }

    /**
     * Tests to see if another CustomWood entity is of the same material.
     * 
     * @param tileEntity the CustomWood entity to test.
     * @return if the construction materials for both entities are the same.
     */
    public final boolean isSameMaterial(TileEntityCustomWood tileEntity) {
        return tileEntity!=null && this.getBlockMetadata()==tileEntity.getBlockMetadata() && this.getMaterial()==tileEntity.getMaterial() && this.getMaterialMeta()==tileEntity.getMaterialMeta();
    }

    
    /**
     * Sets the CustomWood block's material, the material to mimic, from an NBTTag.
     * This function is intended for use internally, for serialization.
     * 
     * @param tag the tag to set the block's material from.
     */
    private final void setMaterial(NBTTagCompound tag) {
        if(tag!=null && tag.hasKey(Names.NBT.material) && tag.hasKey(Names.NBT.materialMeta)) {
            this.setMaterial(tag.getString(Names.NBT.material), tag.getInteger(Names.NBT.materialMeta));
        }
    }

    /**
     * Sets the CustomWood block's material, the material to mimic, from an ItemStack.
     * Hackish -> Don't like.
     * 
     * @param stack the ItemStack to set the block's material from.
     */
    public final void setMaterial(ItemStack stack) {
        if(stack!=null && stack.getItem()!=null && stack.getItem() instanceof ItemBlock) {
        	this.setMaterial(stack.stackTagCompound);
        }
    }

    /**
     * Sets the CustomWood block's material, the material to mimic, from the name of the material (block) and its metadata value.
     * 
     * @param name the name of the material (block).
     * @param meta the metadata value of the material (block).
     */
    public final void setMaterial(String name, int meta) {
        this.setMaterial((Block) Block.blockRegistry.getObject(name), meta);
    }
    
    /**
     * Sets the CustomWood block's material, the material to mimic, from the name of the material (block) and its metadata value.
     * 
     * @param name the name of the material (block).
     * @param meta the metadata value of the material (block).
     */
    public final void setMaterial(Block block, int meta) {
        if(block!=null) {
            this.material = block;
            this.materialMeta = meta;
        }
    }

    /**
     * Retrieves the material the CustomWood is mimicking.
     * 
     * @return the material, in Block form.
     */
    public final Block getMaterial() {
        return this.material;
    }

    /**
     * Retrieves the metadata of the material the CustomWood is mimicking.
     * 
     * @return the metadata of the material.
     */
    public final int getMaterialMeta() {
        return this.materialMeta;
    }
    
    /**
     * Retrieves a stack of the material the CustomWood is mimicking.
     * I do not like this method. Solely for waila.
     * 
     * @return a stack of the mimicked material.
     */
    public final ItemStack getMaterialStack() {
        return new ItemStack(this.material, this.materialMeta);
    }

    /**
     * Retrieves the name of the material the CustomWood is mimicking.
     * 
     * @return the name of the material.
     */
    public final String getMaterialName() {
        return Block.blockRegistry.getNameForObject(this.material);
    }
    
    /**
     * Generates an NBTTag for the material the CustomWood is mimicking.
     * I do not like this method. Solely for waila.
     * 
     * @return an NBTTag for the CustomWood material.
     */
    public final NBTTagCompound getMaterialTag() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(Names.NBT.material, this.getMaterialName());
        tag.setInteger(Names.NBT.materialMeta, this.materialMeta);
        return tag;
    }

    /**
     * Retrieves the CustomWood icon.
     * 
     * @return the icon, or texture, of the CustomWood.
     */
    public IIcon getIcon() {
    	//Look ma! Two line!
    	IIcon icon = this.material.getIcon(0, this.materialMeta);
        return icon==null?DEFAULT_MATERIAL.getIcon(0, DEFAULT_META):icon; //I guess the Icon could come out null.
    }

    @Override
    public void addDebugInfo(List<String> list) {
    	// The old method was kinda dumb.
        list.add("this material is: " + this.getMaterialName() + ":" + this.getMaterialMeta());
    }

    @Override
    public boolean isRotatable() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int colorMultiplier() {
        if(this.worldObj==null) {
            return 16777215;
        } else {
            return getBlockType().colorMultiplier(worldObj, xCoord, yCoord, zCoord);
        }
    }
}
