package com.InfinityRaider.AgriCraft.compatibility.harvestcraft;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

public class HarvestcraftHelper {
    public static ItemSeeds getSeedFromName(String name) {
        String seedItem = Names.seedItem;
        //get the raw seed name
        if(name.indexOf(':')>0) {
            //get rid of 'harvestcraft:' if it's there
            if(name.substring(0,name.indexOf(':')).equalsIgnoreCase(Names.Mods.harvestcraft)) {
                name = name.substring(name.indexOf(":")+1);
            }
            //get rid of the metadata if it's there
            if(name.indexOf(':')>0) {
                name = name.substring(0,name.indexOf(':'));
            }
        }
        //get rid of 'seedItem' if it's there
        if(name.length()>Names.seedItem.length() && name.substring(name.length()-Names.seedItem.length()).equalsIgnoreCase(Names.seedItem)) {
            name = name.substring(0,name.length()-Names.seedItem.length());
        }
        name = Names.Mods.harvestcraft+":"+name+Names.seedItem;
        Object seed = Item.itemRegistry.getObject(name);
        if(seed instanceof  ItemSeeds) {
            return (ItemSeeds) seed;
        }
        else {
            return null;
        }
    }
}
