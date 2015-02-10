package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;


public class Mutation {

    public ItemStack result;
    public ItemStack parent1;
    public ItemStack parent2;
    public double chance;

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int chance) {
        this.result = result;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.chance = ((double) chance)/100;
    }

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2) {
        this(result, parent1, parent2, 100);
        this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem(), result.getItemDamage());
    }

    //copy constructor
    public Mutation(Mutation mutation) {
        this.result = mutation.result;
        this.parent1 = mutation.parent1;
        this.parent2 = mutation.parent2;
    }

    public void setChanceOverride(int chance) {
        this.chance = chance;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if(object instanceof Mutation) {
            Mutation mutation = (Mutation) object;
            if(this.chance==mutation.chance) {
                if(this.result.isItemEqual(mutation.result)) {
                    if(this.parent1.isItemEqual(mutation.parent1) && this.parent2.isItemEqual(mutation.parent2)) {
                        isEqual = true;
                    }
                    else if(this.parent1.isItemEqual(mutation.parent2) && this.parent2.isItemEqual(mutation.parent1)) {
                        isEqual = true;
                    }
                }
            }
        }
        return isEqual;
    }
}
