package com.infinityraider.agricraft.handler.config;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.utility.IOHelper;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;

/**
 * A class to handle the loading of the configuration file.
 */
public class ConfigurationHandler {

	private static String directory;

	public static void init(FMLPreInitializationEvent event) {
		directory = event.getModConfigurationDirectory().toString() + '/' + Reference.MOD_ID + '/';
		// Log
		AgriCore.getLogger("AgriCraft").debug("Configuration Loaded");
	}

	@SideOnly(Side.CLIENT)
	public static void initClientConfigs(FMLPreInitializationEvent event) {
		directory = event.getModConfigurationDirectory().toString() + '/' + Reference.MOD_ID + '/';
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

}
