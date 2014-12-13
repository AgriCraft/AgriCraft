package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.*;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class Items {
    public static ItemCrop crops;
    public static ItemSprinkler sprinkler;
    public static ItemJournal journal;
    public static ItemTrowel trowel;
    public static ItemMagnifyingGlass magnifyingGlass;
    public static ItemDebugger debugItem;

    //fruits
    public static Item nuggetDiamond;
    public static Item nuggetEmerald;
    public static Item nuggetIron;
    public static Item nuggetCopper;
    public static Item nuggetTin;
    public static Item nuggetLead;
    public static Item nuggetSilver;
    public static Item nuggetAluminum;
    public static Item nuggetNickel;
    public static Item nuggetPlatinum ;
    public static Item nuggetOsmium;

    public static int nuggetDiamondMeta = 0;
    public static int nuggetEmeraldMeta = 0;
    public static int nuggetIronMeta = 0;
    public static int nuggetCopperMeta = 0;
    public static int nuggetTinMeta = 0;
    public static int nuggetLeadMeta = 0;
    public static int nuggetSilverMeta = 0;
    public static int nuggetAluminumMeta = 0;
    public static int nuggetNickelMeta = 0;
    public static int nuggetPlatinumMeta = 0;
    public static int nuggetOsmiumMeta = 0;

    public static void init() {
        crops = new ItemCrop();
        RegisterHelper.registerItem(crops, Names.crops+"Item");
        journal = new ItemJournal();
        RegisterHelper.registerItem(journal, Names.journal);
        trowel = new ItemTrowel();
        RegisterHelper.registerItem(trowel, Names.trowel);
        magnifyingGlass = new ItemMagnifyingGlass();
        RegisterHelper.registerItem(magnifyingGlass, Names.magnifyingGlass);
        if(!ConfigurationHandler.disableIrrigation) {
            sprinkler = new ItemSprinkler();
            RegisterHelper.registerItem(sprinkler, Names.sprinkler + "Item");
        }
        if(ConfigurationHandler.debug) {
            debugItem = new ItemDebugger();
            RegisterHelper.registerItem(debugItem, "debugger");
        }
        LogHelper.info("Items Registered");
}

    //fruits
    public static void initFruits() {
        //diamond nugget
        if(nuggetDiamond==null) {
            nuggetDiamond = new ModItem();
            RegisterHelper.registerItem(nuggetDiamond,Names.nuggetDiamond);
            OreDictionary.registerOre(Names.nuggetDiamond, Items.nuggetDiamond);
        }
        //emerald nugget
        if(nuggetEmerald==null) {
            nuggetEmerald = new ModItem();
            RegisterHelper.registerItem(nuggetEmerald,Names.nuggetEmerald);
            OreDictionary.registerOre(Names.nuggetEmerald, Items.nuggetEmerald);
        }
        //iron nugget
        if(nuggetIron==null) {
            nuggetIron = new ModItem();
            RegisterHelper.registerItem(nuggetIron, Names.nuggetIron);
            OreDictionary.registerOre(Names.nuggetIron, Items.nuggetIron);
        }
        //copper nugget
        if(OreDictHelper.oreCopper!=null) {
            if (nuggetCopper == null) {
                nuggetCopper = new ModItem();
                RegisterHelper.registerItem(nuggetCopper, Names.nuggetCopper);
                OreDictionary.registerOre(Names.nuggetCopper, Items.nuggetCopper);
            }
        }
        //tin nugget
        if(OreDictHelper.oreTin!=null) {
            if (nuggetTin == null) {
                nuggetTin = new ModItem();
                RegisterHelper.registerItem(nuggetTin, Names.nuggetTin);
                OreDictionary.registerOre(Names.nuggetTin, Items.nuggetTin);
            }
        }
        //lead nugget
        if(OreDictHelper.oreLead!=null) {
            if (nuggetLead == null) {
                nuggetLead = new ModItem();
                RegisterHelper.registerItem(nuggetLead, Names.nuggetLead);
                OreDictionary.registerOre(Names.nuggetLead, Items.nuggetLead);
            }
        }
        //silver nugget
        if(OreDictHelper.oreSilver!=null) {
            if (nuggetSilver == null){
                nuggetSilver = new ModItem();
                RegisterHelper.registerItem(nuggetSilver, Names.nuggetSilver);
                OreDictionary.registerOre(Names.nuggetSilver, Items.nuggetSilver);
            }
        }
        //aluminum nugget
        if(OreDictHelper.oreAluminum!=null) {
            if (nuggetAluminum == null) {
                nuggetAluminum = new ModItem();
                RegisterHelper.registerItem(nuggetAluminum, Names.nuggetAluminum);
                OreDictionary.registerOre(Names.nuggetAluminum, Items.nuggetAluminum);
            }
        }
        //nickel nugget
        if(OreDictHelper.oreNickel!=null) {
            if (nuggetNickel == null) {
                nuggetNickel = new ModItem();
                RegisterHelper.registerItem(nuggetNickel, Names.nuggetNickel);
                OreDictionary.registerOre(Names.nuggetNickel, Items.nuggetNickel);
            }
        }
        //platinum nugget
        if(OreDictHelper.orePlatinum!=null) {
            if (nuggetPlatinum == null) {
                nuggetPlatinum = new ModItem();
                RegisterHelper.registerItem(nuggetPlatinum, Names.nuggetPlatinum);
                OreDictionary.registerOre(Names.nuggetPlatinum, Items.nuggetPlatinum);
            }
        }
        //osmium nugget
        if(OreDictHelper.oreOsmium!=null) {
            if (nuggetOsmium == null) {
                nuggetOsmium = new ModItem();
                RegisterHelper.registerItem(nuggetOsmium, Names.nuggetOsmium);
                OreDictionary.registerOre(Names.nuggetOsmium, Items.nuggetOsmium);
            }
        }
    }
}
