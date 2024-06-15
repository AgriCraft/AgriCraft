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
						"agricraft:quartzanthemum", "agricraft:redstodendron")
				.build());
	}

}
