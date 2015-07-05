package com.InfinityRaider.AgriCraft.compatibility;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Loader;

public class LoadedMods {
    public static boolean forestry;
    public static boolean mcMultipart;
    public static boolean gardenStuff;

    public static void init() {
        forestry = Loader.isModLoaded(Names.Mods.forestry);
        mcMultipart = Loader.isModLoaded(Names.Mods.mcMultipart);
        gardenStuff = Loader.isModLoaded(Names.Mods.gardenStuff);
    }
}
