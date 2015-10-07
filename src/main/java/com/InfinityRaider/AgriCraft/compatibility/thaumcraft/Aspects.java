package com.InfinityRaider.AgriCraft.compatibility.thaumcraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.arsmagica.ArsMagicaHelper;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class Aspects {
    public static void registerAspects() {
        //seeds
        for(ItemStack seed : Crops.seeds) {
            ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1));
        }
        //resource crops
        if(ConfigurationHandler.resourcePlants) {
            for(ItemStack seed : ResourceCrops.vanillaSeeds) {
                ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
            }
            for(ItemStack seed : ResourceCrops.modSeeds) {
                ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
            }
        }
        //botania crops
        if(ModHelper.allowIntegration(Names.Mods.botania)) {
            for(ItemStack seed : BotaniaHelper.botaniaSeeds) {
                ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.MAGIC, 1));
            }
        }
        //ars magica crops
        if(ModHelper.allowIntegration(Names.Mods.arsMagica)) {
            for(ItemStack seed : ArsMagicaHelper.arsMagicaSeeds) {
                ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.MAGIC, 1));
            }
        }
        //thaumcraft crops
        if(ModHelper.allowIntegration(Names.Mods.thaumcraft)) {
            for(ItemStack seed : ThaumcraftHelper.thaumcraftSeeds) {
                if(seed.getUnlocalizedName().equals("agricraft:seedTaintedRoot")) {
                    ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.MAGIC, 1).add(Aspect.TAINT, 1));
                } else {
                    ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1).add(Aspect.MAGIC, 1));
                }
            }
        }
        //custom crops
        if(ConfigurationHandler.customCrops) {
            for (ItemStack seed : CustomCrops.customSeeds) {
                ThaumcraftApi.registerObjectTag(seed, new AspectList().add(Aspect.PLANT, 1));
            }
        }
        //seed analyzer
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockSeedAnalyzer, 1, 0), new AspectList().add(Aspect.MIND, 2).add(Aspect.PLANT, 2).add(Aspect.CROP, 2));
        //journal
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.journal, 1, 0), new AspectList().add(Aspect.MIND, 2).add(Aspect.PLANT, 2).add(Aspect.CROP, 2));
        //irrigation systems
        if (!ConfigurationHandler.disableIrrigation) {
            ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockWaterTank, 1, 0), new AspectList().add(Aspect.WATER, 2).add(Aspect.TREE, 6));
            ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockWaterChannel, 1, 0), new AspectList().add(Aspect.WATER, 1).add(Aspect.TREE, 3));
            ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.blockSprinkler, 1, 0), new AspectList().add(Aspect.WATER, 1).add(Aspect.TREE, 1).add(Aspect.METAL, 1).add(Aspect.HARVEST, 1));
        }

    }
}
