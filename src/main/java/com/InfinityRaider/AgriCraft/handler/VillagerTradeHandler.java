package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.init.WorldGen;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class VillagerTradeHandler implements VillagerRegistry.IVillageTradeHandler{

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
        if (villager.getProfession() == WorldGen.getVillagerId()){
            for (int i = 0; i < 10; ++i) {
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.emerald, 20 + random.nextInt(12)), new ItemStack(Items.wheat_seeds, 1 + random.nextInt(5)), SeedHelper.getRandomSeed(random, false)));
                recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.emerald, 40 + random.nextInt(24)), new ItemStack(Items.wheat_seeds, 1 + random.nextInt(8)), SeedHelper.getRandomSeed(random, true)));
            }
        }
    }
}
