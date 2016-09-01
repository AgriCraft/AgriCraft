/*
 */
package com.infinityraider.agricraft.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class representing a fuzzy ItemStack.
 */
public class FuzzyStack {

    private final ItemStack stack;

    private final boolean ignoreMeta;
    private final boolean ignoreTags;
    private final boolean useOreDict;

    public FuzzyStack(ItemStack stack, boolean ignoreMeta, boolean ignoreTags, boolean useOreDict) {
        this.stack = stack;
        this.ignoreMeta = ignoreMeta;
        this.ignoreTags = ignoreTags;
        this.useOreDict = useOreDict;

        if (this.ignoreMeta) {
            this.stack.setItemDamage(0);
        }

        if (this.ignoreTags) {
            this.stack.setTagCompound(null);
        }
    }

    public ItemStack toStack() {
        return stack.copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof FuzzyStack) {
            FuzzyStack other = (FuzzyStack) obj;

            if ((this.stack.equals(other.stack))
                    || ((this.stack.isItemEqual(other.stack))
                    && ((this.ignoreMeta) || (other.ignoreMeta) || (this.stack.getMetadata() == other.stack.getMetadata()))
                    && ((this.ignoreTags) || (other.ignoreTags) || (this.stack.getTagCompound().equals(other.stack.getTagCompound()))))) {
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
