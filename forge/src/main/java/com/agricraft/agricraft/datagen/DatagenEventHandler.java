package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = AgriCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		AgriSeed wheat = new AgriSeed(new ExtraCodecs.TagOrElementLocation(new ResourceLocation("minecraft", "wheat_seeds"), false),
				true, new CompoundTag(), 0.0, 1.0, 0.0);
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(PlatformUtils.getSeedRegistryKey(), context -> {
									AgriCraft.LOGGER.info("hello from datagen");
									context.register(
											ResourceKey.create(PlatformUtils.getSeedRegistryKey(), new ResourceLocation(AgriCraft.MOD_ID, "wheat")),
											wheat
									);
								}),
						// Generate dynamic registry objects for this mod
						Set.of(AgriCraft.MOD_ID)
				)
		);
	}

}
