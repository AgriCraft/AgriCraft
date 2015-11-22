package com.InfinityRaider.AgriCraft.compatibility.ex_nihilo;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ExNihiloHelper extends ModHelper {
    @Override
    protected void onInit() {
        Item seedPotato = (Item) Item.itemRegistry.getObject("exnihilo:seed_potato");
        Item seedCarrot = (Item) Item.itemRegistry.getObject("exnihilo:seed_carrot");
        Item seedSugarCane = (Item) Item.itemRegistry.getObject("exnihilo:seed_sugar_cane");
        Item seedCactus = (Item) Item.itemRegistry.getObject("exnihilo:seed_cactus");
        //OreDict tags
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedCarrot);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedPotato);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedSugarCane);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seedCactus);

        //seed converting recipes
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedPotato")), new ItemStack(seedPotato));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedCarrot")), new ItemStack(seedCarrot));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedSugarcane")), new ItemStack(seedSugarCane));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedCactus")), new ItemStack(seedCactus));
    }

    @Override
    protected String modId() {
        return Names.Mods.exNihilo;
    }
}
