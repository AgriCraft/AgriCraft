package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.ex_nihilo.ExNihiloHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.SeedInformation;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.common.Loader;
import mods.natura.common.NContent;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class Seeds {
    //vanilla crop seeds
    public static ItemModSeed seedPotato;
    public static ItemModSeed seedCarrot;
    public static ItemModSeed seedSugarcane;
    public static ItemModSeed seedDandelion;
    public static ItemModSeed seedPoppy;
    public static ItemModSeed seedOrchid;
    public static ItemModSeed seedAllium;
    public static ItemModSeed seedTulipRed;
    public static ItemModSeed seedTulipOrange;
    public static ItemModSeed seedTulipWhite;
    public static ItemModSeed seedTulipPink;
    public static ItemModSeed seedDaisy;

    //resource crop seeds
    public static ItemModSeed seedDiamahlia;
    public static ItemModSeed seedFerranium;
    public static ItemModSeed seedAurigold;
    public static ItemModSeed seedLapender;
    public static ItemModSeed seedEmeryllis;
    public static ItemModSeed seedRedstodendron;
    public static ItemModSeed seedCuprosia;
    public static ItemModSeed seedPetinia;
    public static ItemModSeed seedPlombean;
    public static ItemModSeed seedSilverweed;
    public static ItemModSeed seedJaslumine;
    public static ItemModSeed seedNiccissus;
    public static ItemModSeed seedPlatiolus;
    public static ItemModSeed seedOsmonium;

    public static void init() {
        //vanilla crop seeds
        seedPotato = new ItemModSeed(Crops.potato, SeedInformation.potato);
        seedCarrot = new ItemModSeed(Crops.carrot, SeedInformation.carrot);
        seedSugarcane = new ItemModSeed(Crops.sugarcane, SeedInformation.sugarcane);
        seedDandelion = new ItemModSeed(Crops.dandelion, SeedInformation.dandelion);
        seedPoppy = new ItemModSeed(Crops.poppy, SeedInformation.poppy);
        seedOrchid = new ItemModSeed(Crops.orchid, SeedInformation.orchid);
        seedAllium = new ItemModSeed(Crops.allium, SeedInformation.allium);
        seedTulipRed = new ItemModSeed(Crops.tulipRed, SeedInformation.tulipRed);
        seedTulipOrange = new ItemModSeed(Crops.tulipOrange, SeedInformation.tulipOrange);
        seedTulipWhite = new ItemModSeed(Crops.tulipWhite, SeedInformation.tulipWhite);
        seedTulipPink = new ItemModSeed(Crops.tulipPink, SeedInformation.tulipPink);
        seedDaisy = new ItemModSeed(Crops.daisy, SeedInformation.daisy);

        RegisterHelper.registerItem(seedPotato, Names.Seeds.seedPotato);
        RegisterHelper.registerItem(seedCarrot, Names.Seeds.seedCarrot);
        RegisterHelper.registerItem(seedSugarcane, Names.Seeds.seedSugarcane);
        RegisterHelper.registerItem(seedDandelion, Names.Seeds.seedDandelion);
        RegisterHelper.registerItem(seedPoppy, Names.Seeds.seedPoppy);
        RegisterHelper.registerItem(seedOrchid, Names.Seeds.seedOrchid);
        RegisterHelper.registerItem(seedAllium, Names.Seeds.seedAllium);
        RegisterHelper.registerItem(seedTulipRed, Names.Seeds.seedTulipRed);
        RegisterHelper.registerItem(seedTulipOrange, Names.Seeds.seedTulipOrange);
        RegisterHelper.registerItem(seedTulipWhite, Names.Seeds.seedTulipWhite);
        RegisterHelper.registerItem(seedTulipPink, Names.Seeds.seedTulipPink);
        RegisterHelper.registerItem(seedDaisy, Names.Seeds.seedDaisy);

        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedPotato);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedCarrot);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedSugarcane);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedDandelion);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedPoppy);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedOrchid);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedAllium);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedTulipRed);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedTulipOrange);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedTulipWhite);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedTulipPink);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedDaisy);

        Crops.potato.initializeSeed(seedPotato);
        Crops.carrot.initializeSeed(seedCarrot);
        Crops.sugarcane.initializeSeed(seedSugarcane);
        Crops.dandelion.initializeSeed(seedDandelion);
        Crops.poppy.initializeSeed(seedPoppy);
        Crops.orchid.initializeSeed(seedOrchid);
        Crops.allium.initializeSeed(seedAllium);
        Crops.tulipRed.initializeSeed(seedTulipRed);
        Crops.tulipOrange.initializeSeed(seedTulipOrange);
        Crops.tulipWhite.initializeSeed(seedTulipWhite);
        Crops.tulipPink.initializeSeed(seedTulipPink);
        Crops.daisy.initializeSeed(seedDaisy);

        //resource crop seeds
        if(ConfigurationHandler.resourcePlants) {
            //vanilla resources
            seedDiamahlia = new ItemModSeed(ResourceCrops.diamahlia, SeedInformation.diamahlia);
            seedFerranium = new ItemModSeed(ResourceCrops.ferranium, SeedInformation.ferranium);
            seedAurigold = new ItemModSeed(ResourceCrops.aurigold, SeedInformation.aurigold);
            seedLapender = new ItemModSeed(ResourceCrops.lapender, SeedInformation.lapender);
            seedEmeryllis = new ItemModSeed(ResourceCrops.emeryllis, SeedInformation.emeryllis);
            seedRedstodendron = new ItemModSeed(ResourceCrops.redstodendron, SeedInformation.redstodendron);

            RegisterHelper.registerItem(seedFerranium, Names.Seeds.seedFerranium);
            RegisterHelper.registerItem(seedDiamahlia, Names.Seeds.seedDiamahlia);
            RegisterHelper.registerItem(seedAurigold, Names.Seeds.seedAurigold);
            RegisterHelper.registerItem(seedLapender, Names.Seeds.seedLapender);
            RegisterHelper.registerItem(seedEmeryllis, Names.Seeds.seedEmeryllis);
            RegisterHelper.registerItem(seedRedstodendron, Names.Seeds.seedRedstodendron);

            OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedDiamahlia);
            OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedFerranium);
            OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedAurigold);
            OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedLapender);

            ResourceCrops.diamahlia.initializeSeed(seedDiamahlia);
            ResourceCrops.ferranium.initializeSeed(seedFerranium);
            ResourceCrops.aurigold.initializeSeed(seedAurigold);
            ResourceCrops.lapender.initializeSeed(seedLapender);
            ResourceCrops.emeryllis.initializeSeed(seedEmeryllis);
            ResourceCrops.redstodendron.initializeSeed(seedRedstodendron);

            if(OreDictHelper.oreCopper!=null) {
                seedCuprosia = new ItemModSeed(ResourceCrops.cuprosia, SeedInformation.cuprosia);
                RegisterHelper.registerItem(seedCuprosia, Names.Seeds.seedCuprosia);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedCuprosia);
                ResourceCrops.cuprosia.initializeSeed(seedCuprosia);
            }
            if(OreDictHelper.oreTin!=null) {
                seedPetinia = new ItemModSeed(ResourceCrops.petinia, SeedInformation.petinia);
                RegisterHelper.registerItem(seedPetinia, Names.Seeds.seedPetinia);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedPetinia);
                ResourceCrops.petinia.initializeSeed(Seeds.seedPetinia);
            }
            if(OreDictHelper.oreLead!=null) {
                seedPlombean = new ItemModSeed(ResourceCrops.plombean, SeedInformation.plombean);
                RegisterHelper.registerItem(seedPlombean, Names.Seeds.seedPlombean);
                OreDictionary.registerOre(Names.OreDict.listAllseed, seedPlombean);
                ResourceCrops.plombean.initializeSeed(Seeds.seedPlombean);
            }
            if(OreDictHelper.oreSilver!=null) {
                seedSilverweed = new ItemModSeed(ResourceCrops.silverweed, SeedInformation.silverweed);
                RegisterHelper.registerItem(seedSilverweed, Names.Seeds.seedSilverweed);
                OreDictionary.registerOre(Names.OreDict.listAllseed, seedSilverweed);
                ResourceCrops.silverweed.initializeSeed(Seeds.seedSilverweed);
            }
            if(OreDictHelper.oreAluminum!=null) {
                seedJaslumine = new ItemModSeed(ResourceCrops.jaslumine, SeedInformation.jaslumine);
                RegisterHelper.registerItem(seedJaslumine, Names.Seeds.seedJaslumine);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedJaslumine);
                ResourceCrops.jaslumine.initializeSeed(Seeds.seedJaslumine);
            }
            if(OreDictHelper.oreNickel!=null) {
                seedNiccissus = new ItemModSeed(ResourceCrops.niccissus, SeedInformation.niccissus);
                RegisterHelper.registerItem(seedNiccissus, Names.Seeds.seedNiccissus);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedNiccissus);
                ResourceCrops.niccissus.initializeSeed(Seeds.seedNiccissus);
            }
            if(OreDictHelper.orePlatinum!=null) {
                seedPlatiolus = new ItemModSeed(ResourceCrops.platiolus, SeedInformation.platiolus);
                RegisterHelper.registerItem(seedPlatiolus, Names.Seeds.seedPlatiolus);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedPlatiolus);
                ResourceCrops.platiolus.initializeSeed(Seeds.seedPlatiolus);
            }
            if(OreDictHelper.oreOsmium!=null) {
                seedOsmonium = new ItemModSeed(ResourceCrops.osmonium, SeedInformation.osmonium);
                RegisterHelper.registerItem(seedOsmonium, Names.Seeds.seedOsmonium);
                OreDictionary.registerOre(Names.OreDict.listAllseed, Seeds.seedOsmonium);
                ResourceCrops.osmonium.initializeSeed(Seeds.seedOsmonium);
            }
        }

        //register natura seeds to the ore dictionary if natura is installed
        if(ModIntegration.LoadedMods.natura) {
            OreDictionary.registerOre(Names.OreDict.listAllseed, NContent.plantItem);
        }
        //register ex nihilo seeds to the ore dictionary if ex nihilo is installed
        if(ModIntegration.LoadedMods.exNihilo) {
            OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedCarrot);
            OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedPotato);
            OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedSugarCane);
        }
        //register plant mega pack seeds to the ore dictionary if plant mega pack is installed
        if(ModIntegration.LoadedMods.plantMegaPack) {
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedOnion"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedSpinach"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCelery"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedLettuce"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBellPepperYellow"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCorn"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCucumber"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedTomato"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBeet"));
        }
        //register witchery seeds to the ore dictionary if witchery is installed
        if(Loader.isModLoaded("witchery")) {
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("witchery:seedsbelladonna"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("witchery:seedsmandrake"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("witchery:seedsartichoke"));
            OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("witchery:seedssnowbell"));
        }
        LogHelper.info("Seeds registered");

    }

}
