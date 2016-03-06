package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.models.loaders.AgriCraftModelLoader;
import com.infinityraider.agricraft.models.loaders.StateUnmapper;
import com.infinityraider.agricraft.reference.Reference;
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
import net.minecraftforge.client.model.ModelLoader;
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
		if (ConfigurationHandler.registerCropProductsToOreDict) {
			for (ItemStack fruit : plant.products.getAllProducts()) {
				if (fruit != null && fruit.getItem() != null && !OreDictHelper.hasOreId(fruit, name)) {
					OreDictionary.registerOre(name, fruit);
				}
			}
		}
	}

	private static final StateUnmapper UNMAPPER = new StateUnmapper("agricraft:base");

	public static void hideModel(Block block, String name) {
		ModelLoader.setCustomStateMapper(block, UNMAPPER);
		AgriCraftModelLoader.INSTANCE.addDummyModel("agricraft:models/item/" + name);
	}

	@SideOnly(Side.CLIENT)
	public static ModelResourceLocation registerItemRendererTex(Item item, String... textures) {
		final StringBuilder sb = new StringBuilder("agricraftitem:");
		for (String e : textures) {
			sb.append(e.replace(":", "/"));
			sb.append(".");
		}
		final ModelResourceLocation model = new ModelResourceLocation(sb.toString(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, model);
		return model;
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

	// TODO: Investigate naming.
	public static void registerSeed(ItemModSeed seed, BlockModPlant plant) {
		String name = plant.getUnlocalizedName().substring(plant.getUnlocalizedName().indexOf(':') + 5);
		registerItem(seed, "seed" + name);
		OreDictionary.registerOre("seed" + name, seed);
		OreDictionary.registerOre("listAllseed", seed);
		AgriCraftModelLoader.INSTANCE.addDummyModel("agricraft:models/item/seed" + name);
		hideModel(plant, "crop" + name);
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
