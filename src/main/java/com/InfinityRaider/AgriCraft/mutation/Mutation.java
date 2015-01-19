package com.InfinityRaider.AgriCraft.mutation;

import com.InfinityRaider.AgriCraft.handler.MutationHandler;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

/**
* User: Simon
* Date: 19.01.2015
*/
public class Mutation {
    public ItemStack result;
    public ItemStack parent1;
    public ItemStack parent2;
    public int id;
    public Block requirement;
    public int requirementMeta;
    public double chance;

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int id, Block requirement, int requirementMeta, int chance) {
        this.result = result;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.id = id;
        this.requirement = requirement;
        this.requirementMeta = requirementMeta;
        this.chance = ((double) chance)/100;
    }

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2) {
        this(result, parent1, parent2, 0, null, 0, 100);
        this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem(), result.getItemDamage());
    }

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int chance) {
        this(result, parent1, parent2, 0, null, 0, chance);
    }

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int id, Block requirement, int requirementMeta) {
        this(result, parent1, parent2, id, requirement, requirementMeta, 100);
        this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem(), result.getItemDamage());
    }

    //copy constructor
    public Mutation(Mutation mutation) {
        this.result = mutation.result;
        this.parent1 = mutation.parent1;
        this.parent2 = mutation.parent2;
        this.id = mutation.id;
        this.requirement = mutation.requirement;
        this.requirementMeta = mutation.requirementMeta;
    }

    public void setChanceOverride(int chance) {
        this.chance = chance;
    }
}
