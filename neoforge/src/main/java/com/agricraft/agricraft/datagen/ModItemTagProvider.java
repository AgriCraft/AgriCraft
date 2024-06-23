package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

	public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// Fabric
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:seeds")))
				.add(ModItems.SEED.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:coal_nuggets")))
				.add(ModItems.COAL_PEBBLE.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:copper_nuggets")))
				.add(ModItems.COPPER_NUGGET.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:diamond_nuggets")))
				.add(ModItems.DIAMOND_SHARD.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:emerald_nuggets")))
				.add(ModItems.EMERALD_SHARD.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("c:quartz_nuggets")))
				.add(ModItems.QUARTZ_SHARD.get());

		// Forge
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:seeds")))
				.add(ModItems.SEED.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/coal")))
				.add(ModItems.COAL_PEBBLE.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/copper")))
				.add(ModItems.COPPER_NUGGET.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/diamond")))
				.add(ModItems.DIAMOND_SHARD.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/emerald")))
				.add(ModItems.EMERALD_SHARD.get());
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("forge:nuggets/quartz")))
				.add(ModItems.QUARTZ_SHARD.get());

		// Cross-Compatibility
		//     Recipes
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:rods/wooden")))
				.add(Items.STICK)
				.addOptionalTag(new ResourceLocation("c:wooden_rods"))
				.addOptionalTag(new ResourceLocation("forge:rods/wooden"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/iron")))
				.add(Items.IRON_NUGGET)
				.addOptionalTag(new ResourceLocation("c:iron_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/iron"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:obsidian")))
				.add(Items.OBSIDIAN)
				.addOptionalTag(new ResourceLocation("c:obsidian"))
				.addOptionalTag(new ResourceLocation("forge:obsidian"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:seeds")))
				.add(Items.BEETROOT_SEEDS)
				.add(Items.MELON_SEEDS)
				.add(Items.PUMPKIN_SEEDS)
				.add(Items.WHEAT_SEEDS)
				.addOptionalTag(new ResourceLocation("c:seeds"))
				.addOptionalTag(new ResourceLocation("forge:seeds"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:ingots/iron")))
				.add(Items.IRON_INGOT)
				.addOptionalTag(new ResourceLocation("c:iron_ingots"))
				.addOptionalTag(new ResourceLocation("forge:ingots/iron"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:glass_panes/colorless")))
				.add(Items.GLASS_PANE)
				.addOptionalTag(new ResourceLocation("c:clear_glass_panes"))
				.addOptionalTag(new ResourceLocation("forge:glass_panes/colorless"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:fences/wooden")))
				.addOptionalTag(new ResourceLocation("minecraft:wooden_fences")) // Not sure why this tag isn't available during datagen?
				.addOptionalTag(new ResourceLocation("c:wooden_fences"))
				.addOptionalTag(new ResourceLocation("forge:fences/wooden"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:string")))
				.add(Items.STRING)
				.addOptionalTag(new ResourceLocation("c:string"))
				.addOptionalTag(new ResourceLocation("c:strings"))
				.addOptionalTag(new ResourceLocation("forge:string"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:leather")))
				.add(Items.LEATHER)
				.addOptionalTag(new ResourceLocation("c:leather"))
				.addOptionalTag(new ResourceLocation("c:leathers"))
				.addOptionalTag(new ResourceLocation("forge:leather"));
		//     Plants (produce)
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:dusts/glowstone")))
				.add(Items.GLOWSTONE_DUST)
				.addOptionalTag(new ResourceLocation("c:glowstone_dusts"))
				.addOptionalTag(new ResourceLocation("forge:dusts/glowstone"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:dusts/redstone")))
				.add(Items.REDSTONE)
				.addOptionalTag(new ResourceLocation("c:redstone_dusts"))
				.addOptionalTag(new ResourceLocation("forge:dusts/redstone"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:gems/lapis")))
				.add(Items.LAPIS_LAZULI)
				.addOptionalTag(new ResourceLocation("c:lapis"))
				.addOptionalTag(new ResourceLocation("c:lapis_gems"))
				.addOptionalTag(new ResourceLocation("forge:gems/lapis"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/aluminium")))
				.addOptionalTag(new ResourceLocation("c:aluminum_nuggets"))
				.addOptionalTag(new ResourceLocation("c:aluminium_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/aluminum"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/aluminium"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/coal")))
				.add(ModItems.COAL_PEBBLE.get())
				.addOptionalTag(new ResourceLocation("c:coal_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/coal"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/copper")))
				.add(ModItems.COPPER_NUGGET.get())
				.addOptionalTag(new ResourceLocation("c:copper_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/copper"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/diamond")))
				.add(ModItems.DIAMOND_SHARD.get())
				.addOptionalTag(new ResourceLocation("c:diamond_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/diamond"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/emerald")))
				.add(ModItems.EMERALD_SHARD.get())
				.addOptionalTag(new ResourceLocation("c:emerald_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/emerald"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/gold")))
				.add(Items.GOLD_NUGGET)
				.addOptionalTag(new ResourceLocation("c:gold_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/gold"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/iron")))
				.add(Items.IRON_NUGGET)
				.addOptionalTag(new ResourceLocation("c:iron_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/iron"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/lead")))
				.addOptionalTag(new ResourceLocation("c:lead_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/lead"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/nickel")))
				.addOptionalTag(new ResourceLocation("c:nickel_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/nickel"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/osmium")))
				.addOptionalTag(new ResourceLocation("c:osmium_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/osmium"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/platinum")))
				.addOptionalTag(new ResourceLocation("c:platinum_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/platinum"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/quartz")))
				.add(ModItems.QUARTZ_SHARD.get())
				.addOptionalTag(new ResourceLocation("c:quartz_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/quartz"));
		this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("agricraft:nuggets/tin")))
				.addOptionalTag(new ResourceLocation("c:tin_nuggets"))
				.addOptionalTag(new ResourceLocation("forge:nuggets/tin"));
	}

}
