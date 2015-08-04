package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;

public class TileEntityCustomWood extends TileEntityAgricraft implements IDebuggable {
    protected Block material;
    protected int materialMeta;

    protected TileEntityCustomWood() {}

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.material != null) {
            tag.setString(Names.NBT.material, Block.blockRegistry.getNameForObject(this.material));
            tag.setInteger(Names.NBT.materialMeta, this.materialMeta);
        }
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setMaterial(tag);
    }

    public boolean isSameMaterial(TileEntityCustomWood tileEntity) {
        return tileEntity!=null && this.getBlockMetadata()==tileEntity.getBlockMetadata() && this.getMaterial()==tileEntity.getMaterial() && this.getMaterialMeta()==tileEntity.getMaterialMeta();
    }

    //set material from tag
    public void setMaterial(NBTTagCompound tag) {
        if(tag!=null && tag.hasKey(Names.NBT.material) && tag.hasKey(Names.NBT.materialMeta)) {
            this.setMaterial(tag.getString(Names.NBT.material), tag.getInteger(Names.NBT.materialMeta));
        }
    }

    //set material from stack
    public void setMaterial(ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return;
        }
        if(!(stack.getItem() instanceof ItemBlock)) {
            return;
        }
        this.material = ((ItemBlock) stack.getItem()).field_150939_a;
        this.materialMeta = stack.getItemDamage();
    }

    //set material from string and int
    public void setMaterial(String name, int meta) {
        Block block = (Block) Block.blockRegistry.getObject(name);
        if(block!=null) {
            this.material = block;
            this.materialMeta = meta;
        } else {
            this.material = Blocks.planks;
            this.materialMeta = 0;
        }
    }

    public Block getMaterial() {
        return this.material;
    }

    public int getMaterialMeta() {
        return this.materialMeta;
    }

    public ItemStack getMaterialStack() {
        ItemStack stack = new ItemStack(Blocks.planks, 1, 0);
        if(this.material !=null) {
            stack = new ItemStack(material, 1, this.materialMeta);
        }
        return stack;
    }

    //get material information
    public String getMaterialName() {
        return Block.blockRegistry.getNameForObject(this.material);
    }

    public NBTTagCompound getMaterialTag() {
        NBTTagCompound tag = new NBTTagCompound();
        if(this.material !=null) {
            tag.setString(Names.NBT.material, this.getMaterialName());
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
        if(this.material!=null) {
            IIcon icon = material.getIcon(0, this.materialMeta);
            return icon==null?Blocks.planks.getIcon(0, 0):icon;
        }
        else {
            return Blocks.planks.getIcon(0, 0);
        }
    }

    @Override
    public void addDebugInfo(List<String> list) {
        list.add("this material is: " + this.getMaterialName() + ":" + this.getMaterialStack().getItemDamage());
    }

    @Override
    public boolean isRotatable() {
        return false;
    }
}
