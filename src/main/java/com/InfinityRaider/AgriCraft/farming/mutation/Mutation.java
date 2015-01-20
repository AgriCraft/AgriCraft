package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;


public class Mutation {

    public static final int DEFAULT_ID = 0;
    public static final int DEFAULT_REQUIREMENT_META = 0;

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
        this(result, parent1, parent2, DEFAULT_ID, null, DEFAULT_REQUIREMENT_META, 100);
        this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem(), result.getItemDamage());
    }

    public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int chance) {
        this(result, parent1, parent2, DEFAULT_ID, null, DEFAULT_REQUIREMENT_META, chance);
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
