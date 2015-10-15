package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public abstract class RegisterHelper {
    public static void registerBlock(Block block,String name) {
        RegisterHelper.registerBlock(block, name, null);
    }

    public static void registerBlock(Block block,String name, Class<? extends ItemBlock> itemClass) {
        block.setBlockName(Reference.MOD_ID.toLowerCase()+':'+name);
        LogHelper.info("registering " + block.getUnlocalizedName());
        if(itemClass!=null) {
            GameRegistry.registerBlock(block, itemClass, name);
        }
        else {
            GameRegistry.registerBlock(block, name);
        }
    }

    public static void registerCrop(BlockModPlant plant, String name) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        registerBlock(plant, Names.Objects.crop + name);
        if(ConfigurationHandler.registerCropProductsToOreDict) {
            for (ItemStack fruit : plant.products.getAllProducts()) {
                if (fruit != null && fruit.getItem() != null && !OreDictHelper.hasOreId(fruit, Names.Objects.crop + name)) {
                    OreDictionary.registerOre(Names.Objects.crop + name, fruit);
                }
            }
        }
    }

    public static void registerItem(Item item,String name) {
        item.setUnlocalizedName(Reference.MOD_ID.toLowerCase()+':'+name);
        LogHelper.info("registering " + item.getUnlocalizedName());
        GameRegistry.registerItem(item, name);
    }

    public static void registerSeed(ItemModSeed seed, BlockModPlant plant) {
        String name = Names.Objects.seed + plant.getUnlocalizedName().substring(plant.getUnlocalizedName().indexOf(':')+5);
        registerItem(seed, name);
        OreDictionary.registerOre(name, seed);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seed);
    }

    public static void removeRecipe(ItemStack stack) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
        ItemStack result;
        for(int i=0;i<recipes.size();i++) {
            IRecipe recipe = (IRecipe) recipes.get(i);
            result = recipe.getRecipeOutput();
            if(result!=null && stack.getItem()==result.getItem() && stack.getItemDamage()==result.getItemDamage()) {
                recipes.remove(i);
            }
        }
    }
}
