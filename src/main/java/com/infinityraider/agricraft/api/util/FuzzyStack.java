/*
 */
package com.infinityraider.agricraft.api.util;

import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class representing a fuzzy ItemStack.
 */
public class FuzzyStack {

    private final ItemStack stack;

    private final boolean ignoreMeta;
    private final boolean ignoreTags;
    private final boolean useOreDict;

    public FuzzyStack(IBlockAccess world, BlockPos pos) {
        this(world.getBlockState(pos));
    }

    public FuzzyStack(IBlockState state) {
        this(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
    }

    public FuzzyStack(ItemStack stack) {
        this(stack, false, false, false);
    }

    public FuzzyStack(ItemStack stack, boolean ignoreMeta, boolean ignoreTags, boolean useOreDict) {
        if (stack == null || stack.getItem() == null) {
            throw new NullPointerException();
        }

        this.stack = stack;
        this.ignoreMeta = ignoreMeta;
        this.ignoreTags = ignoreTags;
        this.useOreDict = useOreDict;
    }

    public ItemStack toStack() {
        return this.stack.copy();
    }

    public Item getItem() {
        return this.stack.getItem();
    }

    public int getMeta() {
        return this.stack.getMetadata();
    }

    public Optional<NBTTagCompound> getTags() {
        return Optional.ofNullable(this.stack.getTagCompound()).map(t -> t.copy());
    }

    public boolean matches(ItemStack stack) {
        return stack != null && this.equals(new FuzzyStack(stack));
    }

    public boolean areMetaEqual(FuzzyStack other) {
        return other != null && (this.ignoreMeta || other.ignoreMeta || this.getMeta() == other.getMeta());
    }

    public boolean areTagsEqual(FuzzyStack other) {
        return other != null && (this.ignoreTags || other.ignoreTags || this.getTags().equals(other.getTags()));
    }

    public boolean areItemEqual(FuzzyStack other) {
        return other != null && this.stack.getItem().equals(other.stack.getItem());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FuzzyStack) {
            FuzzyStack other = (FuzzyStack) obj;

            if (this.areItemEqual(other) && this.areMetaEqual(other) && this.areTagsEqual(other)) {
                return true;
            }

            if (this.useOreDict || other.useOreDict) {
                int[] ids1 = OreDictionary.getOreIDs(this.toStack());
                int[] ids2 = OreDictionary.getOreIDs(other.toStack());
                for (int id1 : ids1) {
                    for (int id2 : ids2) {
                        if (id1 == id2) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.stack.toString();
    }

}
