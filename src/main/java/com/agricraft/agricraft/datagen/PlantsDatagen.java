package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.codecs.AgriBlockCondition;
import com.agricraft.agricraft.api.codecs.AgriFluidCondition;
import com.agricraft.agricraft.api.codecs.AgriParticleEffect;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriPlantModifierInfo;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.HIGHLY_ACIDIC;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.HIGHLY_ALKALINE;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.NEUTRAL;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.SLIGHTLY_ALKALINE;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.ARID;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.DAMP;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.DRY;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.WATERY;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.WET;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.HIGH;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.LOW;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.MEDIUM;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.VERY_HIGH;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.EQUAL;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.EQUAL_OR_HIGHER;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.EQUAL_OR_LOWER;
import static com.agricraft.agricraft.api.requirement.AgriSeason.AUTUMN;
import static com.agricraft.agricraft.api.requirement.AgriSeason.SPRING;
import static com.agricraft.agricraft.api.requirement.AgriSeason.SUMMER;
import static com.agricraft.agricraft.api.requirement.AgriSeason.WINTER;

public class PlantsDatagen {

	/**
	 * this is used for easy generation of farmingforblockheads market data. see {@link ModRecipeProvider#buildRecipes(RecipeOutput, HolderLookup.Provider)}.
	 */
	public static final Map<String, List<String>> GENERATED_PLANTS = new HashMap<>();

	public static void registerPlants(BootstrapContext<AgriPlant> context) {
		minecraft(context, "allium", new AgriPlant.Builder().seeds().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:magenta_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:allium").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "azure_bluet", new AgriPlant.Builder().stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:azure_bluet").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "bamboo", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:bamboo").build()).stages(3, 6, 9, 12, 15, 18, 21, 24).chances(0.9, 0.01, 0.1).products(AgriProduct.builder().item("minecraft:bamboo").count(1, 2, 1000.0).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.4).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.4).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.4).light(10, 16, 0.5).seasons(SPRING, SUMMER).build()).build());
		minecraft(context, "beetroot", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:beetroot_seeds").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:beetroot").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(AUTUMN).build()).build());
		minecraft(context, "blue_orchid", new AgriPlant.Builder().stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:light_blue_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:blue_orchid").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "brown_mushroom", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:brown_mushroom").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:brown_mushroom").count(2, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:tree", "minecraft:brown_mushroom")).build());
		minecraft(context, "cactus", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:cactus").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:cactus").count(1, 3, 1.0).build()).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.34).acidity(NEUTRAL, EQUAL, 0.2).nutrients(LOW, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:thorns")).build());
		minecraft(context, "carrot", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:carrot").build()).stages(2, 3, 4, 5, 6, 7, 8, 9).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:carrot").count(1, 4, 1.0).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, AUTUMN).build()).build());
		minecraft(context, "cornflower", new AgriPlant.Builder().stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:blue_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:cornflower").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "crimson_fungus", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:crimson_fungus").build()).stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:crimson_fungus").count(2, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.15).acidity(HIGHLY_ACIDIC, EQUAL, 0.2).nutrients(LOW, EQUAL_OR_LOWER, 0.1).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:fungus", "minecraft:crimson_fungus")).build());
		minecraft(context, "dandelion", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:yellow_dye").count(1, 1, 0.5).build()).clips(AgriProduct.builder().item("minecraft:dandelion").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "kelp", new AgriPlant.Builder().seeds().stages(6, 6, 12, 12, 12, 18, 18, 24).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:kelp").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.4).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(5, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).fluid(AgriFluidCondition.builder().fluid("minecraft:water").build()).build()).build());
		minecraft(context, "lily_of_the_valley", new AgriPlant.Builder().stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:white_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:lily_of_the_valley").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "melon", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:melon_seeds").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:melon").count(3, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).build());
		minecraft(context, "nether_wart", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:nether_wart").build()).stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:nether_wart").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).build());
		minecraft(context, "orange_tulip", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:orange_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:orange_tulip").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "oxeye_daisy", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:oxeye_daisy").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "pink_tulip", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:pink_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:pink_tulip").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "poppy", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:red_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:poppy").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "potato", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:potato").build()).stages(2, 3, 4, 5, 6, 7, 8, 9).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:potato").count(1, 4, 0.95).build(), AgriProduct.builder().item("minecraft:poisonous_potato").count(1, 2, 0.02).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "pumpkin", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:pumpkin_seeds").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:pumpkin").count(1, 2, 1.0).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER, AUTUMN).build()).build());
		minecraft(context, "red_mushroom", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:red_mushroom").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:red_mushroom").count(2, 4, 1.0).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:tree", "minecraft:red_mushroom")).build());
		minecraft(context, "red_tulip", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:red_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:red_tulip").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "seagrass", new AgriPlant.Builder().stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:seagrass").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.4).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(5, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).fluid(AgriFluidCondition.builder().fluid("minecraft:water").build()).build()).build());
		minecraft(context, "sea_pickle", new AgriPlant.Builder().stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:sea_pickle").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.4).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(0, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).fluid(AgriFluidCondition.builder().fluid("minecraft:water").build()).build()).modifiers(AgriPlantModifierInfo.of("agricraft:brightness")).build());
		minecraft(context, "sugar_cane", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:sugar_cane").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:sugar_cane").count(1, 2, 1000.0).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).build());
		minecraft(context, "sweet_berries", new AgriPlant.Builder().stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:sweet_berries").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN).build()).modifiers(AgriPlantModifierInfo.of("agricraft:bushy")).build());
		minecraft(context, "torchflower", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:torchflower_seeds").build()).stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:orange_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:torchflower").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "warped_fungus", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:warped_fungus").build()).stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:warped_fungus").count(2, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.15).acidity(HIGHLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL_OR_LOWER, 0.1).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:fungus", "minecraft:warped_fungus")).build());
		minecraft(context, "wheat", new AgriPlant.Builder().seeds(AgriSeed.builder().item("minecraft:wheat_seeds").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:wheat").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER, AUTUMN).build()).build());
		minecraft(context, "white_tulip", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:light_gray_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:white_tulip").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).build());
		minecraft(context, "wither_rose", new AgriPlant.Builder().stages16().chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("minecraft:black_dye").count(1, 1, 0.75).build()).clips(AgriProduct.builder().item("minecraft:wither_rose").count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(SPRING).build()).modifiers(AgriPlantModifierInfo.of("agricraft:wither")).build());

		agricraft(context, "amathyllis", new AgriPlant.Builder().stages(2, 3, 5, 6, 7, 9, 11, 12).chances(0.5, 0.025, 0.1).products(AgriProduct.builder().item("agricraft:amathyllis_petal").count(1, 1, 0.8).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().item("minecraft:amethyst_block").build()).build()).build());
		agricraft(context, "aurigold", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/gold").count(1, 1, 0.8).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/gold").build()).build()).build());
		agricraft(context, "carbonation", new AgriPlant.Builder().stages16().chances(0.35, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/coal").count(1, 1, 0.8).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/coal").build()).build()).build());
		agricraft(context, "cuprosia", new AgriPlant.Builder().stages16().products(AgriProduct.builder().tag("c:nuggets/copper").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/copper").build()).build()).build());
		agricraft(context, "diamahlia", new AgriPlant.Builder().stages16().chances(0.35, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/diamond").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/diamond").build()).build()).build());
		agricraft(context, "emeryllis", new AgriPlant.Builder().stages16().chances(0.35, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/emerald").count(1, 1, 0.8).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/emerald").build()).build()).build());
		agricraft(context, "ferranium", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/iron").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/iron").build()).build()).build());
		agricraft(context, "jaslumine", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/aluminium").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/aluminium").build()).build()).build());
		agricraft(context, "lapender", new AgriPlant.Builder().stages16().chances(0.6, 0.025, 0.1).products(AgriProduct.builder().tag("c:gems/lapis").count(1, 4, 0.9).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/lapis").build()).build()).build());
		agricraft(context, "nethereed", new AgriPlant.Builder().stages16().chances(0.15, 0.015, 0.025).products(AgriProduct.builder().item("agricraft:netherite_sliver").count(1, 1, 0.25).build()).harvest(0).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).dimensions(Level.NETHER.location()).seasons(SUMMER).blocks(AgriBlockCondition.builder().tag("c:ores/netherite_scrap").build()).fluid(AgriFluidCondition.builder().fluid("minecraft:lava").build()).build()).modifiers(AgriPlantModifierInfo.of("agricraft:thorns"), AgriPlantModifierInfo.of("agricraft:bushy"), AgriPlantModifierInfo.of("agricraft:burn")).fireproof(true).build());
		agricraft(context, "niccissus", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/nickel").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/nickel").build()).build()).build());
		agricraft(context, "nitor_wart", new AgriPlant.Builder().stages16().chances(0.6, 0.025, 0.1).products(AgriProduct.builder().tag("c:dusts/glowstone").count(1, 4, 0.9).build()).harvest(3).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.1).blocks(AgriBlockCondition.builder().item("minecraft:glowstone").build()).build()).modifiers(AgriPlantModifierInfo.of("agricraft:brightness")).build());
		agricraft(context, "osmonium", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/osmium").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/osmmium").build()).build()).build());
		agricraft(context, "petinia", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/tin").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/tin").build()).build()).build());
		agricraft(context, "platiolus", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/platinum").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/platinum").build()).build()).build());
		agricraft(context, "plombean", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/lead").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(WINTER).blocks(AgriBlockCondition.builder().tag("c:ores/lead").build()).build()).build());
		agricraft(context, "quartzanthemum", new AgriPlant.Builder().stages16().chances(0.6, 0.025, 0.1).products(AgriProduct.builder().tag("c:nuggets/quartz").count(1, 5, 0.875).build()).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.3).acidity(NEUTRAL, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/quartz").build()).build()).build());
		agricraft(context, "redstodendron", new AgriPlant.Builder().stages16().chances(0.5, 0.025, 0.1).products(AgriProduct.builder().tag("c:dusts/redstone").count(1, 2, 0.95).build()).requirement(AgriRequirement.builder().humidity(DRY, EQUAL, 0.2).acidity(SLIGHTLY_ALKALINE, EQUAL, 0.2).nutrients(LOW, EQUAL, 0.2).seasons(SPRING).blocks(AgriBlockCondition.builder().tag("c:ores/redstone").build()).build()).modifiers(AgriPlantModifierInfo.of("agricraft:redstone")).build());
	}

	public static void registerBiomesOPlenty(BootstrapContext<AgriPlant> context) {
		r(context, "biomesoplenty", "burning_blossom", flower("minecraft:orange_dye", "biomesoplenty:burning_blossom", SUMMER, AUTUMN).modifiers(AgriPlantModifierInfo.of("agricraft:burn")).particles(new AgriParticleEffect("minecraft:flame", 0.5, 0.75, 0.5, 0.125, List.of(7)), new AgriParticleEffect("minecraft:smoke", 0.5, 0.75, 0.5, 0.25, List.of(7))).build());
		r(context, "biomesoplenty", "glowflower", flower("minecraft:cyan_dye", "biomesoplenty:glowflower", SPRING, SUMMER).build());
		r(context, "biomesoplenty", "glowshroom", new AgriPlant.Builder().seeds(AgriSeed.builder().item("biomesoplenty:glowshroom").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("biomesoplenty:glowshroom").count(2, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).modifiers(AgriPlantModifierInfo.of("agricraft:brightness")).build());
		r(context, "biomesoplenty", "lavender", flower("minecraft:purple_dye", "biomesoplenty:lavender", SPRING, SUMMER, AUTUMN).build());
		r(context, "biomesoplenty", "orange_cosmos", flower("minecraft:orange_dye", "biomesoplenty:orange_cosmos", SUMMER, AUTUMN).build());
		r(context, "biomesoplenty", "pink_daffodil", flower("minecraft:pink_dye", "biomesoplenty:pink_daffodil", SPRING, SUMMER).build());
		r(context, "biomesoplenty", "pink_hibiscus", flower("minecraft:pink_dye", "biomesoplenty:pink_hibiscus", SUMMER).build());
		r(context, "biomesoplenty", "rose", flower("minecraft:red_dye", "biomesoplenty:rose", SPRING).build());
		r(context, "biomesoplenty", "toadstool", new AgriPlant.Builder().seeds(AgriSeed.builder().item("biomesoplenty:toadstool").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("biomesoplenty:toadstool").count(2, 5, 1.0).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2).light(0, 10, 0.5).seasons(SPRING, SUMMER, AUTUMN, WINTER).build()).build());
		r(context, "biomesoplenty", "violet", flower("minecraft:purple_dye", "biomesoplenty:violet", SUMMER).build());
		r(context, "biomesoplenty", "white_lavender", flower("minecraft:white_dye", "biomesoplenty:white_lavender", SPRING, SUMMER, AUTUMN).build());
		r(context, "biomesoplenty", "wilted_lily", flower("minecraft:gray_dye", "biomesoplenty:wilted_lily", AUTUMN, WINTER).build());
	}

	public static void registerCroptopia(BootstrapContext<AgriPlant> context) {
		Stream.of("artichoke", "asparagus", "barley", "basil", "bellpepper", "blackberry", "blueberry",
						"cantaloupe", "cranberry", "elderberry", "ginger", "mustard", "oat", "soybean", "tea",
						"tomato", "turmeric", "vanilla", "zucchini")
				.forEach(name -> croptopia(context, name, croptopiaCrop(name, SPRING, SUMMER)));
		Stream.of("cabbage", "celery", "garlic", "leek", "lettuce", "onion", "radish", "spinach")
				.forEach(name -> croptopia(context, name, croptopiaCrop(name, SPRING)));
		Stream.of("blackbean", "greenbean", "greenonion", "honeydew", "kiwi", "pepper", "pineapple",
						"raspberry", "rice", "saguaro", "strawberry", "tomatillo")
				.forEach(name -> croptopia(context, name, croptopiaCrop(name, SUMMER)));
		Stream.of("broccoli", "cauliflower", "corn", "cucumber", "currant", "eggplant", "grape", "hops", "kale",
						"olive", "peanut", "sweetpotato")
				.forEach(name -> croptopia(context, name, croptopiaCrop(name, AUTUMN)));
		Stream.of("rhubarb", "rutabaga", "squash", "yam")
				.forEach(name -> croptopia(context, name, croptopiaCrop(name, WINTER)));
		croptopia(context, "coffee", new AgriPlant.Builder().seeds(AgriSeed.builder().item("croptopia:coffee_seed").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("croptopia:coffee_beans").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).build());
		croptopia(context, "chile_pepper", croptopiaCrop("chile_pepper", SPRING, AUTUMN));
		croptopia(context, "turnip", croptopiaCrop("chile_pepper", SPRING, WINTER));
	}

	public static void registerFarmersDelight(BootstrapContext<AgriPlant> context) {
		r(context, "farmersdelight", "rice", new AgriPlant.Builder().seeds(AgriSeed.builder().item("farmersdelight:rice").build()).stages(4, 8, 12, 16, 20, 24, 28, 32).harvest(4).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("farmersdelight:rice_panicle").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.4).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(5, 16, 0.5).seasons(SPRING, SUMMER).fluid(AgriFluidCondition.builder().fluid("minecraft:water").build()).build()).build());
		r(context, "farmersdelight", "cabbage", new AgriPlant.Builder().seeds(AgriSeed.builder().item("farmersdelight:cabbage_seeds").build()).stages16().harvest(4).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("farmersdelight:cabbage").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING).build()).build());
		r(context, "farmersdelight", "onion", new AgriPlant.Builder().seeds(AgriSeed.builder().item("farmersdelight:onion").build()).stages16().harvest(4).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("farmersdelight:onion").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER, AUTUMN).build()).build());
		r(context, "farmersdelight", "tomato", new AgriPlant.Builder().seeds(AgriSeed.builder().item("farmersdelight:tomato_seeds").build()).stages16().harvest(4).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("farmersdelight:tomato").count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER).build()).build());
	}

	public static void registerImmersiveEngineering(BootstrapContext<AgriPlant> context) {
		r(context, "immersiveengineering", "hemp", new AgriPlant.Builder().seeds(AgriSeed.builder().item("immersiveengineering:seed").build()).stages(6, 10, 10, 12, 12, 16, 16, 32).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("immersiveengineering:hemp_fiber").count(1, 2, 0.85).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).build()).build());
	}

	public static void registerMysticalAgriculture(BootstrapContext<AgriPlant> context) {
		Stream.of("air", "earth", "water", "fire", "inferium", "stone", "dirt", "wood", "ice", "deepslate",
						"nature", "dye", "nether", "coal", "coral", "honey", "amethyst", "pig", "chicken", "cow",
						"sheep", "squid", "fish", "slime", "turtle", "armadillo", "iron", "copper", "nether_quartz",
						"glowstone", "redstone", "obsidian", "prismarine", "zombie", "skeleton", "creeper", "spider",
						"rabbit", "gold", "lapis_lazuli", "end", "experience", "breeze", "blaze", "ghast", "enderman",
						"diamond", "emerald", "netherite", "wither_skeleton",
						"rubber", "silicon", "sulfur", "aluminum", "saltpeter", "tin", "bronze", "zinc", "brass",
						"silver", "lead", "graphite", "steel", "nickel", "constantan", "electrum", "invar", "uranium",
						"platinum", "iridium", "apatite", "ruby", "sapphire", "peridot", "soulium",
						"blizz", "blitz", "basalz", "signalum", "lumium", "enderium",  // thermal expansion
						"flux_infused_ingot", "flux_infused_gem",  // redstone arsenal
						"hop_graphite",  // immersive engineering
						"amethyst_bronze", "slimesteel", "pig_iron", "cobalt", "rose_gold", "manyullyn", "queens_slime", "hepatizon",  // thinker's construct
						"grains_of_infinity", "copper_alloy", "redstone_alloy", "conductive_alloy", "soularium", "dark_steel", "pulsating_alloy", "energetic_alloy", "vibrant_alloy", "end_steel",  // enderio
						"mystical_flower", "manasteel", "elementium", "terrasteel",  // botania
						"osmium", "fluorite", "refined_glowstone", "refined_obsidian",  // mekanism
						"marble", "limestone", "basalt",  // chisel
						"steeleaf", "ironwood", "knightmetal", "fiery_ingot",  // twilight forest
						"menril",  // integrateddynamics
						"aquamarine", "starmetal", "rock_crystal",  // astralsorcery
						"compressed_iron",  // pneumaticcraft
						"draconium",  // draconic evolution
						"yellorium", "cyanite",  // big reactores
						"sky_stone", "certus_quartz", "fluix",  // applied energestic 2
						"quartz_enriched_iron",  // refined storage
						"energized_steel", "blazing_crystal", "niotic_crystal", "spirited_crystal", "uraninite"  // powah
				)
				.forEach(name -> mysticalagriculture(context, name));
	}

	public static void registerPamsHarvestCraft2(BootstrapContext<AgriPlant> context) {
		hcSandy(context, "agave");
		hcVegetable(context, "amaranth", AUTUMN);
		hcVegetable(context, "arrowroot", AUTUMN);
		hcVegetable(context, "artichoke", AUTUMN);
		hcVegetable(context, "asparagus", SUMMER, AUTUMN);
		hcVegetable(context, "barley", SPRING, SUMMER);
		hcVegetable(context, "bean", SPRING, SUMMER);
		hcVegetable(context, "bellpepper", SUMMER);
		hcBerry(context, "blackberry", AUTUMN);
		hcBerry(context, "blueberry", SUMMER);
		hcVegetable(context, "broccoli", SPRING);
		hcVegetable(context, "brusselsprout", SPRING);
		hcVegetable(context, "cabbage", SPRING);
		hcSandy(context, "cactusfruit");
		hcBerry(context, "candleberry", SUMMER);
		hcVegetable(context, "cantaloupe", SUMMER);
		hcVegetable(context, "cassava", SUMMER);
		hcVegetable(context, "cauliflower", SPRING);
		hcVegetable(context, "celery", SPRING);
		hcVegetable(context, "chickpea", SPRING);
		hcVegetable(context, "chilipepper", SUMMER);
		hcVegetable(context, "coffeebean", SPRING, SUMMER);
		hcVegetable(context, "corn", SUMMER, AUTUMN);
		hcVegetable(context, "cotton", SUMMER);
		hcBerry(context, "cranberry", AUTUMN);
		hcVegetable(context, "cucumber", SUMMER);
		hcVegetable(context, "eggplant", AUTUMN);
		hcBerry(context, "elderberry", AUTUMN);
		hcVegetable(context, "flax", SPRING);
		hcVegetable(context, "garlic", SPRING);
		hcVegetable(context, "ginger", SUMMER, AUTUMN);
		hcVegetable(context, "grape", SUMMER, AUTUMN);
		hcVegetable(context, "greengrape", SUMMER, AUTUMN);
		hcBerry(context, "huckleberry", AUTUMN);
		hcVegetable(context, "jicama", SPRING, AUTUMN);
		hcBerry(context, "juniperberry", AUTUMN);
		hcVegetable(context, "jute", SUMMER);
		hcVegetable(context, "kale", SPRING);
		hcVegetable(context, "kenaf", SUMMER);
		hcVegetable(context, "kiwi", SUMMER);
		hcVegetable(context, "kohlrabi", SPRING, SUMMER, AUTUMN);
		hcVegetable(context, "leek", SPRING);
		hcVegetable(context, "lentil", SPRING);
		hcVegetable(context, "lettuce", SPRING, AUTUMN);
		hcVegetable(context, "millet", SUMMER);
		hcBerry(context, "mulberry", SUMMER);
		hcVegetable(context, "mustardseeds", SUMMER);
		hcVegetable(context, "oats", SPRING, AUTUMN);
		hcVegetable(context, "okra", SUMMER);
		hcVegetable(context, "onion", SPRING, SUMMER, AUTUMN);
		hcVegetable(context, "parsnip", SPRING);
		hcVegetable(context, "peanut", SUMMER);
		hcVegetable(context, "peas", SPRING, SUMMER, AUTUMN);
		hcVegetable(context, "pineapple", SUMMER);
		hcVegetable(context, "quinoa", SPRING, AUTUMN);
		hcVegetable(context, "radish", SUMMER);
		hcBerry(context, "raspberry", SUMMER);
		hcVegetable(context, "rhubarb", SPRING);
		r(context, "pamhc2crops", "rice", hcCrop("rice").requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).build());
		hcVegetable(context, "rutabaga", AUTUMN);
		hcVegetable(context, "rye", AUTUMN);
		hcVegetable(context, "scallion", SPRING);
		hcVegetable(context, "sesameseeds", SUMMER);
		hcSandy(context, "sisal");
		hcVegetable(context, "soybean", SUMMER);
		hcVegetable(context, "spiceleaf", SUMMER);
		hcVegetable(context, "spinach", SPRING, AUTUMN);
		hcBerry(context, "strawberry", SPRING);
		hcVegetable(context, "sweetpotato", SPRING, SUMMER, AUTUMN);
		hcVegetable(context, "taro", SUMMER);
		hcVegetable(context, "tealeaf", SPRING, SUMMER, AUTUMN);
		hcVegetable(context, "tomatillo", SUMMER);
		hcVegetable(context, "tomato", SUMMER);
		hcVegetable(context, "turnip", SPRING);
		hcVegetable(context, "waterchestnut", SUMMER);
		r(context, "pamhc2crops", "whitemushroom", hcCrop("whitemushroom").requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(NEUTRAL, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_LOWER, 0.2).light(0, 10, 0.5).seasons(AUTUMN).build()).build());
		hcVegetable(context, "wintersquash", AUTUMN);
		hcVegetable(context, "zucchini", SPRING);
		hcVegetable(context, "alfalfa", SPRING);
		hcSandy(context, "aloe");
		hcSandy(context, "barrelcactus");
		hcVegetable(context, "canola", SUMMER, AUTUMN);
		r(context, "pamhc2crops", "cattail", hcCrop("cattail").requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER).build()).build());
		hcVegetable(context, "chia", SPRING);
		hcBerry(context, "cloudberry", AUTUMN);
		r(context, "pamhc2crops", "lotus", hcCrop("lotus").requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).build());
		hcVegetable(context, "nettles", SPRING);
		hcSandy(context, "nopales");
		hcVegetable(context, "sorghum", AUTUMN);
		hcBerry(context, "truffle", AUTUMN);
		hcBerry(context, "wolfberry", AUTUMN);
		hcSandy(context, "yucca");
		hcVegetable(context, "bokchoy", AUTUMN);
		hcVegetable(context, "calabash", AUTUMN);
		hcVegetable(context, "guarana", AUTUMN);
		r(context, "pamhc2crops", "papyrus", hcCrop("papyrus").requirement(AgriRequirement.builder().humidity(WATERY, EQUAL_OR_HIGHER, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SPRING, SUMMER, AUTUMN).build()).build());
		hcVegetable(context, "sunchoke", SPRING, AUTUMN);
	}

	public static void r(BootstrapContext<AgriPlant> context, String modid, String plantId, AgriPlant plant) {
		context.register(ResourceKey.create(AgriPlant.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(modid, plantId)), plant);
		if (GENERATED_PLANTS.containsKey(modid)) {
			GENERATED_PLANTS.get(modid).add(plantId);
		} else {
			ArrayList<String> list = new ArrayList<>();
			list.add(plantId);
			GENERATED_PLANTS.put(modid, list);
		}
	}

	private static void minecraft(BootstrapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		r(context, "minecraft", plantId, plant);
	}

	private static void agricraft(BootstrapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		r(context, "agricraft", plantId, plant);
	}

	private static void croptopia(BootstrapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		r(context, "croptopia", plantId, plant);
	}

	private static void mysticalagriculture(BootstrapContext<AgriPlant> context, String plantId) {
		r(context, "mysticalagriculture", plantId, new AgriPlant.Builder().seeds(AgriSeed.builder().item("mysticalagriculture:" + plantId + "_seeds").build()).stages16().harvest(3).cloneable(false).chances(0.65, 0.025, 0.1).products(AgriProduct.builder().item("mysticalagriculture:" + plantId + "_essence").count(1, 1, 0.75).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).build()).build());
	}

	public static AgriPlant croptopiaCrop(String seed, String product, AgriSeason... seasons) {
		return new AgriPlant.Builder().seeds(AgriSeed.builder().item("croptopia:" + seed).build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("croptopia:" + product).count(1, 3, 0.95).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(seasons).build()).build();
	}

	public static AgriPlant croptopiaCrop(String product, AgriSeason... seasons) {
		return croptopiaCrop(product + "_seed", product, seasons);
	}

	public static void hcVegetable(BootstrapContext<AgriPlant> context, String name, AgriSeason... seasons) {
		r(context, "pamhc2crops", name, hcCrop(name).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(seasons).build()).build());
	}

	public static void hcBerry(BootstrapContext<AgriPlant> context, String name, AgriSeason... seasons) {
		r(context, "pamhc2crops", name, hcCrop(name).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(seasons).build()).build());
	}

	public static void hcSandy(BootstrapContext<AgriPlant> context, String name) {
		r(context, "pamhc2crops", name, hcCrop(name).requirement(AgriRequirement.builder().humidity(ARID, EQUAL, 0.34).acidity(NEUTRAL, EQUAL, 0.2).nutrients(LOW, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(SUMMER).build()).build());
	}

	public static AgriPlant.Builder hcCrop(String name) {
		return new AgriPlant.Builder().seeds(AgriSeed.builder().item("pamhc2crops:" + name + "seeditem").build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item("pamhc2crops:" + name + "item").count(1, 5, 0.9).build());
	}

	public static AgriPlant.Builder flower(String product, String clip, AgriSeason... seasons) {
		return new AgriPlant.Builder().stages(2, 3, 5, 6, 8, 9, 11, 12).chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item(product).count(1, 1, 0.75).build()).clips(AgriProduct.builder().item(clip).count(0, 1, 0.5).build()).requirement(AgriRequirement.builder().humidity(DAMP, EQUAL, 0.2).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(VERY_HIGH, EQUAL_OR_HIGHER, 0.2).light(10, 16, 0.5).seasons(seasons).build());
	}

	public static AgriPlant.Builder vegetable(String seed, String product, AgriSeason... seasons) {
		return new AgriPlant.Builder().seeds(AgriSeed.builder().item(seed).build()).stages16().chances(0.75, 0.025, 0.1).products(AgriProduct.builder().item(product).count(1, 5, 0.9).build()).requirement(AgriRequirement.builder().humidity(WET, EQUAL, 0.15).acidity(SLIGHTLY_ACIDIC, EQUAL, 0.2).nutrients(HIGH, EQUAL_OR_HIGHER, 0.1).light(10, 16, 0.5).seasons(seasons).build());
	}

}
