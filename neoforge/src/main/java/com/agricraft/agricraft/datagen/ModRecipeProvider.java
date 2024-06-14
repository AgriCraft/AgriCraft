package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class ModRecipeProvider extends RecipeProvider {

	public ModRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		TagKey<Item> stick = TagKey.create(Registries.ITEM, new ResourceLocation("forge:rods/wooden"));
		TagKey<Item> ironNugget = TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/iron"));
		TagKey<Item> obsidian = TagKey.create(Registries.ITEM, new ResourceLocation("forge:obsidian"));
		TagKey<Item> seed = TagKey.create(Registries.ITEM, new ResourceLocation("forge:seeds"));
		TagKey<Item> ironIngot = TagKey.create(Registries.ITEM, new ResourceLocation("forge:ingots/iron"));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.WOODEN_CROP_STICKS.get(), 8))
				.pattern("##")
				.pattern("##")
				.define('#', stick)
				.unlockedBy("has_stick", has(stick))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.IRON_CROP_STICKS.get(), 8))
				.pattern("#")
				.pattern("#")
				.define('#', ironNugget)
				.unlockedBy("has_nugget", has(ironNugget))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.OBSIDIAN_CROP_STICKS.get(), 8))
				.pattern("#")
				.pattern("#")
				.define('#', obsidian)
				.unlockedBy("has_obsidian", has(obsidian))
				.save(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, new ItemStack(ModItems.JOURNAL.get()))
				.requires(Items.WRITABLE_BOOK)
				.requires(seed)
				.unlockedBy("has_seed", has(seed))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.SEED_ANALYZER.get()))
				.pattern("sgs")
				.pattern(" bs")
				.pattern("pwp")
				.define('s', stick)
				.define('g', TagKey.create(Registries.ITEM, new ResourceLocation("forge:glass_panes/colorless")))
				.define('b', Items.STONE_SLAB)
				.define('p', TagKey.create(Registries.ITEM, new ResourceLocation("minecraft:planks")))
				.define('w', TagKey.create(Registries.ITEM, new ResourceLocation("minecraft:wooden_slabs")))
				.unlockedBy("has_seed", has(seed))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.CLIPPER.get()))
				.pattern(" i ")
				.pattern("sr ")
				.pattern(" s ")
				.define('i', ironIngot)
				.define('r', Items.SHEARS)
				.define('s', stick)
				.unlockedBy("has_iron", has(ironIngot))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.MAGNIFYING_GLASS.get()))
				.pattern("sgs")
				.pattern(" s ")
				.pattern(" s ")
				.define('g', TagKey.create(Registries.ITEM, new ResourceLocation("forge:glass_panes/colorless")))
				.define('s', stick)
				.unlockedBy("has_stick", has(stick))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.WOODEN_RAKE.get()))
				.pattern("f")
				.pattern("s")
				.define('f', TagKey.create(Registries.ITEM, new ResourceLocation("forge:fences/wooden")))
				.define('s', stick)
				.unlockedBy("has_stick", has(stick))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.IRON_RAKE.get()))
				.pattern("b")
				.pattern("s")
				.define('b', Items.IRON_BARS)
				.define('s', stick)
				.unlockedBy("has_stick", has(stick))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.TROWEL.get()))
				.pattern("  s")
				.pattern("ii ")
				.define('s', stick)
				.define('i', ironIngot)
				.unlockedBy("has_iron", has(ironIngot))
				.save(output);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, new ItemStack(ModItems.SEED_BAG.get()))
				.pattern(" s ")
				.pattern("l l")
				.pattern(" l ")
				.define('s', TagKey.create(Registries.ITEM, new ResourceLocation("forge:string")))
				.define('l', TagKey.create(Registries.ITEM, new ResourceLocation("forge:leather")))
				.unlockedBy("has_seed", has(seed))
				.save(output);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.AMETHYST_SHARD))
				.pattern("ppp")
				.pattern("ppp")
				.pattern("ppp")
				.define('p', ModItems.AMATHYLLIS_PETAL.get())
				.unlockedBy("has_petal", has(ModItems.AMATHYLLIS_PETAL.get()))
				.save(output, new ResourceLocation("agricraft:amethyst_shard"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.COAL))
				.pattern("ppp")
				.pattern("ppp")
				.pattern("ppp")
				.define('p', ModItems.COAL_PEBBLE.get())
				.unlockedBy("has_pebble", has(ModItems.COAL_PEBBLE.get()))
				.save(output, new ResourceLocation("agricraft:coal"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.COPPER_INGOT))
				.pattern("nnn")
				.pattern("nnn")
				.pattern("nnn")
				.define('n', ModItems.COPPER_NUGGET.get())
				.unlockedBy("has_nugget", has(ModItems.COPPER_NUGGET.get()))
				.save(output, new ResourceLocation("agricraft:copper_ingot"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.DIAMOND))
				.pattern("sss")
				.pattern("sss")
				.pattern("sss")
				.unlockedBy("has_shard", has(ModItems.DIAMOND_SHARD.get()))
				.define('s', ModItems.DIAMOND_SHARD.get())
				.save(output, new ResourceLocation("agricraft:diamond"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.EMERALD))
				.pattern("sss")
				.pattern("sss")
				.pattern("sss")
				.unlockedBy("has_shard", has(ModItems.EMERALD_SHARD.get()))
				.define('s', ModItems.EMERALD_SHARD.get())
				.save(output, new ResourceLocation("agricraft:emerald"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.NETHERITE_SCRAP))
				.pattern("sss")
				.pattern("sss")
				.pattern("sss")
				.unlockedBy("has_sliver", has(ModItems.NETHERITE_SLIVER.get()))
				.define('s', ModItems.NETHERITE_SLIVER.get())
				.save(output, new ResourceLocation("agricraft:netherite_scrap"));
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, new ItemStack(Items.QUARTZ))
				.pattern("sss")
				.pattern("sss")
				.pattern("sss")
				.unlockedBy("has_shard", has(ModItems.QUARTZ_SHARD.get()))
				.define('s', ModItems.QUARTZ_SHARD.get())
				.save(output, new ResourceLocation("agricraft:quartz"));
	}

}
