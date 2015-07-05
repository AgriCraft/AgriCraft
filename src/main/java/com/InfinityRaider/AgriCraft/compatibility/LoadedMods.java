package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Loader;

public class LoadedMods {
    public static boolean forestry;
    public static boolean mcMultipart;
    public static boolean extraUtilities;
    public static boolean tconstruct;
    public static boolean gardenStuff;

    public static void init() {
        forestry = Loader.isModLoaded(Names.Mods.forestry);
        mcMultipart = Loader.isModLoaded(Names.Mods.mcMultipart);
        extraUtilities = Loader.isModLoaded(Names.Mods.extraUtilities);
        tconstruct = Loader.isModLoaded(Names.Mods.tconstruct);
        gardenStuff = Loader.isModLoaded(Names.Mods.gardenStuff);
    }
}
