package com.infinityraider.agricraft.core;

import com.agricraft.agricore.config.AgriConfigAdapter;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriLoader;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoil;
import com.agricraft.agricore.util.ResourceHelper;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.SoilRegistry;
import com.infinityraider.agricraft.reference.Reference;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class CoreHandler {

	public static final Pattern JSON_FILE_PATTERN = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
	public static final Pattern AGRI_FOLDER_PATTERN = Pattern.compile("json/defaults/.*", Pattern.CASE_INSENSITIVE);

	private static Path configDir;
	private static Path jsonDir;
	private static Path defaultDir;
	private static Configuration config;

	private CoreHandler() {
	}

	public static Configuration getConfig() {
		return config;
	}

	public static Path getConfigDir() {
		return configDir;
	}

	public static Path getJsonDir() {
		return jsonDir;
	}

	public static void preInit(FMLPreInitializationEvent event) {

		// Setup Config.
		configDir = event.getSuggestedConfigurationFile().getParentFile().toPath().resolve(Reference.MOD_ID);
		config = new Configuration(configDir.resolve("config.cfg").toFile());

		// Setup Plant Dir.
		jsonDir = configDir.resolve("json");
		defaultDir = jsonDir.resolve("defaults");

		// Setup Provider
		AgriConfigAdapter provider = new ModProvider(config);
		MinecraftForge.EVENT_BUS.register(provider);

		// Initialize AgriCore
		AgriCore.init(new ModLogger(), new ModTranslator(), new ModValidator(), new ModConverter(), provider);

		// Transfer Defaults
		ResourceHelper.findResources(JSON_FILE_PATTERN.asPredicate()).stream()
				.filter(AGRI_FOLDER_PATTERN.asPredicate())
				.forEach(r -> ResourceHelper.copyResource(r, configDir.resolve(r), false)
				);
	}

	public static void init() {

		// Load the core!
		AgriCore.getLogger("agricraft").info("Attempting to read AgriCraft JSONs!");
		AgriLoader.loadDirectory(
				defaultDir,
                AgriCore.getSoils(),
				AgriCore.getPlants(),
				AgriCore.getMutations()
		);
		AgriCore.getLogger("agricraft").info("Finished trying to read AgriCraft JSONs!");

		// Save settings!
		AgriCore.getConfig().save();

		// Load JSON Stuff
        initSoils();
		initPlants();
		initMutations();

	}
    
    public static void initSoils() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Soils!");
        
        // See if soils are valid...
        final int raw = AgriCore.getSoils().getAll().size();
		AgriCore.getSoils().validate();
        final int count = AgriCore.getSoils().getAll().size();
		
        // Transfer
        AgriCore.getSoils().getAll().stream()
                .filter(AgriSoil::isEnabled)
				.map(JsonSoil::new)
				.forEach(SoilRegistry.getInstance()::addSoil);
		
        // Display Soils
		AgriCore.getLogger("agricraft").info("Registered Soils ({0}/{1}):", count, raw);
        for (IAgriSoil soil : SoilRegistry.getInstance().getSoils()) {
            AgriCore.getLogger("agricraft").info(" - {0}", soil.getName());
        }
	}

	public static void initPlants() {
        // Announce Progress
		AgriCore.getLogger("agricraft").info("Registering Plants!");
        
        // See if plants are valid...
		final int raw = AgriCore.getPlants().getAll().size();
		AgriCore.getPlants().validate();
		final int count = AgriCore.getPlants().getAll().size();
        
        // Transfer
		AgriCore.getPlants().validate();
		AgriCore.getPlants().getAll().stream()
                .filter(AgriPlant::isEnabled)
				.map(JsonPlant::new)
				.forEach(PlantRegistry.getInstance()::addPlant);
        
        // Display Plants
		AgriCore.getLogger("agricraft").info("Registered Plants ({0}/{1}):", count, raw);
        for (IAgriPlant plant : PlantRegistry.getInstance().getPlants()) {
            AgriCore.getLogger("agricraft").info(" - {0}", plant.getPlantName());
        }
	}

	public static void initMutations() {
        // Announce Progress
		AgriCore.getLogger("agricraft").info("Registering Mutations!");
        
        // See if mutations are valid...
		final int raw = AgriCore.getMutations().getAll().size();
		AgriCore.getMutations().validate();
		final int count = AgriCore.getMutations().getAll().size();
        
        // Transfer
		AgriCore.getMutations().getAll().stream()
                .filter(AgriMutation::isEnabled)
				.map(JsonMutation::new)
				.forEach(MutationRegistry.getInstance()::addMutation);
        
		// Display Mutations
		AgriCore.getLogger("agricraft").info("Registered Mutations ({0}/{1}):", count, raw);
		for (IAgriMutation mutation : MutationRegistry.getInstance().getMutations()) {
			AgriCore.getLogger("agricraft").info(" - {0}", mutation);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void loadTextures(Consumer<ResourceLocation> consumer) {
		AgriCore.getPlants().getAll().stream()
                .flatMap(plant -> plant.getTexture().getAllTextures().stream())
                .distinct()
                .map(t -> new ResourceLocation(t))
                .forEach(consumer);
	}
}
