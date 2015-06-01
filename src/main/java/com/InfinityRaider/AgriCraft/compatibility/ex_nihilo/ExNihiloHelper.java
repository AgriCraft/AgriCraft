package com.InfinityRaider.AgriCraft.compatibility.ex_nihilo;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ExNihiloHelper extends ModHelper {
    public static final Item seedPotato = (Item) Item.itemRegistry.getObject("exnihilo:seed_potato");
    public static final Item seedCarrot = (Item) Item.itemRegistry.getObject("exnihilo:seed_carrot");
    public static final Item seedSugarCane = (Item) Item.itemRegistry.getObject("exnihilo:seed_sugar_cane");
    public static final Item seedCactus = (Item) Item.itemRegistry.getObject("exnihilo:seed_cactus");

    @Override
    protected void init() {
        //OreDict tags
        OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedCarrot);
        OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedPotato);
        OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedSugarCane);
        OreDictionary.registerOre(Names.OreDict.listAllseed, ExNihiloHelper.seedCactus);

        //seed converting recipes
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedPotato")), new ItemStack(ExNihiloHelper.seedPotato));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedCarrot")), new ItemStack(ExNihiloHelper.seedCarrot));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedSugarcane")), new ItemStack(ExNihiloHelper.seedSugarCane));
        GameRegistry.addShapelessRecipe(new ItemStack((Item) Item.itemRegistry.getObject("AgriCraft:seedCactus")), new ItemStack(ExNihiloHelper.seedCactus));
    }

    @Override
    protected void initPlants() {

    }

    @Override
    protected String modId() {
        return Names.Mods.exNihilo;
    }
}
