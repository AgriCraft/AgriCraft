package com.InfinityRaider.AgriCraft.farming;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public abstract class CropOverride {
    /** This is called when a seed is planted. Argument is the player that planted the seed, this is null when the seed got planted due to a mutation, crossover or world gen */
    public abstract void onSeedPlanted(EntityPlayer player);

    /** Return false if you want to define custom growing logic */
    public abstract boolean hasDefaultGrowth();

    /** This is called when the crop doesn't follow default growing and should advance one growth stage*/
    public abstract void increaseGrowth();

    /** Return false if this has custom bone meal logic */
    public abstract boolean hasDefaultBonemeal();

    /** Gets called when bonemeal is applied */
    public abstract void applyBonemeal();

    /** Return false if you want to define custom harvesting logic (right clicks)*/
    public abstract boolean hasDefaultHarvesting();

    /** This is called when the crop doesn't follow default harvesting and is harvested*/
    public abstract void onHarvest();

    /** Return false if you want to define custom breaking logic (left clicks)*/
    public abstract boolean hasDefaultBreaking();

    /** This is called when the crop doesn't follow default breaking and is broken */
    public abstract void onBreak();

    /** Immune to weeds or not */
    public abstract boolean immuneToWeed();

    /** Returns a relevant icon */
    @SideOnly(Side.CLIENT)
    public abstract IIcon getIcon();

    /** Return an NBT tag with stored relevant data */
    public abstract NBTTagCompound saveToNBT();

    /** Loads relevant data from NBT*/
    public abstract void loadFromNBT(NBTTagCompound tag);
}
