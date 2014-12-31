package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class TileEntityCustomWood extends TileEntityAgricraft {
    protected String materialName;
    protected int materialMeta;


    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.materialName!=null && !this.materialName.equals("")) {
            tag.setString(Names.NBT.material, this.materialName);
            tag.setInteger(Names.NBT.materialMeta, this.materialMeta);
        }
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.NBT.material) && tag.hasKey(Names.NBT.materialMeta)) {
            this.materialName = tag.getString(Names.NBT.material);
            this.materialMeta = tag.getInteger(Names.NBT.materialMeta);
        }
    }

    public boolean isSameMaterial(TileEntityCustomWood tileEntity) {
        return tileEntity != null && (this.getBlockMetadata() == tileEntity.getBlockMetadata()) && (ItemStack.areItemStacksEqual(this.getMaterial(), tileEntity.getMaterial()));
    }

    //set material from tag
    public void setMaterial(NBTTagCompound tag) {
        if(tag!=null && tag.hasKey(Names.NBT.material) && tag.hasKey(Names.NBT.materialMeta)) {
            this.materialName = tag.getString(Names.NBT.material);
            this.materialMeta = tag.getInteger(Names.NBT.materialMeta);
        }
    }

    //set material from stack
    public void setMaterial(ItemStack stack) {
        this.materialName = Block.blockRegistry.getNameForObject(stack.getItem());
        this.materialMeta = stack.getItemDamage();
    }

    //set material from string and int
    public void setMaterial(String name, int meta) {
        if(name!=null && Block.blockRegistry.getObject(name)!=null) {
            this.materialName = name;
            this.materialMeta = meta;
        }
    }

    //get material information
    public String getMaterialName() {
        return this.materialName;
    }

    public int getMaterialMeta() {
        return this.materialMeta;
    }

    public ItemStack getMaterial() {
        ItemStack stack = new ItemStack(Blocks.planks, 1, 0);
        if(this.materialName !=null && !this.materialName.equals("")) {
            stack = new ItemStack((Block) Block.blockRegistry.getObject(this.materialName), 1, this.materialMeta);
        }
        return stack;
    }

    public NBTTagCompound getMaterialTag() {
        NBTTagCompound tag = new NBTTagCompound();
        if(this.materialName !=null && !this.materialName.equals("")) {
            tag.setString(Names.NBT.material, this.materialName);
            tag.setInteger(Names.NBT.materialMeta, this.materialMeta);
        }
        else {
            //default to oak planks
            tag.setString(Names.NBT.material, Block.blockRegistry.getNameForObject(Blocks.planks));
            tag.setInteger(Names.NBT.materialMeta, 0);
        }
        return tag;
    }

    public IIcon getIcon() {
        if(this.materialName !=null && !this.materialName.equals("")) {
            Block material = (Block) Block.blockRegistry.getObject(this.materialName);
            return material.getIcon(0, this.materialMeta);
        }
        else {
            return Blocks.planks.getIcon(0, 0);
        }
    }
}
