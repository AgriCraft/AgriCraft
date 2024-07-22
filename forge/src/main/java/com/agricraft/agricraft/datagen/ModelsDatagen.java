package com.agricraft.agricraft.datagen;

import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;

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
		m.withExistingParent("torchflower_stage0", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage0");
		m.withExistingParent("torchflower_stage1", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage0");
		m.withExistingParent("torchflower_stage2", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage0");
		m.withExistingParent("torchflower_stage3", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage0");
		m.withExistingParent("torchflower_stage4", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage1");
		m.withExistingParent("torchflower_stage5", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage1");
		m.withExistingParent("torchflower_stage6", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower_crop_stage1");
		m.withExistingParent("torchflower_stage7", "agricraft:crop/crop_cross").texture("crop", "minecraft:block/torchflower");
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
		m.withExistingParent("torchflower", "minecraft:item/torchflower_seeds");
		m.withExistingParent("wheat", "minecraft:item/wheat_seeds");
	}

	public static void registerAgricraftPlant(ModelProvider<BlockModelBuilder> m) {
		with4TexturesIn8stage(m, "amathyllis", "agricraft:crop/crop_plus", "agricraft:plant/amathyllis");
		with4TexturesIn8stage(m, "aurigold", "agricraft:crop/crop_hash", "agricraft:plant/aurigold");
		with4TexturesIn8stage(m, "carbonation", "agricraft:crop/crop_hash", "agricraft:plant/carbonation");
		with4TexturesIn8stage(m, "cuprosia", "agricraft:crop/crop_hash", "agricraft:plant/cuprosia");
		with4TexturesIn8stage(m, "diamahlia", "agricraft:crop/crop_hash", "agricraft:plant/diamahlia");
		with4TexturesIn8stage(m, "emeryllis", "agricraft:crop/crop_hash", "agricraft:plant/emeryllis");
		with4TexturesIn8stage(m, "ferranium", "agricraft:crop/crop_hash", "agricraft:plant/ferranium");
		with4TexturesIn8stage(m, "jaslumine", "agricraft:crop/crop_hash", "agricraft:plant/jaslumine");
		with4TexturesIn8stage(m, "lapender", "agricraft:crop/crop_hash", "agricraft:plant/lapender");
		m.withExistingParent("nethereed_stage0", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage0").texture("crop_top", "agricraft:plant/nethereed_stage0");
		m.withExistingParent("nethereed_stage1", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage1").texture("crop_top", "agricraft:plant/nethereed_stage1");
		m.withExistingParent("nethereed_stage2", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage1").texture("crop_top", "agricraft:plant/nethereed_stage1");
		m.withExistingParent("nethereed_stage3", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage2").texture("crop_top", "agricraft:plant/nethereed_stage2");
		m.withExistingParent("nethereed_stage4", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage2").texture("crop_top", "agricraft:plant/nethereed_stage2");
		m.withExistingParent("nethereed_stage5", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage3").texture("crop_top", "agricraft:plant/nethereed_stage3");
		m.withExistingParent("nethereed_stage6", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage3").texture("crop_top", "agricraft:plant/nethereed_stage3");
		m.withExistingParent("nethereed_stage7", "agricraft:crop/tall_crop_hash").texture("crop", "agricraft:plant/nethereed_base_stage4").texture("crop_top", "agricraft:plant/nethereed_stage4");
		with4TexturesIn8stage(m, "niccissus", "agricraft:crop/crop_hash", "agricraft:plant/niccissus");
		with4TexturesIn8stage(m, "nitor_wart", "agricraft:crop/crop_hash", "agricraft:plant/nitor_wart");
		with4TexturesIn8stage(m, "osmonium", "agricraft:crop/crop_hash", "agricraft:plant/osmonium");
		with4TexturesIn8stage(m, "petinia", "agricraft:crop/crop_hash", "agricraft:plant/petinia");
		with4TexturesIn8stage(m, "platiolus", "agricraft:crop/crop_hash", "agricraft:plant/platiolus");
		with4TexturesIn8stage(m, "plombean", "agricraft:crop/crop_hash", "agricraft:plant/plombean");
		with4TexturesIn8stage(m, "quartzanthemum", "agricraft:crop/crop_hash", "agricraft:plant/quartzanthemum");
		with4TexturesIn8stage(m, "redstodendron", "agricraft:crop/crop_hash", "agricraft:plant/redstodendron");
	}

	public static void registerAgricraftSeed(ModelProvider<ItemModelBuilder> m) {
		List.of("amathyllis", "aurigold", "carbonation", "cuprosia", "diamahlia", "emeryllis", "ferranium",
						"jaslumine", "lapender", "nethereed", "niccissus", "nitor_wart", "osmonium", "petinia",
						"platiolus", "plombean", "quartzanthemum", "redstodendron")
				.forEach(name -> m.withExistingParent(name, "minecraft:item/generated").texture("layer0", "agricraft:seed/" + name));
	}

	public static void registerAgricraftWeed(ModelProvider<BlockModelBuilder> m) {
		with4TexturesIn8stage(m, "weed", "agricraft:crop/crop_rhombus", "agricraft:weed/weed");
	}

	public static void registerBiomesOPlentyPlant(ModelProvider<BlockModelBuilder> m) {
		with4TexturesIn8stage(m, "burning_blossom", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/burning_blossom", "biomesoplenty:block/burning_blossom");
		with4TexturesIn8stage(m, "glowflower", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/glowflower", "biomesoplenty:block/glowflower");
		with4TexturesIn8stage(m, "glowshroom", "agricraft:crop/crop_cross", "agricraft:plant/biomesoplenty/glowshroom", "biomesoplenty:block/glowshroom");
		with4TexturesIn8stage(m, "lavender", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/lavender", "biomesoplenty:block/lavender");
		with4TexturesIn8stage(m, "orange_cosmos", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/orange_cosmos", "biomesoplenty:block/orange_cosmos");
		with4TexturesIn8stage(m, "pink_daffodil", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/pink_daffodil", "biomesoplenty:block/pink_daffodil");
		with4TexturesIn8stage(m, "pink_hibiscus", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/pink_hibiscus", "biomesoplenty:block/pink_hibiscus");
		with4TexturesIn8stage(m, "rose", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/rose", "biomesoplenty:block/rose");
		with4TexturesIn8stage(m, "toadstool", "agricraft:crop/crop_hash", "agricraft:plant/biomesoplenty/toadstool");
		with4TexturesIn8stage(m, "violet", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/violet", "biomesoplenty:block/violet");
//		with4TexturesIn8stage(m, "white_lavender", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/white_lavender", "biomesoplenty:block/white_lavender");
		with4TexturesIn8stage(m, "wilted_lily", "agricraft:crop/crop_plus", "agricraft:plant/biomesoplenty/wilted_lily", "biomesoplenty:block/wilted_lily");
	}

	public static void registerBiomesOPlentySeed(ModelProvider<ItemModelBuilder> m) {
		List.of("burning_blossom", "glowflower", "glowshroom", "lavender", "orange_cosmos", "pink_daffodil",
						"pink_hibiscus", "rose", "toadstool", "violet"/*, "white_lavender"*/, "wilted_lily")
				.forEach(name -> m.withExistingParent(name, "minecraft:item/generated").texture("layer0", "agricraft:seed/biomesoplenty/" + name));
	}

	public static void registerImmersiveEngineeringPlant(ModelProvider<BlockModelBuilder> m) {
		m.withExistingParent("hemp_stage0", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom0");
		m.withExistingParent("hemp_stage1", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom0");
		m.withExistingParent("hemp_stage2", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom1");
		m.withExistingParent("hemp_stage3", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom1");
		m.withExistingParent("hemp_stage4", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom2");
		m.withExistingParent("hemp_stage5", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom2");
		m.withExistingParent("hemp_stage6", "agricraft:crop/crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom3");
		m.withExistingParent("hemp_stage7", "agricraft:crop/tall_crop_hash").texture("crop", "immersiveengineering:block/hemp/bottom4").texture("crop_top", "immersiveengineering:block/hemp/top0");
	}

	public static void registerImmersiveEngineeringSeed(ModelProvider<ItemModelBuilder> m) {
		m.withExistingParent("hemp", "immersiveengineering:item/hemp");
	}

	public static void registerPamsHarvestCraft2Plant(ModelProvider<BlockModelBuilder> m) {
		List.of("agave", "amaranth", "arrowroot", "artichoke", "asparagus", "barley", "bean", "bellpepper", "blackberry",
						"blueberry", "broccoli", "brusselsprout", "cabbage", "cactusfruit", "candleberry", "cantaloupe", "cassava",
						"cauliflower", "celery", "chickpea", "chilipepper", "coffeebean", "corn", "cotton", "cranberry", "cucumber",
						"eggplant", "elderberry", "flax", "garlic", "ginger", "grape", "greengrape", "huckleberry", "jicama", "juniperberry",
						"jute", "kale", "kenaf", "kiwi", "kohlrabi", "leek", "lentil", "lettuce", "millet", "mulberry", "mustardseeds",
						"oats", "okra", "onion", "parsnip", "peanut", "peas", "pineapple", "quinoa", "radish", "raspberry", "rhubarb",
						"rice", "rutabaga", "rye", "scallion", "sesameseeds", "sisal", "soybean", "spiceleaf", "spinach", "strawberry",
						"sweetpotato", "taro", "tealeaf", "tomatillo", "tomato", "turnip", "waterchestnut", "whitemushroom", "wintersquash",
						"zucchini", "alfalfa", "aloe", "barrelcactus", "canola", "cattail", "chia", "cloudberry", "lotus", "nettles",
						"nopales", "sorghum", "truffle", "wolfberry", "yucca", "bokchoy", "calabash", "guarana", "papyrus", "sunchoke")
				.forEach(name -> {
					m.withExistingParent(name + "_stage0", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_0");
					m.withExistingParent(name + "_stage1", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_0");
					m.withExistingParent(name + "_stage2", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_0");
					m.withExistingParent(name + "_stage3", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_1");
					m.withExistingParent(name + "_stage4", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_1");
					m.withExistingParent(name + "_stage5", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_1");
					m.withExistingParent(name + "_stage6", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_1");
					m.withExistingParent(name + "_stage7", "agricraft:crop/crop_hash").texture("crop", "pamhc2crops:block/" + name + "_stage_3");
				});
	}

	public static void registerPamsHarvestCraft2Seed(ModelProvider<ItemModelBuilder> m) {
		List.of("agave", "amaranth", "arrowroot", "artichoke", "asparagus", "barley", "bean", "bellpepper", "blackberry",
						"blueberry", "broccoli", "brusselsprout", "cabbage", "cactusfruit", "candleberry", "cantaloupe", "cassava",
						"cauliflower", "celery", "chickpea", "chilipepper", "coffeebean", "corn", "cotton", "cranberry", "cucumber",
						"eggplant", "elderberry", "flax", "garlic", "ginger", "grape", "greengrape", "huckleberry", "jicama", "juniperberry",
						"jute", "kale", "kenaf", "kiwi", "kohlrabi", "leek", "lentil", "lettuce", "millet", "mulberry", "mustardseeds",
						"oats", "okra", "onion", "parsnip", "peanut", "peas", "pineapple", "quinoa", "radish", "raspberry", "rhubarb",
						"rice", "rutabaga", "rye", "scallion", "sesameseeds", "sisal", "soybean", "spiceleaf", "spinach", "strawberry",
						"sweetpotato", "taro", "tealeaf", "tomatillo", "tomato", "turnip", "waterchestnut", "whitemushroom", "wintersquash",
						"zucchini", "alfalfa", "aloe", "barrelcactus", "canola", "cattail", "chia", "cloudberry", "lotus", "nettles",
						"nopales", "sorghum", "truffle", "wolfberry", "yucca", "bokchoy", "calabash", "guarana", "papyrus", "sunchoke")
				.forEach(name -> m.withExistingParent(name, "pamhc2crops:item/" + name + "seeditem"));
	}

	public static void registerMysticalAgriculturePlant(ModelProvider<BlockModelBuilder> m) {
		List.of("basalz", "blaze", "blitz", "blizz", "chicken", "cow", "creeper", "enderman", "fish", "ghast", "pig", "rabbit", "sheep",
				"skeleton", "slime", "spider", "squid", "turtle", "wither_skeleton", "zombie")
						.forEach(name -> mysticalAgriculture(m, name, "mysticalagriculture:block/mystical_mob_crop", "mysticalagriculture:block/flower/" + name + "_flower"));

		List.of("aluminum", "amethyst_bronze", "amethyst", "apatite", "aquamarine", "basalt", "blazing_crystal", "brass", "bronze",
						"certus_quartz", "coal", "cobalt", "compressed_iron", "conductive_alloy", "constantan", "copper_alloy", "copper", "coral",
						"cyanite", "dark_steel", "deepslate", "diamond", "dirt", "draconium", "dye", "electrum", "elementium", "emerald", "enderium",
						"end", "end_steel", "energetic_alloy", "energized_steel", "experience", "fiery_ingot", "fluix", "fluorite", "flux_infused_gem",
						"flux_infused_ingot", "glowstone", "gold", "grains_of_infinity", "graphite", "hepatizon", "honey", "hop_graphite", "ice", "inferium",
						"invar", "iridium", "ironwood", "iron", "knightmetal", "lapis_lazuli", "lead", "limestone", "lumium", "manasteel", "manyullyn",
						"marble", "menril", "mystical_flower", "nature", "netherite", "nether", "nether_quartz", "nickel", "niotic_crystal", "obsidian",
						"osmium", "peridot", "pig_iron", "platinum", "prismarine", "pulsating_alloy", "quartz_enriched_iron", "queens_slime",
						"redstone_alloy", "redstone", "refined_glowstone", "refined_obsidian", "rock_crystal", "rose_gold", "rubber", "ruby", "saltpeter",
						"sapphire", "signalum", "silicon", "silver", "sky_stone", "slimesteel", "soularium", "soulium", "spirited_crystal", "starmetal",
						"steeleaf", "steel", "stone", "sulfur", "terrasteel", "tin", "uraninite", "uranium", "vibrant_alloy", "wood", "yellorium", "zinc")
				.forEach(name -> mysticalAgriculture(m, name, "mysticalagriculture:block/mystical_resource_crop", "mysticalagriculture:block/flower/" + name + "_flower"));

		List.of("air", "chrome", "earth", "fire", "mithril", "titanium", "tungsten", "water")
				.forEach(name -> mysticalAgriculture(m, name, "mysticalagriculture:block/mystical_resource_crop", "mysticalagriculture:block/flower_ingot"));
	}

	private static void mysticalAgriculture(ModelProvider<BlockModelBuilder> m, String name, String baseTexture, String flowerTexture) {
		m.withExistingParent(name + "_stage0", baseTexture + "_0");
		m.withExistingParent(name + "_stage1", baseTexture + "_1");
		m.withExistingParent(name + "_stage2", baseTexture + "_2");
		m.withExistingParent(name + "_stage3", baseTexture + "_3");
		m.withExistingParent(name + "_stage4", baseTexture + "_4");
		m.withExistingParent(name + "_stage5", baseTexture + "_5");
		m.withExistingParent(name + "_stage6", baseTexture + "_6");
		m.withExistingParent(name + "_stage7", baseTexture + "_7").texture("flower", flowerTexture);
	}

	public static void registerMysticalAgricultureSeed(ModelProvider<ItemModelBuilder> m) {
		List.of("air", "earth", "water", "fire", "inferium", "stone", "dirt", "wood", "ice", "deepslate", "nature", "dye",
						"nether", "coal", "coral", "honey", "amethyst", "pig", "chicken", "cow", "sheep", "squid", "fish", "slime",
						"turtle", "rubber", "silicon", "sulfur", "aluminum", "saltpeter", "apatite", "grains_of_infinity", "mystical_flower",
						"marble", "limestone", "basalt", "menril", "iron", "copper", "nether_quartz", "glowstone", "redstone", "obsidian",
						"prismarine", "zombie", "skeleton", "creeper", "spider", "rabbit", "tin", "bronze", "zinc", "brass", "silver", "lead",
						"graphite", "blizz", "blitz", "basalz", "amethyst_bronze", "slimesteel", "pig_iron", "copper_alloy", "redstone_alloy",
						"conductive_alloy", "manasteel", "steeleaf", "ironwood", "aquamarine", "sky_stone", "certus_quartz", "quartz_enriched_iron",
						"gold", "lapis_lazuli", "end", "experience", "blaze", "ghast", "enderman", "steel", "nickel", "constantan", "electrum",
						"invar", "mithril", "tungsten", "titanium", "uranium", "chrome", "ruby", "sapphire", "peridot", "soulium", "signalum",
						"lumium", "flux_infused_ingot", "hop_graphite", "cobalt", "rose_gold", "soularium", "dark_steel", "pulsating_alloy",
						"energetic_alloy", "elementium", "osmium", "fluorite", "refined_glowstone", "refined_obsidian", "knightmetal", "fiery_ingot",
						"starmetal", "compressed_iron", "fluix", "energized_steel", "blazing_crystal", "diamond", "emerald", "netherite",
						"wither_skeleton", "platinum", "iridium", "enderium", "flux_infused_gem", "manyullyn", "queens_slime", "hepatizon",
						"vibrant_alloy", "end_steel", "terrasteel", "rock_crystal", "draconium", "yellorium", "cyanite", "niotic_crystal",
						"spirited_crystal", "uraninite")
				.forEach(name -> m.withExistingParent(name, "mysticalagriculture:item/" + name + "_seeds"));
	}

	public static void registerFarmersDelightPlant(ModelProvider<BlockModelBuilder> m) {
		with8TexturesIn8stage(m, "cabbage", "agricraft:crop/crop_cross", "farmersdelight:block/cabbages");
		with4TexturesIn8stage(m, "onion", "agricraft:crop/crop_hash", "farmersdelight:block/onions");
		with4TexturesIn8stage(m, "tomato", "agricraft:crop/crop_cross", "farmersdelight:block/tomatoes");

		m.withExistingParent("rice_stage0", "agricraft:crop/crop_cross").texture("crop", "farmersdelight:block/rice_stage0");
		m.withExistingParent("rice_stage1", "agricraft:crop/crop_cross").texture("crop", "farmersdelight:block/rice_stage1");
		m.withExistingParent("rice_stage2", "agricraft:crop/crop_cross").texture("crop", "farmersdelight:block/rice_stage2");
		m.withExistingParent("rice_stage3", "agricraft:crop/crop_cross").texture("crop", "farmersdelight:block/rice_stage3");
		m.withExistingParent("rice_stage4", "agricraft:crop/tall_crop_cross").texture("crop", "farmersdelight:block/rice_supporting").texture("crop_top", "farmersdelight:block/rice_panicles_stage0");
		m.withExistingParent("rice_stage5", "agricraft:crop/tall_crop_cross").texture("crop", "farmersdelight:block/rice_supporting").texture("crop_top", "farmersdelight:block/rice_panicles_stage1");
		m.withExistingParent("rice_stage6", "agricraft:crop/tall_crop_cross").texture("crop", "farmersdelight:block/rice_supporting").texture("crop_top", "farmersdelight:block/rice_panicles_stage2");
		m.withExistingParent("rice_stage7", "agricraft:crop/tall_crop_cross").texture("crop", "farmersdelight:block/rice_supporting").texture("crop_top", "farmersdelight:block/rice_panicles_stage3");
	}

	public static void registerFarmersDelightSeed(ModelProvider<ItemModelBuilder> m) {
		m.withExistingParent("cabbage", "farmersdelight:item/cabbage_seeds");
		m.withExistingParent("onion", "farmersdelight:item/onion");
		m.withExistingParent("rice", "farmersdelight:item/rice");
		m.withExistingParent("tomato", "farmersdelight:item/tomato_seeds");
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

	private static void with4TexturesIn8stage(ModelProvider<BlockModelBuilder> m, String name, String parent, String baseTexture, String mature) {
		m.withExistingParent(name + "_stage0", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage1", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage2", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage3", parent).texture("crop", baseTexture + "_stage1");
		m.withExistingParent(name + "_stage4", parent).texture("crop", baseTexture + "_stage1");
		m.withExistingParent(name + "_stage5", parent).texture("crop", baseTexture + "_stage2");
		m.withExistingParent(name + "_stage6", parent).texture("crop", baseTexture + "_stage2");
		m.withExistingParent(name + "_stage7", parent).texture("crop", mature);
	}

	private static void with8TexturesIn8stage(ModelProvider<BlockModelBuilder> m, String name, String parent, String baseTexture) {
		m.withExistingParent(name + "_stage0", parent).texture("crop", baseTexture + "_stage0");
		m.withExistingParent(name + "_stage1", parent).texture("crop", baseTexture + "_stage1");
		m.withExistingParent(name + "_stage2", parent).texture("crop", baseTexture + "_stage2");
		m.withExistingParent(name + "_stage3", parent).texture("crop", baseTexture + "_stage3");
		m.withExistingParent(name + "_stage4", parent).texture("crop", baseTexture + "_stage4");
		m.withExistingParent(name + "_stage5", parent).texture("crop", baseTexture + "_stage5");
		m.withExistingParent(name + "_stage6", parent).texture("crop", baseTexture + "_stage6");
		m.withExistingParent(name + "_stage7", parent).texture("crop", baseTexture + "_stage7");
	}
}
