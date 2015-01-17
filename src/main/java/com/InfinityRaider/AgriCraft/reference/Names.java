package com.InfinityRaider.AgriCraft.reference;

public final class Names {
    //NBT tags
    public static class NBT {
        public static final String growth = "growth";
        public static final String gain = "gain";
        public static final String strength = "strength";
        public static final String analyzed = "analyzed";
        public static final String crossCrop = "crossCrop";
        public static final String weed = "weed";
        public static final String discoveredSeeds = "discoveredSeeds";
        public static final String currentPage = "currentPage";
        public static final String wood = "wood";
        public static final String connected = "nrTanks";
        public static final String level = "level";
        public static final String id = "id";
        public static final String meta = "meta";
        public static final String material = "material";
        public static final String materialMeta = "matMeta";
        public static final String angle = "angle";
        public static final String isSprinkled = "isSprinkled";
        public static final String power = "powerrrr";
    }

    //mod objects
    public static class Objects {
        public static final String crop = "crop";
        public static final String crops = "crops";
        public static final String farmland = "farmland";
        public static final String analyzer = "Analyzer";
        public static final String seed = "seed";
        public static final String seedAnalyzer = seed+analyzer;
        public static final String journal = "journal";
        public static final String trowel = "trowel";
        public static final String magnifyingGlass = "magnifyingGlass";
        public static final String nugget = "nugget";
        public static final String ingot = "ingot";
        public static final String ore = "ore";
        public static final String tank = "waterTank";
        public static final String channel = "waterChannel";
        public static final String valve = "channelValve";
        public static final String sprinkler = "sprinkler";
    }

    //tile entities
    public static class TileEntity {
        public static final String tileEntity = "TileEntity";
    }

    //resources
    public static class Resources {
        public static final String iron = "Iron";
        public static final String diamond = "Diamond";
        public static final String emerald = "Emerald";
        public static final String copper = "Copper";
        public static final String tin = "Tin";
        public static final String lead = "Lead";
        public static final String silver = "Silver";
        public static final String aluminum = "Aluminum";
        public static final String nickel = "Nickel";
        public static final String platinum = "Platinum";
        public static final String osmium = "Osmium";
    }

    //colors
    public static class Colors {
        public static final String red = "Red";
        public static final String orange = "Orange";
        public static final String white = "White";
        public static final String pink = "Pink";
    }

    //plants
    public static class Plants {
        public static final String potato = "Potato";
        public static final String carrot = "Carrot";
        public static final String melon = "Melon";
        public static final String pumpkin = "Pumpkin";
        public static final String sugarcane = "Sugarcane";
        public static final String dandelion = "Dandelion";
        public static final String poppy = "Poppy";
        public static final String orchid = "Orchid";
        public static final String allium = "Allium";
        public static final String tulip = "Tulip";
        public static final String daisy = "Daisy";
        public static final String diamahlia = "Diamahlia";
        public static final String aurigold = "Aurigold";
        public static final String ferranium = "Ferranium";
        public static final String lapender = "Lapender";
        public static final String emeryllis = "Emeryllis";
        public static final String redstodendron = "Redstodendron";
        public static final String cuprosia = "Cuprosia";
        public static final String petinia = "Petinia";
        public static final String plombean = "Plombean";
        public static final String silverweed = "Silverweed";
        public static final String jaslumine = "Jaslumine";
        public static final String niccissus = "Niccissus";
        public static final String platiolus = "Platiolus";
        public static final String osmonium = "Osmonium";
    }

    //nuggets
    public static class Nuggets {
        public static final String nuggetDiamond = Objects.nugget+ Resources.diamond;
        public static final String nuggetIron = Objects.nugget+ Resources.iron;
        public static final String nuggetEmerald = Objects.nugget+ Resources.emerald;
        public static final String nuggetCopper = Objects.nugget+ Resources.copper;
        public static final String nuggetTin = Objects.nugget+ Resources.tin;
        public static final String nuggetLead = Objects.nugget+ Resources.lead;
        public static final String nuggetSilver = Objects.nugget+ Resources.silver;
        public static final String nuggetAluminum = Objects.nugget+ Resources.aluminum;
        public static final String nuggetNickel = Objects.nugget+ Resources.nickel;
        public static final String nuggetPlatinum = Objects.nugget+ Resources.platinum;
        public static final String nuggetOsmium = Objects.nugget+ Resources.osmium;
    }

    //ingots
    public static class Ingots {
        public static final String ingotCopper = Objects.ingot+ Resources.copper;
        public static final String ingotTin = Objects.ingot+ Resources.tin;
        public static final String ingotLead = Objects.ingot+ Resources.lead;
        public static final String ingotSilver = Objects.ingot+ Resources.silver;
        public static final String ingotAluminum = Objects.ingot+ Resources.aluminum;
        public static final String ingotNickel = Objects.ingot+ Resources.nickel;
        public static final String ingotPlatinum = Objects.ingot+ Resources.platinum;
        public static final String ingotOsmium = Objects.ingot+ Resources.osmium;
    }

    //ores
    public static class Ores {
        public static final String oreCopper = Objects.ore+ Resources.copper;
        public static final String oreTin = Objects.ore+ Resources.tin;
        public static final String oreLead = Objects.ore+ Resources.lead;
        public static final String oreSilver = Objects.ore+ Resources.silver;
        public static final String oreAluminum = Objects.ore+ Resources.aluminum;
        public static final String oreNickel = Objects.ore+ Resources.nickel;
        public static final String orePlatinum = Objects.ore+ Resources.platinum;
        public static final String oreOsmium = Objects.ore+ Resources.osmium;
    }

    //crops
    public static class Crops {
        public static final String cropPotato = Objects.crop+ Plants.potato;
        public static final String cropCarrot = Objects.crop+ Plants.carrot;
        public static final String cropMelon = Objects.crop+ Plants.melon;
        public static final String cropPumpkin = Objects.crop+ Plants.pumpkin;
        public static final String cropSugarcane = Objects.crop+ Plants.sugarcane;
        public static final String cropDandelion = Objects.crop+ Plants.dandelion;
        public static final String cropPoppy = Objects.crop+ Plants.poppy;
        public static final String cropOrchid = Objects.crop+ Plants.orchid;
        public static final String cropAllium = Objects.crop+ Plants.allium;
        public static final String cropTulipRed = Objects.crop+ Plants.tulip+ Colors.red;
        public static final String cropTulipOrange = Objects.crop+ Plants.tulip+ Colors.orange;
        public static final String cropTulipWhite = Objects.crop+ Plants.tulip+ Colors.white;
        public static final String cropTulipPink = Objects.crop+ Plants.tulip+ Colors.pink;
        public static final String cropDaisy = Objects.crop+ Plants.daisy;
        public static final String cropDiamahlia = Objects.crop+ Plants.diamahlia;
        public static final String cropAurigold = Objects.crop+ Plants.aurigold;
        public static final String cropFerranium = Objects.crop+ Plants.ferranium;
        public static final String cropLapender = Objects.crop+ Plants.lapender;
        public static final String cropEmeryllis = Objects.crop+ Plants.emeryllis;
        public static final String cropRedstodendron = Objects.crop+ Plants.redstodendron;
        public static final String cropCuprosia = Objects.crop+ Plants.cuprosia;
        public static final String cropPetinia = Objects.crop+ Plants.petinia;
        public static final String cropPlombean = Objects.crop+ Plants.plombean;
        public static final String cropSilverweed = Objects.crop+ Plants.silverweed;
        public static final String cropJaslumine = Objects.crop+ Plants.jaslumine;
        public static final String cropNiccissus = Objects.crop+ Plants.niccissus;
        public static final String cropPlatiolus = Objects.crop+ Plants.platiolus;
        public static final String cropOsmonium = Objects.crop+ Plants.osmonium;
    }

    //seeds
    public static class Seeds {
        public static final String seedPotato = Objects.seed+ Plants.potato;
        public static final String seedCarrot = Objects.seed+ Plants.carrot;
        public static final String seedSugarcane = Objects.seed+ Plants.sugarcane;
        public static final String seedDandelion = Objects.seed+ Plants.dandelion;
        public static final String seedPoppy = Objects.seed+ Plants.poppy;
        public static final String seedOrchid = Objects.seed+ Plants.orchid;
        public static final String seedAllium = Objects.seed+ Plants.allium;
        public static final String seedTulipRed = Objects.seed+ Plants.tulip+ Colors.red;
        public static final String seedTulipOrange = Objects.seed+ Plants.tulip+ Colors.orange;
        public static final String seedTulipWhite = Objects.seed+ Plants.tulip+ Colors.white;
        public static final String seedTulipPink = Objects.seed+ Plants.tulip+ Colors.pink;
        public static final String seedDaisy = Objects.seed+ Plants.daisy;
        public static final String seedDiamahlia = Objects.seed+ Plants.diamahlia;
        public static final String seedAurigold = Objects.seed+ Plants.aurigold;
        public static final String seedFerranium = Objects.seed+ Plants.ferranium;
        public static final String seedLapender = Objects.seed+ Plants.lapender;
        public static final String seedEmeryllis = Objects.seed+ Plants.emeryllis;
        public static final String seedRedstodendron = Objects.seed+ Plants.redstodendron;
        public static final String seedCuprosia = Objects.seed+ Plants.cuprosia;
        public static final String seedPetinia = Objects.seed+ Plants.petinia;
        public static final String seedPlombean = Objects.seed+ Plants.plombean;
        public static final String seedSilverweed = Objects.seed+ Plants.silverweed;
        public static final String seedJaslumine = Objects.seed+ Plants.jaslumine;
        public static final String seedNiccissus = Objects.seed+ Plants.niccissus;
        public static final String seedPlatiolus = Objects.seed+ Plants.platiolus;
        public static final String seedOsmonium = Objects.seed+ Plants.osmonium;
    }

    //ore dictionary entries
    public static class OreDict {
        public static final String listAllseed = "listAllseed";
        public static final String plankWood = "plankWood";
        public static final String slabWood = "slabWood";
    }

    //mod ids
    public static class Mods {
        public static final String harvestcraft = "harvestcraft";
        public static final String natura = "Natura";
        public static final String weeeFlowers = "weeeflowers";
        public static final String forestry = "Forestry";
        public static final String nei = "NotEnoughItems";
        public static final String thaumicTinkerer = "ThaumicTinkerer";
        public static final String hungerOverhaul= "HungerOverhaul";
        public static final String exNihilo = "exnihilo";
        public static final String plantMegaPack = "plantmegapack";
        public static final String magicalCrops = "magicalcrops";
        public static final String railcraft = "Railcraft";
        public static final String thaumcraft = "Thaumcraft";
        public static final String mfr = "MineFactoryReloaded";
        public static final String waila = "Waila";
        public static final String chococraft = "chococraft";
        public static final String mcMultipart = "McMultipart";
        public static final String minetweaker = "MineTweaker3";
        public static final String extraUtilities = "ExtraUtilities";
    }

    //item registry keywords
    public static final String seedItem = "seedItem";


}
