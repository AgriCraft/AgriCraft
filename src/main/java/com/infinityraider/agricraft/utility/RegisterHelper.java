package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// I don't know if final or abstract is better...
public abstract class RegisterHelper {

    public static void registerBlock(Block block, String name) {
        RegisterHelper.registerBlock(block, name, null);
    }

    public static void registerBlock(Block block, String name, Class<? extends ItemBlock> itemClass) {
        block.setUnlocalizedName(Reference.MOD_ID + ':' + name);
        if (itemClass != null) {
            GameRegistry.registerBlock(block, itemClass, name);
        } else {
            GameRegistry.registerBlock(block, name);
        }
    }

    public static void registerCrop(BlockModPlant plant, String name) {
        name = "crop" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        registerBlock(plant, name);
        if (AgriCraftConfig.registerCropProductsToOreDict) {
            for (ItemStack fruit : plant.products.getAllProducts()) {
                if (fruit != null && fruit.getItem() != null && !OreDictHelper.hasOreId(fruit, name)) {
                    OreDictionary.registerOre(name, fruit);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemRenderer(Item item, String... varients) {
        ModelResourceLocation[] locations = new ModelResourceLocation[varients.length];
        for (int i = 0; i < varients.length; i++) {
            locations[i] = new ModelResourceLocation(item.getRegistryName() + (varients[i].isEmpty() ? "" : ('_' + varients[i])), "inventory");
        }
        registerItemRender(item, locations);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemRender(Item item, ModelResourceLocation... varients) {
        ModelBakery.registerItemVariants(item, varients);
        for (int i = 0; i < varients.length; i++) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i, varients[i]);
        }
    }

    public static void registerItem(Item item, String name) {
        item.setUnlocalizedName(Reference.MOD_ID + ':' + name);
        GameRegistry.registerItem(item, name);
    }

    public static void registerSeed(ItemModSeed seed, BlockModPlant plant, String name) {
        name = name.startsWith("seed") ? (name) : ("seed" + name);
        registerItem(seed, name);
        OreDictionary.registerOre(name, seed);
        OreDictionary.registerOre("listAllseed", seed);
    }

    public static void removeRecipe(ItemStack stack) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
        ItemStack result;
        for (int i = 0; i < recipes.size(); i++) {
            IRecipe recipe = (IRecipe) recipes.get(i);
            result = recipe.getRecipeOutput();
            if (result != null && stack.getItem() == result.getItem() && stack.getItemDamage() == result.getItemDamage()) {
                recipes.remove(i);
            }
        }
    }
}
