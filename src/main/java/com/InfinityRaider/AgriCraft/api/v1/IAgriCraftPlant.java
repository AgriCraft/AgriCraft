package com.InfinityRaider.AgriCraft.api.v1;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.Random;

/** should be implemented in Block class */
public interface IAgriCraftPlant extends IGrowable, IPlantable {
    /** Returns the GowthRequirement for this plant */
    public IGrowthRequirement getGrowthRequirement();

    /** Gets the seed for this plant */
    public IAgriCraftSeed getSeed();

    /** Gets the block for this plant, should return this if implemented correctly, but it's just here to be sure */
    public Block getBlock();

    /** Gets an ItemStack with the correct seed */
    public ItemStack getSeedStack(int amount);

    /** Gets an arraylist of all possible fruit drops from this plant */
    public ArrayList<ItemStack> getAllFruits();

    /** Returns a random fruit for this plant */
    public ItemStack getRandomFruit(Random rand);

    /** Returns an ArrayList with amount of  random fruit stacks for this plant */
    public ArrayList<ItemStack> getFruit(int amount, Random rand);

    /** Gets the icon for the growth stage, going from 0 to 7. 0 is a newly planted plant and 7 is a mature plant */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta);

    /** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
    @SideOnly(Side.CLIENT)
    public abstract boolean renderAsFlower();

}
