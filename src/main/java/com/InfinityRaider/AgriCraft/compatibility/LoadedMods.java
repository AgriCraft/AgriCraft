package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.Loader;

public class LoadedMods {
    public static boolean bluePower;
    public static boolean nei;
    public static boolean harvestcraft;
    public static boolean natura;
    public static boolean thaumicTinkerer;
    public static boolean hungerOverhaul;
    public static boolean weeeFlowers;
    public static boolean exNihilo;
    public static boolean plantMegaPack;
    public static boolean magicalCrops;
    public static boolean railcraft;
    public static boolean thaumcraft;
    public static boolean mfr;
    public static boolean waila;
    public static boolean forestry;
    public static boolean chococraft;
    public static boolean mcMultipart;
    public static boolean minetweaker;
    public static boolean extraUtilities;
    public static boolean botania;
    public static boolean tconstruct;
    public static boolean gardenStuff;
    public static boolean psychedelicraft;
    public static boolean witchery;

    public static void init() {
        bluePower = Loader.isModLoaded(Names.Mods.bluePower);
        nei = Loader.isModLoaded(Names.Mods.nei);
        harvestcraft = Loader.isModLoaded(Names.Mods.harvestcraft);
        natura = Loader.isModLoaded(Names.Mods.natura);
        weeeFlowers = Loader.isModLoaded(Names.Mods.weeeFlowers);
        forestry = Loader.isModLoaded(Names.Mods.forestry);
        thaumicTinkerer = Loader.isModLoaded(Names.Mods.thaumicTinkerer);
        hungerOverhaul = Loader.isModLoaded(Names.Mods.hungerOverhaul);
        exNihilo = Loader.isModLoaded(Names.Mods.exNihilo);
        plantMegaPack = Loader.isModLoaded(Names.Mods.plantMegaPack);
        magicalCrops = Loader.isModLoaded(Names.Mods.magicalCrops);
        railcraft = Loader.isModLoaded(Names.Mods.railcraft);
        thaumcraft = Loader.isModLoaded(Names.Mods.thaumcraft);
        mfr = Loader.isModLoaded(Names.Mods.mfr);
        waila = Loader.isModLoaded(Names.Mods.waila);
        chococraft = Loader.isModLoaded(Names.Mods.chococraft);
        mcMultipart = Loader.isModLoaded(Names.Mods.mcMultipart);
        minetweaker = Loader.isModLoaded(Names.Mods.minetweaker);
        extraUtilities = Loader.isModLoaded(Names.Mods.extraUtilities);
        botania = Loader.isModLoaded(Names.Mods.botania);
        tconstruct = Loader.isModLoaded(Names.Mods.tconstruct);
        gardenStuff = Loader.isModLoaded(Names.Mods.gardenStuff);
        psychedelicraft = Loader.isModLoaded(Names.Mods.psychedelicraft);
        witchery = Loader.isModLoaded(Names.Mods.witchery);

        LogHelper.debug("Checking for loaded mods:");
        LogHelper.debug(" - NEI loaded: " + nei);
        LogHelper.debug(" - Pam's HarvestCraft loaded: " + harvestcraft);
        LogHelper.debug(" - Natura loaded: " + natura);
        LogHelper.debug(" - Pam's Weee Flowers loaded: " + weeeFlowers);
        LogHelper.debug(" - Forestry loaded: " + forestry);
        LogHelper.debug(" - Thaumic tinkerer loaded: " + thaumicTinkerer);
        LogHelper.debug(" - Hunger Overhaul loaded: " + hungerOverhaul);
        LogHelper.debug(" - Ex Nihilo loaded: " + exNihilo);
        LogHelper.debug(" - Plant Mega Pack loaded: " + plantMegaPack);
        LogHelper.debug(" - Magical Crops loaded: " + magicalCrops);
        LogHelper.debug(" - Railcraft loaded: " + railcraft);
        LogHelper.debug(" - Thaumcraft loaded: " + thaumcraft);
        LogHelper.debug(" - MineFactory Reloaded loaded: " + mfr);
        LogHelper.debug(" - Waila loaded: " + waila);
        LogHelper.debug(" - Chococraft loaded: " + chococraft);
        LogHelper.debug(" - McMultipart loaded: " + mcMultipart);
        LogHelper.debug(" - MineTweaker loaded: " + minetweaker);
        LogHelper.debug(" - ExtraUtilities loaded: " + extraUtilities);
        LogHelper.debug(" - Botania loaded: " + botania);
        LogHelper.debug(" - Tinker's Construct loaded: " + tconstruct);
        LogHelper.debug(" - Garden Stuff loaded: " + gardenStuff);
        LogHelper.debug(" - Psychedelicraft loaded: " + psychedelicraft);
        LogHelper.debug(" - Witchery loaded: " + witchery);
        LogHelper.debug("Done");
    }
}
