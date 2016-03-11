package com.infinityraider.agricraft.handler.config;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.utility.IOHelper;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to handle the loading of the configuration file.
 */
public class ConfigurationHandler {
	
	public static Configuration config;
	private static String directory;
	
	private static final Map<Class, List<Field>> configurables = new HashMap<>();

	public static final synchronized void addConfigurable(Class clazz) {
		if (!configurables.containsKey(clazz)) {
			List<Field> fields = new ArrayList<>();
			//LogHelper.debug("Registering Configurable: " + clazz.getCanonicalName());
			for (Field f : clazz.getDeclaredFields()) {
				if (f.getAnnotation(AgriCraftConfigurable.class) != null) {
					//LogHelper.debug("Handling Configurable Field: " + f.getName());
					if (!Modifier.isStatic(f.getModifiers())) {
						LogHelper.error("Configurable Field: " + f.getName() + " is not static!");
					} else if (Modifier.isFinal(f.getModifiers())) {
						LogHelper.error("Configurable Field: " + f.getName() + " is final!");
					} else {
						fields.add(f);
					}
				}
			}
			configurables.put(clazz, fields);
			if (config != null) {
				loadConfiguration();
			}
		}
	}

	protected static final void handleConfigurable(Field f) {

		//LogHelper.debug("Loading Configurable Field: " + e.getName());
		final AgriCraftConfigurable anno = f.getAnnotation(AgriCraftConfigurable.class);
		try {

			f.setAccessible(true);
			Object obj = f.get(null);

			if (obj instanceof String) {
				f.set(null, config.getString(anno.key(), anno.category().name, (String) obj, anno.comment()));
			} else if (obj instanceof Boolean) {
				f.set(null, config.getBoolean(anno.key(), anno.category().name, (boolean) obj, anno.comment()));
			} else if (obj instanceof Integer) {
				int min = Integer.parseInt(anno.min());
				int max = Integer.parseInt(anno.max());
				f.set(null, config.getInt(anno.key(), anno.category().name, (int) obj, min, max, anno.comment()));
			} else if (obj instanceof Float) {
				float min = Float.parseFloat(anno.min());
				float max = Float.parseFloat(anno.max());
				f.set(null, config.getFloat(anno.key(), anno.category().name, (float) obj, min, max, anno.comment()));
			} else {
				LogHelper.debug("Bad Type: " + f.getType().toString());
			}

		} catch (NumberFormatException e) {
			LogHelper.debug("Invalid parameter bound!");
		} catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
			LogHelper.printStackTrace(e);
		}

	}

	public static void init(FMLPreInitializationEvent event) {
		// Check
		checkAndCreateConfig(event);
		// Load
		loadConfiguration();
		// Log
		LogHelper.debug("Configuration Loaded");
	}

	private static void checkAndCreateConfig(FMLPreInitializationEvent event) {
		if (config == null) {
			directory = event.getModConfigurationDirectory().toString() + '/' + Reference.MOD_ID + '/';
			config = new Configuration(new File(directory, "Configuration.cfg"));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void initClientConfigs(FMLPreInitializationEvent event) {
		checkAndCreateConfig(event);

		if (config.hasChanged()) {
			config.save();
		}
	}

	//read values from the config
	private static void loadConfiguration() {
		// Annotations
		LogHelper.debug("Loading configuration values!");
		configurables.values().forEach((l) -> l.forEach((e) -> handleConfigurable(e)));
		//irrigation
		AgriCraftConfig.sprinklerRatePerHalfSecond = Math.round(AgriCraftConfig.sprinklerRatePerSecond / 2);
		AgriCraftConfig.sprinklerGrowthChancePercent = AgriCraftConfig.sprinklerGrowthChance / 100F;
		AgriCraftConfig.sprinklerGrowthIntervalTicks = AgriCraftConfig.sprinklerGrowthInterval * 20;

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static boolean enableModCompatibility(String modId) {
		boolean flag = config.getBoolean(modId, ConfigCategory.COMPATIBILITY.name, true, "set to false to disable compatibility for " + modId);
		if (config.hasChanged()) {
			config.save();
		}
		return flag;
	}

	public static String readGrassDrops() {
		return IOHelper.readOrWrite(directory, "GrassDrops", IOHelper.getGrassDrops());
	}

	public static String readCustomCrops() {
		return IOHelper.readOrWrite(directory, "CustomCrops", IOHelper.getCustomCropInstructions());
	}

	public static List<Mutation> getMutations() {
		String filePath = directory + "/mutations.json";
		File file = new File(filePath);
		if (!file.exists()) {
			List<Mutation> mutations = CropPlantHandler.getDefaultMutations();
			MutationConfig.getInstance().writeMutations(mutations, filePath);
			config.save();
			return mutations;
		} else {
			return MutationConfig.getInstance().readMutations(filePath);
		}
	}

	public static String readSpreadChances() {
		return IOHelper.readOrWrite(directory, "SpreadChancesOverrides", IOHelper.getSpreadChancesOverridesInstructions());
	}

	public static String readSeedTiers() {
		return IOHelper.readOrWrite(directory, "SeedTiers", IOHelper.getSeedTierOverridesInstructions());
	}

	public static String readSeedBlackList() {
		return IOHelper.readOrWrite(directory, "SeedBlackList", IOHelper.getSeedBlackListInstructions());
	}

	public static String readVanillaOverrides() {
		return IOHelper.readOrWrite(directory, "VanillaPlantingExceptions", IOHelper.getPlantingExceptionsInstructions());
	}

	public static String readSoils() {
		return IOHelper.readOrWrite(directory, "SoilWhitelist", IOHelper.getSoilwhitelistData());
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(Reference.MOD_ID)) {
			loadConfiguration();
			LogHelper.debug("Configuration reloaded.");
		}
	}
}
