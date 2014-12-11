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

        RegisterHelper.registerItem(seedPotato, Names.seedPotato);
        RegisterHelper.registerItem(seedCarrot, Names.seedCarrot);
        RegisterHelper.registerItem(seedSugarcane, Names.seedSugarcane);
        RegisterHelper.registerItem(seedDandelion, Names.seedDandelion);
        RegisterHelper.registerItem(seedPoppy, Names.seedPoppy);
        RegisterHelper.registerItem(seedOrchid, Names.seedOrchid);
        RegisterHelper.registerItem(seedAllium, Names.seedAllium);
        RegisterHelper.registerItem(seedTulipRed, Names.seedTulipRed);
        RegisterHelper.registerItem(seedTulipOrange, Names.seedTulipOrange);
        RegisterHelper.registerItem(seedTulipWhite, Names.seedTulipWhite);
        RegisterHelper.registerItem(seedTulipPink, Names.seedTulipPink);
        RegisterHelper.registerItem(seedDaisy, Names.seedDaisy);

        OreDictionary.registerOre(Names.listAllseed, Seeds.seedPotato);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedCarrot);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedSugarcane);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedDandelion);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedPoppy);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedOrchid);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedAllium);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedTulipRed);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedTulipOrange);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedTulipWhite);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedTulipPink);
        OreDictionary.registerOre(Names.listAllseed, Seeds.seedDaisy);

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

            RegisterHelper.registerItem(seedFerranium, Names.seedFerranium);
            RegisterHelper.registerItem(seedDiamahlia, Names.seedDiamahlia);
            RegisterHelper.registerItem(seedAurigold, Names.seedAurigold);
            RegisterHelper.registerItem(seedLapender, Names.seedLapender);
            RegisterHelper.registerItem(seedEmeryllis, Names.seedEmeryllis);
            RegisterHelper.registerItem(seedRedstodendron, Names.seedRedstodendron);

            OreDictionary.registerOre(Names.listAllseed, Seeds.seedDiamahlia);
            OreDictionary.registerOre(Names.listAllseed, Seeds.seedFerranium);
            OreDictionary.registerOre(Names.listAllseed, Seeds.seedAurigold);
            OreDictionary.registerOre(Names.listAllseed, Seeds.seedLapender);

            ResourceCrops.diamahlia.initializeSeed(seedDiamahlia);
            ResourceCrops.ferranium.initializeSeed(seedFerranium);
            ResourceCrops.aurigold.initializeSeed(seedAurigold);
            ResourceCrops.lapender.initializeSeed(seedLapender);
            ResourceCrops.emeryllis.initializeSeed(seedEmeryllis);
            ResourceCrops.redstodendron.initializeSeed(seedRedstodendron);

            if(OreDictHelper.oreCopper!=null) {
                seedCuprosia = new ItemModSeed(ResourceCrops.cuprosia, SeedInformation.cuprosia);
                RegisterHelper.registerItem(seedCuprosia, Names.seedCuprosia);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedCuprosia);
                ResourceCrops.cuprosia.initializeSeed(seedCuprosia);
            }
            if(OreDictHelper.oreTin!=null) {
                seedPetinia = new ItemModSeed(ResourceCrops.petinia, SeedInformation.petinia);
                RegisterHelper.registerItem(seedPetinia, Names.seedPetinia);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedPetinia);
                ResourceCrops.petinia.initializeSeed(Seeds.seedPetinia);
            }
            if(OreDictHelper.oreLead!=null) {
                seedPlombean = new ItemModSeed(ResourceCrops.plombean, SeedInformation.plombean);
                RegisterHelper.registerItem(seedPlombean, Names.seedPlombean);
                OreDictionary.registerOre(Names.listAllseed, seedPlombean);
                ResourceCrops.plombean.initializeSeed(Seeds.seedPlombean);
            }
            if(OreDictHelper.oreSilver!=null) {
                seedSilverweed = new ItemModSeed(ResourceCrops.silverweed, SeedInformation.silverweed);
                RegisterHelper.registerItem(seedSilverweed, Names.seedSilverweed);
                OreDictionary.registerOre(Names.listAllseed, seedSilverweed);
                ResourceCrops.silverweed.initializeSeed(Seeds.seedSilverweed);
            }
            if(OreDictHelper.oreAluminum!=null) {
                seedJaslumine = new ItemModSeed(ResourceCrops.jaslumine, SeedInformation.jaslumine);
                RegisterHelper.registerItem(seedJaslumine, Names.seedJaslumine);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedJaslumine);
                ResourceCrops.jaslumine.initializeSeed(Seeds.seedJaslumine);
            }
            if(OreDictHelper.oreNickel!=null) {
                seedNiccissus = new ItemModSeed(ResourceCrops.niccissus, SeedInformation.niccissus);
                RegisterHelper.registerItem(seedNiccissus, Names.seedNiccissus);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedNiccissus);
                ResourceCrops.niccissus.initializeSeed(Seeds.seedNiccissus);
            }
            if(OreDictHelper.orePlatinum!=null) {
                seedPlatiolus = new ItemModSeed(ResourceCrops.platiolus, SeedInformation.platiolus);
                RegisterHelper.registerItem(seedPlatiolus, Names.seedPlatiolus);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedPlatiolus);
                ResourceCrops.platiolus.initializeSeed(Seeds.seedPlatiolus);
            }
            if(OreDictHelper.oreOsmium!=null) {
                seedOsmonium = new ItemModSeed(ResourceCrops.osmonium, SeedInformation.osmonium);
                RegisterHelper.registerItem(seedOsmonium, Names.seedOsmonium);
                OreDictionary.registerOre(Names.listAllseed, Seeds.seedOsmonium);
                ResourceCrops.osmonium.initializeSeed(Seeds.seedOsmonium);
            }
        }

        //register natura seeds to the ore dictionary if natura is installed
        if(ModIntegration.LoadedMods.natura) {
            OreDictionary.registerOre(Names.listAllseed, NContent.plantItem);
        }
        //register ex nihilo seeds to the ore dictionary if ex nihilo is installed
        if(ModIntegration.LoadedMods.exNihilo) {
            OreDictionary.registerOre(Names.listAllseed, ExNihiloHelper.seedCarrot);
            OreDictionary.registerOre(Names.listAllseed, ExNihiloHelper.seedPotato);
            OreDictionary.registerOre(Names.listAllseed, ExNihiloHelper.seedSugarCane);
        }
        //register plant mega pack seeds to the ore dictionary if plant mega pack is installed
        if(ModIntegration.LoadedMods.plantMegaPack) {
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedOnion"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedSpinach"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCelery"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedLettuce"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBellPepperYellow"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCorn"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCucumber"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedTomato"));
            OreDictionary.registerOre(Names.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBeet"));
        }
        LogHelper.info("Seeds registered");

    }

}
