package com.agricraft.agricraft.client;

import com.agricraft.agricraft.AgriCraft;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AgriCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AgriCraftForgeClient {

	@SubscribeEvent
	public static void loadModels(ModelEvent.RegisterAdditional event) {
//		Registry<AgriSeed> registry = Minecraft.getInstance().level.registryAccess().registry(AgriCraftForge.AGRISEEDS_REGISTRY.get().getRegistryKey()).get();
//		for (Map.Entry<ResourceKey<AgriSeed>, AgriSeed> entry : registry.entrySet()) {
//			event.register();
//			items.add(AgriSeedItem.toStack(entry.getKey().location().toString()));
//		}
		// https://discord.com/channels/313125603924639766/983834532904042537/1104441106248253592
//		FileToIdConverter
//		event.
	}

}
