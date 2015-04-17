package com.InfinityRaider.AgriCraft.compatibility.thaumcraft;

import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class Aspects {
    public static void registerAspects() {
        //seeds
        for(ItemModSeed seed : Crops.seeds) {
            ThaumcraftApi.registerObjectTag(new ItemStack(seed, 1, 0), new AspectList().add(Aspect.PLANT, 1));
        }
        //resource crops
        if(ConfigurationHandler.resourcePlants) {
            for(ItemModSeed seed : ResourceCrops.vanillaSeeds) {
                ThaumcraftApi.registerObjectTag(new ItemStack(seed, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
            }
            for(ItemModSeed seed : ResourceCrops.modSeeds) {
                ThaumcraftApi.registerObjectTag(new ItemStack(seed, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
            }
        }
        //botania crops
        if(ConfigurationHandler.integration_Botania) {
            for(ItemModSeed seed : BotaniaHelper.botaniaSeeds) {
                ThaumcraftApi.registerObjectTag(new ItemStack(seed, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.MAGIC, 1));
            }
        }
        //custom crops
        for(ItemModSeed seed : CustomCrops.customSeeds) {
            ThaumcraftApi.registerObjectTag(new ItemStack(seed, 1, 0), new AspectList().add(Aspect.PLANT, 1));
        }
        //seed analyzer
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.seedAnalyzer, 1, 0), new AspectList().add(Aspect.MIND, 2).add(Aspect.PLANT, 2).add(Aspect.CROP, 2));
        //journal
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.journal, 1, 0), new AspectList().add(Aspect.MIND, 2).add(Aspect.PLANT, 2).add(Aspect.CROP, 2));
        //irrigation systems
        if (!ConfigurationHandler.disableIrrigation) {
            ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockWaterTank, 1, 0), new AspectList().add(Aspect.WATER, 2).add(Aspect.TREE, 6));
            ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockWaterChannel, 1, 0), new AspectList().add(Aspect.WATER, 1).add(Aspect.TREE, 3));
            ThaumcraftApi.registerObjectTag(new ItemStack(Items.sprinkler, 1, 0), new AspectList().add(Aspect.WATER, 1).add(Aspect.TREE, 1).add(Aspect.METAL, 1).add(Aspect.HARVEST, 1));
        }

    }
}
