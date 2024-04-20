package com.agricraft.agricraft.datagen;

import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;

import java.util.List;

public class ModelsDatagen {

	public static void registerMinecraftPlant(ModelProvider<BlockModelBuilder> m) {
		with4TexturesIn8stage(m, "allium", "agricraft:crop/crop_hash", "agricraft:plant/allium");
		m.withExistingParent("azure_bluet_stage0", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
		m.withExistingParent("azure_bluet_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
		m.withExistingParent("azure_bluet_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage0");
		m.withExistingParent("azure_bluet_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage1");
		m.withExistingParent("azure_bluet_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage1");
		m.withExistingParent("azure_bluet_stage5", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage2");
		m.withExistingParent("azure_bluet_stage6", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/azure_bluet_stage2");
		m.withExistingParent("azure_bluet_stage7", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/azure_bluet");
		m.withExistingParent("bamboo_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/bamboo_stage0");
		m.withExistingParent("bamboo_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage1");
		m.withExistingParent("bamboo_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage2");
		m.withExistingParent("bamboo_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/bamboo_stage3");
		m.withExistingParent("bamboo_stage4", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage4");
		m.withExistingParent("bamboo_stage5", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage5");
		m.withExistingParent("bamboo_stage6", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage6");
		m.withExistingParent("bamboo_stage7", "agricraft:crop/tall_crop_plus").texture("crop", "agricraft:plant/bamboo_base").texture("crop_top", "agricraft:plant/bamboo_stage7");
		with4TexturesIn8stage(m, "beetroot", "agricraft:crop/crop_hash", "minecraft:block/beetroots");
		m.withExistingParent("blue_orchid_stage0", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
		m.withExistingParent("blue_orchid_stage1", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
		m.withExistingParent("blue_orchid_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage0");
		m.withExistingParent("blue_orchid_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage1");
		m.withExistingParent("blue_orchid_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage1");
		m.withExistingParent("blue_orchid_stage5", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage2");
		m.withExistingParent("blue_orchid_stage6", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/blue_orchid_stage2");
		m.withExistingParent("blue_orchid_stage7", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/blue_orchid");
		with4TexturesIn8stage(m, "brown_mushroom", "agricraft:crop/crop_hash", "agricraft:plant/brown_mushroom");
		with4TexturesIn8stage(m, "cactus", "agricraft:crop/crop_hash", "agricraft:plant/cactus");
		with4TexturesIn8stage(m, "carrot", "agricraft:crop/crop_hash", "minecraft:block/carrots");
		with4TexturesIn8stage(m, "cornflower", "agricraft:crop/crop_plus", "agricraft:plant/cornflower");
		with4TexturesIn8stage(m, "crimson_fungus", "agricraft:crop/crop_plus", "agricraft:plant/crimson_fungus");
		with4TexturesIn8stage(m, "dandelion", "agricraft:crop/crop_hash", "agricraft:plant/dandelion");
		m.withExistingParent("kelp_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
		m.withExistingParent("kelp_stage1", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
		m.withExistingParent("kelp_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
		m.withExistingParent("kelp_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
		m.withExistingParent("kelp_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:plant/kelp");
		m.withExistingParent("kelp_stage5", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
		m.withExistingParent("kelp_stage6", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
		m.withExistingParent("kelp_stage7", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "agricraft:plant/kelp");
		with4TexturesIn8stage(m, "lily_of_the_valley", "agricraft:crop/crop_plus", "agricraft:plant/lily_of_the_valley");
		m.withExistingParent("melon_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
		m.withExistingParent("melon_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
		m.withExistingParent("melon_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage0");
		m.withExistingParent("melon_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage1");
		m.withExistingParent("melon_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage1");
		m.withExistingParent("melon_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage2");
		m.withExistingParent("melon_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/melon_stage2");
		m.withExistingParent("melon_stage7", "agricraft:crop/crop_gourd").texture("crop", "agricraft:plant/melon_stage3").texture("gourd", "minecraft:block/melon_side");
		m.withExistingParent("nether_wart_stage0", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
		m.withExistingParent("nether_wart_stage1", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
		m.withExistingParent("nether_wart_stage2", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage0");
		m.withExistingParent("nether_wart_stage3", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
		m.withExistingParent("nether_wart_stage4", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
		m.withExistingParent("nether_wart_stage5", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
		m.withExistingParent("nether_wart_stage6", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage1");
		m.withExistingParent("nether_wart_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/nether_wart_stage2");
		with4TexturesIn8stage(m, "orange_tulip", "agricraft:crop/crop_hash", "agricraft:plant/orange_tulip");
		with4TexturesIn8stage(m, "oxeye_daisy", "agricraft:crop/crop_hash", "agricraft:plant/oxeye_daisy");
		with4TexturesIn8stage(m, "pink_tulip", "agricraft:crop/crop_hash", "agricraft:plant/pink_tulip");
		with4TexturesIn8stage(m, "poppy", "agricraft:crop/crop_hash", "agricraft:plant/poppy");
		with4TexturesIn8stage(m, "potato", "agricraft:crop/crop_hash", "minecraft:block/potatoes");
		m.withExistingParent("pumpkin_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
		m.withExistingParent("pumpkin_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
		m.withExistingParent("pumpkin_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage0");
		m.withExistingParent("pumpkin_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage1");
		m.withExistingParent("pumpkin_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage1");
		m.withExistingParent("pumpkin_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage2");
		m.withExistingParent("pumpkin_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/pumpkin_stage2");
		m.withExistingParent("pumpkin_stage7", "agricraft:crop/crop_gourd").texture("crop", "agricraft:plant/pumpkin_stage3").texture("gourd", "minecraft:block/pumpkin_side");
		with4TexturesIn8stage(m, "red_mushroom", "agricraft:crop/crop_hash", "agricraft:plant/red_mushroom");
		with4TexturesIn8stage(m, "red_tulip", "agricraft:crop/crop_hash", "agricraft:plant/red_tulip");
		m.withExistingParent("seagrass_stage0", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
		m.withExistingParent("seagrass_stage1", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
		m.withExistingParent("seagrass_stage2", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage0");
		m.withExistingParent("seagrass_stage3", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage1");
		m.withExistingParent("seagrass_stage4", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage1");
		m.withExistingParent("seagrass_stage5", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage2");
		m.withExistingParent("seagrass_stage6", "agricraft:crop/crop_hash").texture("crop", "agricraft:plant/seagrass_stage2");
		m.withExistingParent("seagrass_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/seagrass");
		with4TexturesIn8stage(m, "sea_pickle", "agricraft:crop/crop_hash", "agricraft:plant/sea_pickle");
		with4TexturesIn8stage(m, "sugar_cane", "agricraft:crop/crop_hash", "agricraft:plant/sugar_cane");
		with4TexturesIn8stage(m, "sweet_berries", "agricraft:crop/crop_hash", "agricraft:plant/sweet_berries");
		with4TexturesIn8stage(m, "warped_fungus", "agricraft:crop/crop_plus", "agricraft:plant/warped_fungus");
		m.withExistingParent("wheat_stage0", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage0");
		m.withExistingParent("wheat_stage1", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage1");
		m.withExistingParent("wheat_stage2", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage2");
		m.withExistingParent("wheat_stage3", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage3");
		m.withExistingParent("wheat_stage4", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage4");
		m.withExistingParent("wheat_stage5", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage5");
		m.withExistingParent("wheat_stage6", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage6");
		m.withExistingParent("wheat_stage7", "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage7");
		with4TexturesIn8stage(m, "white_tulip", "agricraft:crop/crop_hash", "agricraft:plant/white_tulip");
		with4TexturesIn8stage(m, "wither_rose", "agricraft:crop/crop_hash", "agricraft:plant/wither_rose");
	}

	public static void registerMinecraftSeed(ModelProvider<ItemModelBuilder> m) {
		List.of("allium", "azure_bluet", "bamboo", "blue_orchid", "brown_mushroom", "cactus", "carrot",
						"cornflower", "crimson_fungus", "dandelion", "kelp", "lily_of_the_valley", "nether_wart",
						"orange_tulip", "oxeye_daisy", "pink_tulip", "poppy", "potato", "red_mushroom",
						"red_tulip", "seagrass", "sea_pickle", "sugar_cane", "sweet_berries", "warped_fungus",
						"white_tulip", "wither_rose", "unknown")
				.forEach(name -> m.withExistingParent(name, "minecraft:item/generated").texture("layer0", "agricraft:seed/" + name));
		m.withExistingParent("beetroot", "minecraft:item/beetroot_seeds");
		m.withExistingParent("melon", "minecraft:item/melon_seeds");
		m.withExistingParent("pumpkin", "minecraft:item/pumpkin_seeds");
		m.withExistingParent("wheat", "minecraft:item/wheat_seeds");
	}

	public static void registerAgricraftWeed(ModelProvider<BlockModelBuilder> m) {
		with4TexturesIn8stage(m, "weed", "agricraft:crop/crop_rhombus", "agricraft:weed/weed");
	}

	private static void with4TexturesIn8stage(ModelProvider<BlockModelBuilder> m, String name, String parent, String baseTexture) {
		m.withExistingParent(name + "_stage0", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage1", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage2", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage3", parent).texture("crop", baseTexture + "_stage1");
		m.withExistingParent(name + "_stage4", parent).texture("crop", baseTexture + "_stage1");
		m.withExistingParent(name + "_stage5", parent).texture("crop", baseTexture + "_stage2");
		m.withExistingParent(name + "_stage6", parent).texture("crop", baseTexture + "_stage2");
		m.withExistingParent(name + "_stage7", parent).texture("crop", baseTexture + "_stage3");
	}

}
