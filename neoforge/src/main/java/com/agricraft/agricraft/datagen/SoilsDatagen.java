package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilVariant;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.HIGHLY_ACIDIC;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.HIGHLY_ALKALINE;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.NEUTRAL;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.SLIGHTLY_ALKALINE;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.ARID;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.DAMP;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.DRY;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.FLOODED;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.WET;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.HIGH;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.LOW;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.MEDIUM;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.VERY_HIGH;

public class SoilsDatagen {

	public static void registerSoils(BootstapContext<AgriSoil> context) {
		mc(context, "farmland", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:farmland").build()).humidity(WET).acidity(SLIGHTLY_ACIDIC).nutrients(HIGH).growthModifier(1.0).build());
		mc(context, "sand", AgriSoil.builder().variants(AgriSoilVariant.builder().tag("forge:sand").build(), AgriSoilVariant.builder().tag("minecraft:sand").build()).humidity(ARID).acidity(NEUTRAL).nutrients(LOW).growthModifier(0.8).build());
		mc(context, "clay", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:clay").build()).humidity(FLOODED).acidity(SLIGHTLY_ACIDIC).nutrients(MEDIUM).growthModifier(1.0).build());
		mc(context, "podzol", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:podzol").build()).humidity(DAMP).acidity(SLIGHTLY_ACIDIC).nutrients(VERY_HIGH).growthModifier(1.1).build());
		mc(context, "mycelium", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:mycelium").build()).humidity(DAMP).acidity(NEUTRAL).nutrients(LOW).growthModifier(0.8).build());
		mc(context, "soul_sand", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:soul_sand").build()).humidity(ARID).acidity(NEUTRAL).nutrients(HIGH).growthModifier(0.8).build());
		mc(context, "soul_soil", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:soul_soil").build()).humidity(ARID).acidity(NEUTRAL).nutrients(VERY_HIGH).growthModifier(1.0).build());
		mc(context, "crimson_nylium", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:crimson_nylium").build()).humidity(ARID).acidity(HIGHLY_ACIDIC).nutrients(LOW).growthModifier(0.75).build());
		mc(context, "warped_nylium", AgriSoil.builder().variants(AgriSoilVariant.builder().block("minecraft:warped_nylium").build()).humidity(ARID).acidity(HIGHLY_ALKALINE).nutrients(LOW).growthModifier(0.75).build());

		agricraft(context, "gravel", AgriSoil.builder().variants(AgriSoilVariant.builder().tag("forge:gravel").build()).humidity(DRY).acidity(SLIGHTLY_ALKALINE).nutrients(LOW).growthModifier(0.5).build());
	}

	private static void mc(BootstapContext<AgriSoil> context, String soilId, AgriSoil soil) {
		context.register(ResourceKey.create(AgriApi.AGRISOILS, new ResourceLocation("minecraft", soilId)), soil);
	}

	private static void agricraft(BootstapContext<AgriSoil> context, String soilId, AgriSoil soil) {
		context.register(ResourceKey.create(AgriApi.AGRISOILS, new ResourceLocation("agricraft", soilId)), soil);
	}

}
