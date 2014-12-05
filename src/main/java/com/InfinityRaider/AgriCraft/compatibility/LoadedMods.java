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

    public static void init() {
        nei = Loader.isModLoaded(Names.nei);
        harvestcraft = Loader.isModLoaded(Names.harvestcraft);
        natura = Loader.isModLoaded(Names.natura);
        weeeFlowers = Loader.isModLoaded(Names.weeeFlowers);
        thaumicTinkerer = Loader.isModLoaded(Names.thaumicTinkerer);
        hungerOverhaul = Loader.isModLoaded(Names.hungerOverhaul);
        LogHelper.info("NEI loaded: "+nei);
        LogHelper.info("HarvestCraft loaded: "+harvestcraft);
        LogHelper.info("Natura loaded: "+natura);
        LogHelper.info("Weee Flowers loaded: "+weeeFlowers);
        LogHelper.info("Thaumic tinkerer loaded: "+thaumicTinkerer);
        LogHelper.info("Hunger Overhaul loaded: "+hungerOverhaul);
    }
}
