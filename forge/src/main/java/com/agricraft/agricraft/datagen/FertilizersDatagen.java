package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizerParticle;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizerVariant;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FertilizersDatagen {

	private static void mc(BootstapContext<AgriFertilizer> context, String fertilizerId, AgriFertilizer fertilizer) {
		context.register(
				ResourceKey.create(AgriApi.AGRIFERTILIZERS, new ResourceLocation("minecraft", fertilizerId)),
				fertilizer
		);
	}

	public static void registerFertilizers(BootstapContext<AgriFertilizer> context) {
		mc(context, "bone_meal", new AgriFertilizer.Builder()
				.variants(new AgriFertilizerVariant.Builder().item("minecraft:bone_meal").build())
				.particles(new AgriFertilizerParticle("minecraft:happy_villager", 0.6, 0.4, 0.6, 2, List.of("positive")),
						new AgriFertilizerParticle("minecraft:smoke", 0.6, 0.4, 0.6, 2, List.of("negative")))
				.neutralOn("agricraft:amathyllis", "agricraft:aurigold", "agricraft:carbonation", "agricraft:cuprosia",
						"agricraft:diamahlia", "agricraft:emeryllis", "agricraft:ferranium", "agricraft:jaslumine",
						"agricraft:lapender", "agricraft:nethereed", "agricraft:niccissus", "agricraft:nitor_wart",
						"agricraft:osmonium", "agricraft:petinia", "agricraft:platiolus", "agricraft:plombean",
						"agricraft:quartzanthemum", "agricraft:redstodendron", "mysticalagriculture:air", "mysticalagriculture:earth",
						"mysticalagriculture:water", "mysticalagriculture:fire", "mysticalagriculture:inferium", "mysticalagriculture:stone",
						"mysticalagriculture:dirt", "mysticalagriculture:wood", "mysticalagriculture:ice", "mysticalagriculture:deepslate",
						"mysticalagriculture:nature", "mysticalagriculture:dye", "mysticalagriculture:nether", "mysticalagriculture:coal",
						"mysticalagriculture:coral", "mysticalagriculture:honey", "mysticalagriculture:amethyst", "mysticalagriculture:pig",
						"mysticalagriculture:chicken", "mysticalagriculture:cow", "mysticalagriculture:sheep", "mysticalagriculture:squid",
						"mysticalagriculture:fish", "mysticalagriculture:slime", "mysticalagriculture:turtle", "mysticalagriculture:rubber",
						"mysticalagriculture:silicon", "mysticalagriculture:sulfur", "mysticalagriculture:aluminum", "mysticalagriculture:saltpeter",
						"mysticalagriculture:apatite", "mysticalagriculture:grains_of_infinity", "mysticalagriculture:mystical_flower",
						"mysticalagriculture:marble", "mysticalagriculture:limestone", "mysticalagriculture:basalt", "mysticalagriculture:menril",
						"mysticalagriculture:iron", "mysticalagriculture:copper", "mysticalagriculture:nether_quartz", "mysticalagriculture:glowstone",
						"mysticalagriculture:redstone", "mysticalagriculture:obsidian", "mysticalagriculture:prismarine", "mysticalagriculture:zombie",
						"mysticalagriculture:skeleton", "mysticalagriculture:creeper", "mysticalagriculture:spider", "mysticalagriculture:rabbit",
						"mysticalagriculture:tin", "mysticalagriculture:bronze", "mysticalagriculture:zinc", "mysticalagriculture:brass",
						"mysticalagriculture:silver", "mysticalagriculture:lead", "mysticalagriculture:graphite", "mysticalagriculture:blizz",
						"mysticalagriculture:blitz", "mysticalagriculture:basalz", "mysticalagriculture:amethyst_bronze", "mysticalagriculture:slimesteel",
						"mysticalagriculture:pig_iron", "mysticalagriculture:copper_alloy", "mysticalagriculture:redstone_alloy",
						"mysticalagriculture:conductive_alloy", "mysticalagriculture:manasteel", "mysticalagriculture:steeleaf",
						"mysticalagriculture:ironwood", "mysticalagriculture:aquamarine", "mysticalagriculture:sky_stone",
						"mysticalagriculture:certus_quartz", "mysticalagriculture:quartz_enriched_iron", "mysticalagriculture:gold",
						"mysticalagriculture:lapis_lazuli", "mysticalagriculture:end", "mysticalagriculture:experience", "mysticalagriculture:blaze",
						"mysticalagriculture:ghast", "mysticalagriculture:enderman", "mysticalagriculture:steel", "mysticalagriculture:nickel",
						"mysticalagriculture:constantan", "mysticalagriculture:electrum", "mysticalagriculture:invar", "mysticalagriculture:mithril",
						"mysticalagriculture:tungsten", "mysticalagriculture:titanium", "mysticalagriculture:uranium", "mysticalagriculture:chrome",
						"mysticalagriculture:ruby", "mysticalagriculture:sapphire", "mysticalagriculture:peridot", "mysticalagriculture:soulium",
						"mysticalagriculture:signalum", "mysticalagriculture:lumium", "mysticalagriculture:flux_infused_ingot",
						"mysticalagriculture:hop_graphite", "mysticalagriculture:cobalt", "mysticalagriculture:rose_gold", "mysticalagriculture:soularium",
						"mysticalagriculture:dark_steel", "mysticalagriculture:pulsating_alloy", "mysticalagriculture:energetic_alloy",
						"mysticalagriculture:elementium", "mysticalagriculture:osmium", "mysticalagriculture:fluorite", "mysticalagriculture:refined_glowstone",
						"mysticalagriculture:refined_obsidian", "mysticalagriculture:knightmetal", "mysticalagriculture:fiery_ingot",
						"mysticalagriculture:starmetal", "mysticalagriculture:compressed_iron", "mysticalagriculture:fluix",
						"mysticalagriculture:energized_steel", "mysticalagriculture:blazing_crystal", "mysticalagriculture:diamond",
						"mysticalagriculture:emerald", "mysticalagriculture:netherite", "mysticalagriculture:wither_skeleton",
						"mysticalagriculture:platinum", "mysticalagriculture:iridium", "mysticalagriculture:enderium", "mysticalagriculture:flux_infused_gem",
						"mysticalagriculture:manyullyn", "mysticalagriculture:queens_slime", "mysticalagriculture:hepatizon",
						"mysticalagriculture:vibrant_alloy", "mysticalagriculture:end_steel", "mysticalagriculture:terrasteel",
						"mysticalagriculture:rock_crystal", "mysticalagriculture:draconium", "mysticalagriculture:yellorium",
						"mysticalagriculture:cyanite", "mysticalagriculture:niotic_crystal", "mysticalagriculture:spirited_crystal",
						"mysticalagriculture:uraninite")
				.build());
	}

	public static void registerMysticalAgriculture(BootstapContext<AgriFertilizer> context) {
		mc(context, "mystical_fertilizer", new AgriFertilizer.Builder()
				.potency(5)
				.triggerMutation(false)
				.triggerWeeds(false)
				.reduceGrowth(true)
				.killPlant(true)
				.variants(new AgriFertilizerVariant.Builder().item("mysticalagriculture:mystical_fertilizer").build())
				.particles(new AgriFertilizerParticle("minecraft:happy_villager", 0.6, 0.4, 0.6, 2, List.of("positive")),
						new AgriFertilizerParticle("minecraft:smoke", 0.6, 0.4, 0.6, 2, List.of("negative")))
				.build());
	}
}
