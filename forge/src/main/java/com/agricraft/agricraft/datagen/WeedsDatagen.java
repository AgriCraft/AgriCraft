package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.plant.AgriWeed;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Acidity.NEUTRAL;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Humidity.DAMP;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Nutrients.MEDIUM;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.EQUAL;
import static com.agricraft.agricraft.api.codecs.AgriSoilCondition.Type.EQUAL_OR_HIGHER;

public class WeedsDatagen {

	public static void registerWeeds(BootstapContext<AgriWeed> context) {
		agricraft(context, "weed", new AgriWeed.Builder()
				.stages16()
				.requirement(AgriRequirement.builder()
					.humidity(DAMP, EQUAL, 0.15)
					.acidity(NEUTRAL, EQUAL, 0.2)
					.nutrients(MEDIUM, EQUAL_OR_HIGHER, 0.1)
					.build())
				.build());
	}

	private static void agricraft(BootstapContext<AgriWeed> context, String weedId, AgriWeed weed) {
		context.register(
				ResourceKey.create(AgriApi.AGRIWEEDS, new ResourceLocation(AgriApi.MOD_ID, weedId)),
				weed
		);
	}

}
