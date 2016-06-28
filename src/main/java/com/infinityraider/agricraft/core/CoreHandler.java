package com.infinityraider.agricraft.core;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriLoader;
import com.infinityraider.agricraft.reference.Reference;
import java.nio.file.Path;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.agricraft.agricore.config.AgriConfigAdapter;

public final class CoreHandler {

	private static Path configDir;
	private static Configuration config;

	private CoreHandler() {
	}

	public static Configuration getConfig() {
		return config;
	}

	public static Path getConfigDir() {
		return configDir;
	}

	@EventHandler
	public static void preinit(FMLPreInitializationEvent event) {

		// Setup Config.
		configDir = event.getSuggestedConfigurationFile().getParentFile().toPath().resolve(Reference.MOD_ID);
		config = new Configuration(configDir.resolve("config.cfg").toFile());

		// Setup Provider
		AgriConfigAdapter provider = new ModProvider(config);
		MinecraftForge.EVENT_BUS.register(provider);

		// Initialize AgriCore
		AgriCore.init(new ModLogger(), new ModTranslator(), new ModValidator(), new ModConverter(), provider);

	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {

		// Load the core!
		AgriCore.getLogger("AgriCraft").info("Attempting to load plants!");
		AgriLoader.loadManifest(
				configDir.resolve("plants/manifest.json"),
				AgriCore.getPlants(),
				AgriCore.getMutations(),
				AgriCore.getRecipes(),
				AgriCore.getItems()
		);
		AgriCore.getLogger("AgriCraft").info("Finished trying to load plants!");

		// See if plants are valid...
		AgriCore.getCoreLogger().debug("Validated Mutations: {0}", AgriCore.getPlants().getAll().size());
		AgriCore.getPlants().validate();
		AgriCore.getCoreLogger().debug("Unvalidated Mutations: {0}", AgriCore.getPlants().getAll().size());

		// See if mutations are valid...
		AgriCore.getCoreLogger().debug("Unvalidated Mutations: {0}", AgriCore.getMutations().getAll().size());
		AgriCore.getMutations().validate();
		AgriCore.getCoreLogger().debug("Validated Mutations: {0}", AgriCore.getMutations().getAll().size());
		
		// Save settings!
		AgriCore.getConfig().save();

	}

}
