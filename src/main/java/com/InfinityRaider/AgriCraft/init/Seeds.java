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
    public static ItemModSeed seedCactus;

    //botania flower seeds
    public static ItemModSeed seedBotaniaWhite;
    public static ItemModSeed seedBotaniaOrange;
    public static ItemModSeed seedBotaniaMagenta;
    public static ItemModSeed seedBotaniaLightBlue;
    public static ItemModSeed seedBotaniaYellow;
    public static ItemModSeed seedBotaniaLime;
    public static ItemModSeed seedBotaniaPink;
    public static ItemModSeed seedBotaniaGray;    //I'm in favor of the British grey, but I'm just being consistent with Vazkii being consistent with Vanilla
    public static ItemModSeed seedBotaniaLightGray;
    public static ItemModSeed seedBotaniaCyan;
    public static ItemModSeed seedBotaniaPurple;
    public static ItemModSeed seedBotaniaBlue;
    public static ItemModSeed seedBotaniaBrown;
    public static ItemModSeed seedBotaniaGreen;
    public static ItemModSeed seedBotaniaRed;
    public static ItemModSeed seedBotaniaBlack;

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
        seedCactus = new ItemModSeed(Crops.cactus, SeedInformation.cactus);

        RegisterHelper.registerSeed(seedPotato, Names.Seeds.seedPotato, Crops.potato);
        RegisterHelper.registerSeed(seedCarrot, Names.Seeds.seedCarrot, Crops.carrot);
        RegisterHelper.registerSeed(seedSugarcane, Names.Seeds.seedSugarcane, Crops.sugarcane);
        RegisterHelper.registerSeed(seedDandelion, Names.Seeds.seedDandelion, Crops.dandelion);
        RegisterHelper.registerSeed(seedPoppy, Names.Seeds.seedPoppy, Crops.poppy);
        RegisterHelper.registerSeed(seedOrchid, Names.Seeds.seedOrchid, Crops.orchid);
        RegisterHelper.registerSeed(seedAllium, Names.Seeds.seedAllium, Crops.allium);
        RegisterHelper.registerSeed(seedTulipRed, Names.Seeds.seedTulipRed, Crops.tulipRed);
        RegisterHelper.registerSeed(seedTulipOrange, Names.Seeds.seedTulipOrange, Crops.tulipOrange);
        RegisterHelper.registerSeed(seedTulipWhite, Names.Seeds.seedTulipWhite, Crops.tulipWhite);
        RegisterHelper.registerSeed(seedTulipPink, Names.Seeds.seedTulipPink, Crops.tulipPink);
        RegisterHelper.registerSeed(seedDaisy, Names.Seeds.seedDaisy, Crops.daisy);
        RegisterHelper.registerSeed(seedCactus, Names.Seeds.seedCactus, Crops.cactus);


        //resource crop seeds
        if(ConfigurationHandler.resourcePlants) {
            //vanilla resources
            seedDiamahlia = new ItemModSeed(ResourceCrops.diamahlia, SeedInformation.diamahlia);
            seedFerranium = new ItemModSeed(ResourceCrops.ferranium, SeedInformation.ferranium);
            seedAurigold = new ItemModSeed(ResourceCrops.aurigold, SeedInformation.aurigold);
            seedLapender = new ItemModSeed(ResourceCrops.lapender, SeedInformation.lapender);
            seedEmeryllis = new ItemModSeed(ResourceCrops.emeryllis, SeedInformation.emeryllis);
            seedRedstodendron = new ItemModSeed(ResourceCrops.redstodendron, SeedInformation.redstodendron);

            RegisterHelper.registerSeed(seedFerranium, Names.Seeds.seedFerranium, ResourceCrops.ferranium);
            RegisterHelper.registerSeed(seedDiamahlia, Names.Seeds.seedDiamahlia, ResourceCrops.diamahlia);
            RegisterHelper.registerSeed(seedAurigold, Names.Seeds.seedAurigold, ResourceCrops.aurigold);
            RegisterHelper.registerSeed(seedLapender, Names.Seeds.seedLapender, ResourceCrops.lapender);
            RegisterHelper.registerSeed(seedEmeryllis, Names.Seeds.seedEmeryllis, ResourceCrops.emeryllis);
            RegisterHelper.registerSeed(seedRedstodendron, Names.Seeds.seedRedstodendron, ResourceCrops.redstodendron);

            if(OreDictHelper.oreCopper!=null) {
                seedCuprosia = new ItemModSeed(ResourceCrops.cuprosia, SeedInformation.cuprosia);
                RegisterHelper.registerSeed(seedCuprosia, Names.Seeds.seedCuprosia, ResourceCrops.cuprosia);
            }
            if(OreDictHelper.oreTin!=null) {
                seedPetinia = new ItemModSeed(ResourceCrops.petinia, SeedInformation.petinia);
                RegisterHelper.registerSeed(seedPetinia, Names.Seeds.seedPetinia, ResourceCrops.petinia);
            }
            if(OreDictHelper.oreLead!=null) {
                seedPlombean = new ItemModSeed(ResourceCrops.plombean, SeedInformation.plombean);
                RegisterHelper.registerSeed(seedPlombean, Names.Seeds.seedPlombean, ResourceCrops.plombean);
            }
            if(OreDictHelper.oreSilver!=null) {
                seedSilverweed = new ItemModSeed(ResourceCrops.silverweed, SeedInformation.silverweed);
                RegisterHelper.registerSeed(seedSilverweed, Names.Seeds.seedSilverweed, ResourceCrops.silverweed);
            }
            if(OreDictHelper.oreAluminum!=null) {
                seedJaslumine = new ItemModSeed(ResourceCrops.jaslumine, SeedInformation.jaslumine);
                RegisterHelper.registerSeed(seedJaslumine, Names.Seeds.seedJaslumine, ResourceCrops.jaslumine);
            }
            if(OreDictHelper.oreNickel!=null) {
                seedNiccissus = new ItemModSeed(ResourceCrops.niccissus, SeedInformation.niccissus);
                RegisterHelper.registerSeed(seedNiccissus, Names.Seeds.seedNiccissus, ResourceCrops.niccissus);
            }
            if(OreDictHelper.orePlatinum!=null) {
                seedPlatiolus = new ItemModSeed(ResourceCrops.platiolus, SeedInformation.platiolus);
                RegisterHelper.registerSeed(seedPlatiolus, Names.Seeds.seedPlatiolus, ResourceCrops.platiolus);
            }
            if(OreDictHelper.oreOsmium!=null) {
                seedOsmonium = new ItemModSeed(ResourceCrops.osmonium, SeedInformation.osmonium);
                RegisterHelper.registerSeed(seedOsmonium, Names.Seeds.seedOsmonium, ResourceCrops.osmonium);
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
