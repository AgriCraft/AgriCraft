/*
 */
package com.infinityraider.agricraft.api.v1.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public FuzzyStack(@Nonnull ItemStack stack) {
        this(stack, false, false);
    }

    public FuzzyStack(@Nonnull ItemStack stack, boolean ignoreMeta, boolean useOreDict, @Nonnull String... ignoreTags) {
        this(stack, ignoreMeta, useOreDict, Arrays.asList(ignoreTags));
    }

    public FuzzyStack(@Nonnull ItemStack stack, boolean ignoreMeta, boolean useOreDict, @Nullable List<String> ignoreTags) {
        // Perform Parameter Validation
        Objects.requireNonNull(stack, "The Itemstack must not be null for FuzzyStacks!");
        Objects.requireNonNull(stack.getItem(), "The Item must not be null for FuzzyStacks!");

        // Perform Assignments
        this.stack = stack.copy();
        this.tags = Optional.ofNullable(stack.getTagCompound()).orElseGet(NBTTagCompound::new);
        this.ignoreTags = Optional.ofNullable(ignoreTags).orElseGet(Collections::emptyList);
        this.ignoreMeta = ignoreMeta;
        this.useOreDict = useOreDict;

        // Strip Ignored Tags From Itemstack
        this.stack.setTagCompound(stripTags(this.stack.getTagCompound()));
    }

    @Nonnull
    public static final Optional<FuzzyStack> from(@Nullable IBlockState state) {
        return Optional.ofNullable(state)
                .map(s -> new ItemStack(s.getBlock(), 1, s.getBlock().getMetaFromState(s)))
                .filter(i -> i.getItem() != null)
                .map(FuzzyStack::new);
    }

    @Nonnull
    public static final Optional<FuzzyStack> from(@Nullable ItemStack stack) {
        return Optional.ofNullable(stack)
                .filter(s -> (s.getItem() != null))
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

    public boolean isMetaEqual(@Nullable ItemStack other) {
        return (other != null) && (this.ignoreMeta || this.getMeta() == other.getMetadata());
    }

    public boolean isMetaEqual(@Nullable FuzzyStack other) {
        return (other != null) && (this.ignoreMeta || other.ignoreMeta || this.getMeta() == other.getMeta());
    }

    public boolean isTagsEqual(@Nullable ItemStack other) {
        return (other != null) && this.getTagCompound().equals(stripTags(other.getTagCompound()));
    }

    public boolean isTagsEqual(@Nullable FuzzyStack other) {
        return (other != null) && other.stripTags(this.getTagCompound()).equals(this.stripTags(other.getTagCompound()));
    }

    public boolean isItemEqual(@Nullable Item other) {
        return (other != null) && this.stack.getItem().equals(other);
    }

    public boolean isItemEqual(@Nullable ItemStack other) {
        return (other != null) && this.stack.getItem().equals(other.getItem());
    }

    public boolean isItemEqual(@Nullable FuzzyStack other) {
        return (other != null) && this.stack.getItem().equals(other.stack.getItem());
    }

    @Nonnull
    private NBTTagCompound stripTags(@Nullable NBTTagCompound tag) {
        if ((tag == null) || this.ignoreTags.contains("*")) {
            return new NBTTagCompound();
        } else {
            final NBTTagCompound stripped = tag.copy();
            this.ignoreTags.forEach(stripped::removeTag);
            return stripped;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
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
                ItemStack a = this.toStack();
                ItemStack b = other.toStack();
                // To fix quite possibly the dumbest line in Forge code, we must first check that the stacks are not empty.
                // See OreDictionary line 481
                // (https://github.com/MinecraftForge/MinecraftForge/blob/a40df67004dc02c502f7b07d8fe7e6349273514c/src/main/java/net/minecraftforge/oredict/OreDictionary.java#L481)
                int[] ids1 = (a.isEmpty() ? new int[0] : OreDictionary.getOreIDs(a));
                int[] ids2 = (b.isEmpty() ? new int[0] : OreDictionary.getOreIDs(b));
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
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.stack);
        hash = 97 * hash + Objects.hashCode(this.tags);
        hash = 97 * hash + (this.ignoreMeta ? 1 : 0);
        hash = 97 * hash + (this.useOreDict ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.ignoreTags);
        return hash;
    }

    @Override
    public String toString() {
        return this.stack.toString();
    }

}
