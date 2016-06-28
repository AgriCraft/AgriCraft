package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.crafting.RecipeJournal;
import com.infinityraider.agricraft.items.crafting.RecipeShapelessCustomWood;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.reference.AgriNuggetType;
import com.infinityraider.agricraft.utility.OreDictHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import com.infinityraider.agricraft.reference.AgriNBT;

public class AgriRecipes {

	/**
	 * Will be replaced with all the custom woods in CustomWood recipes
	 */
	public static final ItemStack REFERENCE = new ItemStack(Blocks.PLANKS, 1);

	/**
	 * Holds all the custom woods for CustomWood items, will get filled on
	 * init()
	 */
	private static final List<ItemStack> woodList = new ArrayList<>();

	public static void init() {
		//crop item
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.crops, AgriCraftConfig.cropsPerCraft), "ss", "ss", 's', "stickWood"));
		if (AgriCraftConfig.cropsPerCraft == 3) {
			GameRegistry.addShapelessRecipe(new ItemStack(Items.STICK, 6 / AgriCraftConfig.cropsPerCraft), new ItemStack(AgriItems.crops), new ItemStack(AgriItems.crops));
		} else {
			GameRegistry.addShapelessRecipe(new ItemStack(Items.STICK, 4 / AgriCraftConfig.cropsPerCraft), new ItemStack(AgriItems.crops));
		}
		//seed analyzer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriBlocks.blockSeedAnalyzer, 1), "sgs", " bs", "pwp", 's', "stickWood", 'g', "paneGlass", 'b', Blocks.STONE_SLAB, 'p', "plankWood", 'w', "slabWood"));
		//journal
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.journal, 1), "csc", "sbs", "csc", 'c', AgriItems.crops, 's', "listAllseed", 'b', Items.WRITABLE_BOOK));
		GameRegistry.addRecipe(new RecipeJournal());
		//trowel
		if (AgriItems.enableTrowel && AgriItems.trowel != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.trowel, 1, 0), "  s", "ii ", 's', "stickWood", 'i', "ingotIron"));
		}
		//magnifying glass
		if (AgriItems.enableMagnifyingGlass && AgriItems.magnifyingGlass != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.magnifyingGlass, 1, 0), "sgs", " s ", " s ", 's', "stickWood", 'g', "paneGlass"));
		}
		//hand rakes
		if (AgriItems.enableHandRake && AgriItems.handRake != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.OAK_FENCE, 's', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.BIRCH_FENCE, 's', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.SPRUCE_FENCE, 's', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.ACACIA_FENCE, 's', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.JUNGLE_FENCE, 's', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', Blocks.DARK_OAK_FENCE, 's', "stickWood"));
			if (AgriBlocks.enableFences && AgriBlocks.blockFence != null) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 0), "fs", 'f', AgriBlocks.blockFence, 's', "stickWood"));
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.handRake, 1, 1), "fs", 'f', Blocks.IRON_BARS, 's', "stickWood"));
		}
		//clipper
		if (AgriItems.enableClipper && AgriItems.clipper != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriItems.clipper, 1, 0), " i ", "scn", " s ", 'i', "ingotIron", 's', "stickWood", 'c', new ItemStack(Items.SHEARS)));
		}
		//peripheral
		// To be done elsewhere.
		//CustomWood recipes
		registerCustomWoodRecipes();
		if (!AgriCraftConfig.disableIrrigation) {
			//change wooden bowl recipe
			RegisterHelper.removeRecipe(new ItemStack(Items.BOWL));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.BOWL, 4), "w w", " w ", 'w', "slabWood"));
			//sprinkler
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AgriBlocks.blockSprinkler, 1), " w ", " i ", "bcb", 'w', "plankWood", 'i', "ingotIron", 'b', Blocks.IRON_BARS, 'c', Items.BUCKET));
		}
		//fruits
		for (AgriNuggetType type : AgriNuggetType.values()) {
			ItemStack nugget = new ItemStack(AgriItems.nugget, 9, type.ordinal());
			ItemStack ingot = OreDictHelper.getIngot(type.ingot);
			AgriCore.getLogger("AgriCraft").debug("Registering Nugget: {0} For: {1}", type.nugget, type.ingot);
			if (ingot != null) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "nnn", "nnn", "nnn", 'n', type.nugget));
				GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, type.ingot));
			}
		}
		
		AgriCore.getLogger("AgriCraft").debug("Recipes Registered");
	}

	private static void registerCustomWoodRecipes() {
		initWoodList();
		if (!AgriCraftConfig.disableIrrigation) {
			ItemStack channel = new ItemStack(AgriBlocks.blockWaterChannel, 1);
			ItemStack channelFull = new ItemStack(AgriBlocks.blockWaterChannelFull, 1);

			registerCustomWoodRecipe(AgriBlocks.blockWaterTank, 1, true, "w w", "w w", "www", 'w', REFERENCE);
			registerCustomWoodRecipe(AgriBlocks.blockWaterChannel, 6, true, "w w", " w ", 'w', REFERENCE);
			registerCustomWoodRecipe(AgriBlocks.blockWaterChannelFull, 1, false, channel, channel, channel, channel);
			registerCustomWoodRecipe(AgriBlocks.blockWaterChannel, 4, false, channelFull);
			registerCustomWoodRecipe(AgriBlocks.blockChannelValve, 1, false, new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Blocks.LEVER, 1), channel);
		}
		if (!AgriCraftConfig.disableSeedStorage) {
			registerCustomWoodRecipe(AgriBlocks.blockSeedStorage, 1, true, "wiw", "wcw", "wcw", 'w', REFERENCE, 'i', Items.IRON_INGOT, 'c', Blocks.CHEST);
		}
		if (AgriBlocks.enableFences) {
			ItemStack fence = new ItemStack(AgriBlocks.blockFence, 1);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.OAK_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.BIRCH_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.JUNGLE_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.SPRUCE_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.ACACIA_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFence, 8, true, "fff", "fwf", "fff", 'w', REFERENCE, 'f', Blocks.DARK_OAK_FENCE);
			registerCustomWoodRecipe(AgriBlocks.blockFenceGate, 1, true, "fwf", 'w', REFERENCE, 'f', fence);
		}
		if (AgriBlocks.enableGrates) {
			registerCustomWoodRecipe(AgriBlocks.blockGrate, 8, true, "w w", " w ", "w w", 'w', REFERENCE);
		}
	}

	private static void initWoodList() {
		if (woodList.size() <= 0) {
			ReflectionHelper.forEachIn(AgriBlocks.class, BlockCustomWood.class, (b) -> {
				((ItemBlockCustomWood) Item.getItemFromBlock(b)).getSubItems(woodList);
			});
		}
	}

	/**
	 * Adds the given recipe for every available WOOD type.
	 *
	 * @param params Same as for GameRegistry. The only difference is that
	 * planks will get replaced with the different woods.
	 */
	public static void registerCustomWoodRecipe(Block block, int stackSize, boolean shaped, Object... params) {
		for (ItemStack stack : woodList) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(AgriNBT.MATERIAL) && stack.getTagCompound().hasKey(AgriNBT.MATERIAL_META)) {
				//get MATERIAL
				NBTTagCompound materialTag = stack.getTagCompound();
				ItemStack plank = new ItemStack(Block.getBlockFromName(materialTag.getString(AgriNBT.MATERIAL)), 1, materialTag.getInteger(AgriNBT.MATERIAL_META));
				Object[] ingredients = Arrays.copyOf(params, params.length);
				for (int i = 0; i < ingredients.length; i++) {
					// replace all planks with the custom ones
					if (ingredients[i] instanceof ItemStack && ((ItemStack) ingredients[i]).isItemEqual(REFERENCE)) {
						ingredients[i] = plank;
					}
					// Also replace ItemBlockCustomWood with the correct version
					if (ingredients[i] instanceof ItemStack && ingredients[i] != null && ((ItemStack) ingredients[i]).getItem() instanceof ItemBlockCustomWood) {
						((ItemStack) ingredients[i]).setTagCompound(materialTag);
					}
				}
				ItemStack result = new ItemStack(block, stackSize);
				result.setTagCompound(materialTag);
				//register recipes
				if (shaped) {
					GameRegistry.addShapedRecipe(result, ingredients);
				} else {
					addShapelessCustomWoodRecipe(result, ingredients);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void registerCustomWoodRecipe(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
			registerCustomWoodRecipe(((ItemBlock) shapedRecipe.getRecipeOutput().getItem()).block, shapedRecipe.getRecipeOutput().stackSize, true, (Object[]) shapedRecipe.recipeItems);
		} else if (recipe instanceof ShapelessRecipes) {
			ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
			registerCustomWoodRecipe(((ItemBlock) shapelessRecipe.getRecipeOutput().getItem()).block, shapelessRecipe.getRecipeOutput().stackSize, false, (Object[]) shapelessRecipe.recipeItems.toArray(new ItemStack[shapelessRecipe.recipeItems.size()]));
		}
	}

	@SuppressWarnings("unchecked")
	private static void addShapelessCustomWoodRecipe(ItemStack output, Object... params) {
		List recipeItemsCopy = new ArrayList();
		for (Object recipeItem : params) {
			if (recipeItem instanceof ItemStack) {
				recipeItemsCopy.add(((ItemStack) recipeItem).copy());
			} else if (recipeItem instanceof Item) {
				recipeItemsCopy.add(new ItemStack((Item) recipeItem));
			} else {
				if (!(recipeItem instanceof Block)) {
					throw new RuntimeException("Invalid shapeless recipe!");
				}
				recipeItemsCopy.add(new ItemStack((Block) recipeItem));
			}
		}
		RecipeShapelessCustomWood recipe = new RecipeShapelessCustomWood(output, recipeItemsCopy);
		GameRegistry.addRecipe(recipe);
	}
}
