package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.emoniph.witchery.Witchery;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WitcheryHelper {
    private Item[] seeds = {
            Witchery.Items.SEEDS_BELLADONNA,
            Witchery.Items.SEEDS_MANDRAKE,
            Witchery.Items.SEEDS_ARTICHOKE,
            Witchery.Items.SEEDS_SNOWBELL,
            Witchery.Items.SEEDS_WOLFSBANE,
            Witchery.Items.SEEDS_GARLIC,
            Witchery.Items.SEEDS_WORMWOOD,
    };
    public static void init() {
        OreDictionary.registerOre("seedBelladonna", Witchery.Items.SEEDS_BELLADONNA);
        OreDictionary.registerOre("seedMandrake", Witchery.Items.SEEDS_MANDRAKE);
        OreDictionary.registerOre("seedWaterArtichoke", Witchery.Items.SEEDS_ARTICHOKE);
        OreDictionary.registerOre("seedSnowbell", Witchery.Items.SEEDS_SNOWBELL);
        OreDictionary.registerOre("seedWolfsbane", Witchery.Items.SEEDS_WOLFSBANE);
        OreDictionary.registerOre("seedWitchGarlic", Witchery.Items.SEEDS_GARLIC);
        OreDictionary.registerOre("seedWormwood", Witchery.Items.SEEDS_WORMWOOD);

        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_BELLADONNA);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_MANDRAKE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_ARTICHOKE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_SNOWBELL);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_WOLFSBANE);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_GARLIC);
        OreDictionary.registerOre(Names.OreDict.listAllseed, Witchery.Items.SEEDS_WORMWOOD);

        OreDictionary.registerOre("cropBelladonna", new ItemStack(Witchery.Items.GENERIC, 1, 21));
        OreDictionary.registerOre("cropMandrake", new ItemStack(Witchery.Items.GENERIC, 1, 22));
        OreDictionary.registerOre("cropWaterArtichoke", new ItemStack(Witchery.Items.GENERIC, 1, 69));
        OreDictionary.registerOre("cropSnowbell", new ItemStack(Witchery.Items.GENERIC, 1, 78));
        OreDictionary.registerOre("cropSnowbell", Items.snowball);
        OreDictionary.registerOre("cropWolfsbane", new ItemStack(Witchery.Items.GENERIC, 1, 22));
        OreDictionary.registerOre("cropWitchGarlic", new ItemStack(Witchery.Items.GENERIC, 1, 69));
        OreDictionary.registerOre("cropWormwood", new ItemStack(Witchery.Items.GENERIC, 1, 111));

    }

    public static void initPlants() {

    }
}
