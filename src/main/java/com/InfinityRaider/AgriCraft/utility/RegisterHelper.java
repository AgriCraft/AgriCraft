package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.config.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// I don't know if final or abstract is better...
public abstract class RegisterHelper {
	
    public static void registerBlock(Block block,String name) {
        RegisterHelper.registerBlock(block, name, null);
    }

    public static void registerBlock(Block block, String name, Class<? extends ItemBlock> itemClass) {
        block.setUnlocalizedName(Reference.MOD_ID.toLowerCase()+':'+name);
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
        registerBlock(plant, "crop" + name);
        if(ConfigurationHandler.registerCropProductsToOreDict) {
            for (ItemStack fruit : plant.products.getAllProducts()) {
                if (fruit != null && fruit.getItem() != null && !OreDictHelper.hasOreId(fruit, "crop" + name)) {
                    OreDictionary.registerOre("crop" + name, fruit);
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
			locations[i] = new ModelResourceLocation(item.getRegistryName() + '_' + varients[i], "inventory");
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
        item.setUnlocalizedName(Reference.MOD_ID.toLowerCase()+':'+name);
        LogHelper.info("registering " + item.getUnlocalizedName());
        GameRegistry.registerItem(item, name);
    }

	// TODO: Investigate naming.
    public static void registerSeed(ItemModSeed seed, BlockModPlant plant) {
        String name = "seed" + plant.getUnlocalizedName().substring(plant.getUnlocalizedName().indexOf(':')+5);
        registerItem(seed, name);
        OreDictionary.registerOre(name, seed);
        OreDictionary.registerOre("listAllseed", seed);
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
