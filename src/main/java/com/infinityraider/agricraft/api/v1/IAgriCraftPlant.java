package com.infinityraider.agricraft.api.v1;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

/** should be implemented in Block class */
public interface IAgriCraftPlant extends IGrowable, IPlantable, IAgriCraftRenderable {
    /** Returns the GowthRequirement for this plant */
    IGrowthRequirement getGrowthRequirement();

    /** Gets the seed for this plant */
    IAgriCraftSeed getSeed();

    /** Gets the block for this plant, should return this if implemented correctly, but it's just here to be sure */
    Block getBlock();

    /** Gets an ItemStack with the correct seed */
    ItemStack getSeedStack(int amount);

    /** Gets an arraylist of all possible fruit drops from this plant */
    ArrayList<ItemStack> getAllFruits();

    /** Returns a random fruit for this plant */
    ItemStack getRandomFruit(Random rand);

    /** Returns an ArrayList with amount of  random fruit stacks for this plant */
    ArrayList<ItemStack> getFruit(int amount, Random rand);

    /** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
    @SideOnly(Side.CLIENT)
    boolean renderAsFlower();

    /** Gets the registered TextureAtlasSprite icon for the given growth stage */
    @SideOnly(Side.CLIENT)
    TextureAtlasSprite getPlantIcon(int growthStage);


}
