/*
 */
package com.infinityraider.agricraft.api.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class representing a fuzzy ItemStack.
 */
public class FuzzyStack {

    @Nonnull
    private final ItemStack stack;
    @Nonnull
    private final NBTTagCompound tags;

    private final boolean ignoreMeta;
    private final boolean useOreDict;
    
    @Nonnull
    private final List<String> ignoreTags;

    public FuzzyStack(ItemStack stack) {
        this(stack, false, false);
    }
    
    public FuzzyStack(ItemStack stack, boolean ignoreMeta, boolean useOreDict, String... ignoreTags) {
        this(stack, ignoreMeta, useOreDict, Arrays.asList(ignoreTags));
    }

    public FuzzyStack(ItemStack stack, boolean ignoreMeta, boolean useOreDict, List<String> ignoreTags) {
        if (stack == null) {
            throw new NullPointerException("The Itemstack must not be null for FuzzyStacks!");
        } else if (stack.getItem() == null) {
            throw new NullPointerException("The Item must not be null for FuzzyStacks!");
        }

        this.stack = stack.copy();
        this.tags = Optional.ofNullable(stack.getTagCompound()).orElseGet(NBTTagCompound::new);
        this.ignoreTags = (ignoreTags != null) ? ignoreTags : Collections.EMPTY_LIST;
        this.ignoreMeta = ignoreMeta;
        this.useOreDict = useOreDict;
        
        this.stack.setTagCompound(stripTags(this.stack.getTagCompound()));
        
    }

    @Nonnull
    public static final Optional<FuzzyStack> fromBlockState(IBlockState state) {
        return Optional.ofNullable(state)
                .map(s -> new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s)))
                .filter(i -> i.getItem() != null)
                .map(FuzzyStack::new);
    }

    @Nonnull
    public ItemStack toStack() {
        final ItemStack copy = this.stack.copy();
        copy.setTagCompound(tags.getKeySet().isEmpty() ? null : tags.copy());
        return copy;
    }

    @Nonnull
    public Item getItem() {
        return this.stack.getItem();
    }

    @Nonnull
    public int getMeta() {
        return this.stack.getMetadata();
    }

    @Nonnull
    public NBTTagCompound getTagCompound() {
        return this.tags.copy();
    }

    public boolean isMetaEqual(ItemStack other) {
        return other != null && (this.ignoreMeta || this.getMeta() == other.getMetadata());
    }

    public boolean isMetaEqual(FuzzyStack other) {
        return other != null && (this.ignoreMeta || other.ignoreMeta || this.getMeta() == other.getMeta());
    }
    
    public boolean isTagsEqual(ItemStack other) {
        return other != null && this.getTagCompound().equals(stripTags(other.getTagCompound()));
    }

    public boolean isTagsEqual(FuzzyStack other) {
        return other != null && other.stripTags(this.getTagCompound()).equals(this.stripTags(other.getTagCompound()));
    }

    public boolean isItemEqual(Item other) {
        return other != null && this.stack.getItem().equals(other);
    }

    public boolean isItemEqual(ItemStack other) {
        return other != null && this.stack.getItem().equals(other.getItem());
    }

    public boolean isItemEqual(FuzzyStack other) {
        return other != null && this.stack.getItem().equals(other.stack.getItem());
    }
    
    @Nonnull
    private NBTTagCompound stripTags(NBTTagCompound tag) {
        if (tag == null || this.ignoreTags.contains("*")) {
            return new NBTTagCompound();
        } else {
            final NBTTagCompound stripped = tag.copy();
            this.ignoreTags.forEach(stripped::removeTag);
            return stripped;
        }
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

            if (this.isItemEqual(other) && this.isMetaEqual(other) && this.isTagsEqual(other)) {
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
