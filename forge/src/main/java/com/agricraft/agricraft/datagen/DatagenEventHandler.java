package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriParticleEffect;
import com.agricraft.agricraft.common.codecs.AgriPlant;
import com.agricraft.agricraft.common.codecs.AgriPlantCallback;
import com.agricraft.agricraft.common.codecs.AgriProduct;
import com.agricraft.agricraft.common.codecs.AgriRequirement;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AgriCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		AgriPlant wheat = AgriPlant.builder()
				.mods("agricraft", "minecraft")
				.seeds(AgriSeed.builder().item("minecraft:wheat_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:wheat").count(1, 3, 0.95).build())
				.requirement(AgriRequirement.builder()
						.humidity("wet", "equal", 0.15)
						.acidity("slightly_acidic", "equal", 0.2)
						.nutrients("high", "equal_or_higher", 0.1)
						.light(10, 16, 0.5)
						.seasons("summer", "autumn")
						.build())
				.build();
		AgriPlant potato = AgriPlant.builder()
				.mods("agricraft", "minecraft")
				.seeds(AgriSeed.builder().item("minecraft:potato").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(
						AgriProduct.builder().item("minecraft:potato").count(1, 4, 0.95).build(),
						AgriProduct.builder().item("minecraft:poisonous_potato").count(1, 2, 0.02).build()
				)
				.requirement(AgriRequirement.builder()
						.humidity("wet", "equal", 0.15)
						.acidity("slightly_acidic", "equal", 0.2)
						.nutrients("high", "equal_or_higher", 0.1)
						.light(10, 16, 0.5)
						.seasons("spring")
						.build())
				.build();
		AgriPlant carrot = AgriPlant.builder()
				.mods("agricraft", "minecraft")
				.seeds(AgriSeed.builder().item("minecraft:carrot").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:carrot").count(1, 4, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity("wet", "equal", 0.15)
						.acidity("slightly_acidic", "equal", 0.2)
						.nutrients("high", "equal_or_higher", 0.1)
						.light(10, 16, 0.5)
						.seasons("spring", "autumn")
						.build())
				.build();
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(PlatformUtils.getPlantRegistryKey(), context -> {
									AgriCraft.LOGGER.info("hello from plant datagen");
									registerPlant(context, "wheat", wheat);
									registerPlant(context, "potato", potato);
									registerPlant(context, "carrot", carrot);
								}),
						// Generate dynamic registry objects for this mod
						Set.of(AgriCraft.MOD_ID)
				)
		);
	}

	private static void registerPlant(BootstapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		context.register(
				ResourceKey.create(PlatformUtils.getPlantRegistryKey(), new ResourceLocation(AgriCraft.MOD_ID, plantId)),
				plant
		);
	}

}
