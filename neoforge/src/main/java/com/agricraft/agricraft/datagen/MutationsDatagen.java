package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MutationsDatagen {

	public static void registerMutations(BootstapContext<AgriMutation> context) {
		mc(context, "allium", AgriMutation.builder().child("minecraft:allium").parents("minecraft:poppy", "minecraft:blue_orchid").chance(0.75).build());
		mc(context, "azure_bluet", AgriMutation.builder().child("minecraft:azure_bluet").parents("minecraft:dandelion", "minecraft:lily_of_the_valley").chance(0.75).build());
		mc(context, "bamboo", AgriMutation.builder().child("minecraft:bamboo").parents("minecraft:sugar_cane", "minecraft:cactus").chance(0.5).build());
		mc(context, "blue_orchid", AgriMutation.builder().child("minecraft:blue_orchid").parents("minecraft:poppy", "minecraft:dandelion").chance(0.75).build());
		mc(context, "brown_mushroom", AgriMutation.builder().child("minecraft:brown_mushroom").parents("minecraft:potato", "minecraft:carrot").chance(0.5).build());
		mc(context, "cactus", AgriMutation.builder().child("minecraft:cactus").parents("minecraft:sugar_cane", "minecraft:potato").chance(0.5).build());
		mc(context, "carrot", AgriMutation.builder().child("minecraft:carrot").parents("minecraft:wheat", "minecraft:potato").chance(0.75).build());
		mc(context, "cornflower", AgriMutation.builder().child("minecraft:cornflower").parents("minecraft:blue_orchid", "minecraft:lily_of_the_valley").chance(0.75).build());
		mc(context, "crimson_fungus", AgriMutation.builder().child("minecraft:crimson_fungus").parents("minecraft:nether_wart", "minecraft:poppy").chance(0.75).build());
		mc(context, "dandelion", AgriMutation.builder().child("minecraft:dandelion").parents("minecraft:pumpkin", "minecraft:wheat").chance(0.75).build());
		mc(context, "kelp", AgriMutation.builder().child("minecraft:kelp").parents("minecraft:seagrass", "minecraft:bamboo").chance(0.5).build());
		mc(context, "lily_of_the_valley", AgriMutation.builder().child("minecraft:lily_of_the_valley").parents("minecraft:white_tulip", "minecraft:oxeye_daisy").chance(0.75).build());
		mc(context, "melon", AgriMutation.builder().child("minecraft:melon").parents("minecraft:sugar_cane", "minecraft:pumpkin").chance(0.25).build());
		mc(context, "orange_tulip", AgriMutation.builder().child("minecraft:orange_tulip").parents("minecraft:oxeye_daisy", "minecraft:blue_orchid").chance(0.75).build());
		mc(context, "oxeye_daisy", AgriMutation.builder().child("minecraft:oxeye_daisy").parents("minecraft:dandelion", "minecraft:blue_orchid").chance(0.75).build());
		mc(context, "pink_tulip", AgriMutation.builder().child("minecraft:pink_tulip").parents("minecraft:allium", "minecraft:dandelion").chance(0.75).build());
		mc(context, "poppy", AgriMutation.builder().child("minecraft:poppy").parents("minecraft:sugar_cane", "minecraft:melon").chance(0.75).build());
		mc(context, "pumpkin", AgriMutation.builder().child("minecraft:pumpkin").parents("minecraft:carrot", "minecraft:potato").chance(0.25).build());
		mc(context, "red_mushroom", AgriMutation.builder().child("minecraft:red_mushroom").parents("minecraft:potato", "minecraft:carrot").chance(0.5).build());
		mc(context, "red_tulip", AgriMutation.builder().child("minecraft:red_tulip").parents("minecraft:poppy", "minecraft:allium").chance(0.75).build());
		mc(context, "seagrass", AgriMutation.builder().child("minecraft:seagrass").parents("minecraft:wheat", "minecraft:bamboo").chance(0.5).build());
		mc(context, "sea_pickle", AgriMutation.builder().child("minecraft:sea_pickle").parents("minecraft:seagrass", "minecraft:carrot").chance(0.5).build());
		mc(context, "sugar_cane", AgriMutation.builder().child("minecraft:sugar_cane").parents("minecraft:wheat", "minecraft:beetroot").chance(0.5).build());
		mc(context, "sweet_berries", AgriMutation.builder().child("minecraft:sweet_berries").parents("minecraft:sugar_cane", "minecraft:beetroot").chance(0.75).build());
		mc(context, "warped_fungus", AgriMutation.builder().child("minecraft:warped_fungus").parents("minecraft:nether_wart", "minecraft:blue_orchid").chance(0.75).build());
		mc(context, "white_tulip", AgriMutation.builder().child("minecraft:white_tulip").parents("minecraft:oxeye_daisy", "minecraft:dandelion").chance(0.75).build());
		mc(context, "wither_rose", AgriMutation.builder().child("minecraft:wither_rose").parents("minecraft:cornflower", "minecraft:poppy").chance(0.75).build());
	}

	private static void mc(BootstapContext<AgriMutation> context, String mutationId, AgriMutation mutation) {
		context.register(
				ResourceKey.create(AgriApi.AGRIMUTATIONS, new ResourceLocation("minecraft", mutationId)),
				mutation
		);
	}

}
