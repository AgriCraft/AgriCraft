package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.Loader;

public class LoadedMods {
    public static boolean nei;
    public static boolean harvestcraft;
    public static boolean natura;
    public static boolean thaumicTinkerer;
    public static boolean hungerOverhaul;
    public static boolean weeeFlowers;
    public static boolean exNihilo;
    public static boolean plantMegaPack;
    public static boolean railcraft;

    public static void init() {
        nei = Loader.isModLoaded(Names.nei);
        harvestcraft = Loader.isModLoaded(Names.harvestcraft);
        natura = Loader.isModLoaded(Names.natura);
        weeeFlowers = Loader.isModLoaded(Names.weeeFlowers);
        thaumicTinkerer = Loader.isModLoaded(Names.thaumicTinkerer);
        hungerOverhaul = Loader.isModLoaded(Names.hungerOverhaul);
        exNihilo = Loader.isModLoaded(Names.exNihilo);
        plantMegaPack = Loader.isModLoaded(Names.plantMegaPack);
        railcraft = Loader.isModLoaded(Names.railcraft);
        LogHelper.info("Checking for loaded mods:");
        LogHelper.info(" - NEI loaded: "+nei);
        LogHelper.info(" - Pam's HarvestCraft loaded: "+harvestcraft);
        LogHelper.info(" - Natura loaded: "+natura);
        LogHelper.info(" - Pam's Weee Flowers loaded: "+weeeFlowers);
        LogHelper.info(" - Thaumic tinkerer loaded: "+thaumicTinkerer);
        LogHelper.info(" - Hunger Overhaul loaded: "+hungerOverhaul);
        LogHelper.info(" - Ex Nihilo loaded: "+exNihilo);
        LogHelper.info(" - Plant Mega Pack loaded: "+plantMegaPack);
        LogHelper.info(" - Railcraft loaded: "+railcraft);
        LogHelper.info("Done");
    }
}
