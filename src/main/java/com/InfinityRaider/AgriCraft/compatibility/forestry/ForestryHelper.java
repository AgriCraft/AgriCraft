package com.InfinityRaider.AgriCraft.compatibility.forestry;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.arsmagica.ArsMagicaHelper;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.init.CustomCrops;
import com.InfinityRaider.AgriCraft.init.ResourceCrops;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class ForestryHelper extends ModHelper {

    @Override
    protected void init() {
    }

    @Override
    protected void initPlants() {
    }

    @Override
    protected void postTasks() {
        String seedOil = "seedoil";
        //seeds
        for(ItemModSeed seed : Crops.seeds) {
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] {new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
        }
        //resource crops
        if(ConfigurationHandler.resourcePlants) {
            for(ItemModSeed seed : ResourceCrops.vanillaSeeds) {
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
            }
            for(ItemModSeed seed : ResourceCrops.modSeeds) {
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
            }
        }
        //botania crops
        if(ConfigurationHandler.integration_Botania) {
            for(ItemModSeed seed : BotaniaHelper.botaniaSeeds) {
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
            }
        }
        //ars magica crops
        if(ConfigurationHandler.integration_ArsMagica) {
            for(ItemModSeed seed : ArsMagicaHelper.arsMagicaSeeds) {
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
            }
        }
        //thaumcraft crops
        if(ConfigurationHandler.integration_Thaumcraft) {
            for(ItemModSeed seed : ThaumcraftHelper.thaumcraftSeeds) {
                if(seed.getUnlocalizedName().equals("agricraft:seedTaintedRoot")) {
                    RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
                } else {
                    RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
                }
            }
        }
        //custom crops
        if(ConfigurationHandler.customCrops) {
            for (ItemModSeed seed : CustomCrops.customSeeds) {
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(seed, 1, 0)}, FluidRegistry.getFluidStack(seedOil, 10));
            }
        }
    }

    @Override
    protected String modId() {
        return "Forestry";
    }
}
