/*
 */
package com.infinityraider.agricraft.api.util;

import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class representing a fuzzy ItemStack.
 */
public class FuzzyStack {

    private final ItemStack stack;

    private final boolean ignoreMeta;
    private final boolean ignoreTags;
    private final boolean useOreDict;

    public FuzzyStack(ItemStack stack) {
        this(stack, false, false, false);
    }

    public FuzzyStack(ItemStack stack, boolean ignoreMeta, boolean ignoreTags, boolean useOreDict) {
        if (stack == null) {
            throw new NullPointerException("The Itemstack must not be null for FuzzyStacks!");
        } else if (stack.getItem() == null) {
            throw new NullPointerException("The Item must not be null for FuzzyStacks!");
        }

        this.stack = stack;
        this.ignoreMeta = ignoreMeta;
        this.ignoreTags = ignoreTags;
        this.useOreDict = useOreDict;
    }

    public static final Optional<FuzzyStack> fromBlockState(IBlockState state) {
        return Optional.ofNullable(state)
                .map(s -> new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s)))
                .filter(i -> i.getItem() != null)
                .map(FuzzyStack::new);
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
        if (obj instanceof ItemStack) {
            ItemStack other = (ItemStack) obj;
            if (other.getItem() != null) {
                return this.equals(new FuzzyStack(other));
            } else {
                return false;
            }
        } else if (obj instanceof FuzzyStack) {
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
