package com.InfinityRaider.AgriCraft.compatibility.thaumcraft;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.init.Seeds;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class Aspects {
    public static void registerAspects() {
        if (Loader.isModLoaded("Thaumcraft")) {
            //seeds
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedSugarcane, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedDandelion, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedPoppy, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedOrchid, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedAllium, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedTulipRed, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedTulipOrange, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedTulipWhite, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedTulipPink, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedDaisy, 1, 0), new AspectList().add(Aspect.PLANT, 1));
            //resource crops
            if (ConfigurationHandler.resourcePlants) {
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedFerranium, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.METAL, 1));
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedAurigold, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedRedstodendron, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.ENERGY, 1));
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedLapender, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.SENSES, 1));
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedDiamahlia, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRYSTAL, 1));
                ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedEmeryllis, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRYSTAL, 1));
                if (OreDictHelper.oreCopper != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedCuprosia, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.EXCHANGE, 1));
                }
                if (OreDictHelper.oreTin != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedPetinia, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRYSTAL, 1));
                }
                if (OreDictHelper.oreLead != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedPlombean, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.ORDER, 1));
                }
                if (OreDictHelper.oreSilver != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedSilverweed, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.GREED, 1));
                }
                if (OreDictHelper.oreAluminum != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedJaslumine, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.METAL, 1));
                }
                if (OreDictHelper.oreNickel != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedNiccissus, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.METAL, 1));
                }
                if (OreDictHelper.orePlatinum != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedPlatiolus, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.METAL, 1));
                }
                if (OreDictHelper.oreOsmium != null) {
                    ThaumcraftApi.registerObjectTag(new ItemStack(Seeds.seedOsmonium, 1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.METAL, 1));
                }
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
}
