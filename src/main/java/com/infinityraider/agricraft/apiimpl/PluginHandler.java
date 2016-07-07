/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.AgriPlugin;
import com.infinityraider.agricraft.api.IAgriPlugin;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author RlonRyan
 */
public final class PluginHandler {

	private static List<IAgriPlugin> plugins = new ArrayList<>();

	public static void preInit(FMLPreInitializationEvent event) {
		plugins = getInstances(event.getAsmData(), AgriPlugin.class, IAgriPlugin.class);
	}
	
	public static void init() {
		plugins.forEach(IAgriPlugin::initPlugin);
	}
	
	public static void postInit() {
		registerPlants(PlantRegistry.getInstance());
		registerMutations(MutationRegistry.getInstance());
		registerStats(StatRegistry.getInstance());
		registerSeeds(SeedRegistry.getInstance());
		registerFertilizers(FertilizerRegistry.getInstance());
	}

	public static void registerPlants(IAgriPlantRegistry plantRegistry) {
		plugins.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerPlants(plantRegistry));
	}
	
	public static void registerMutations(IAgriMutationRegistry mutationRegistry) {
		plugins.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerMutations(mutationRegistry));
	}
	
	public static void registerStats(IAgriAdapterRegistry<IAgriStat> statRegistry) {
		plugins.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerStats(statRegistry));
	}
	
	public static void registerSeeds(IAgriAdapterRegistry<AgriSeed> seedRegistry) {
		plugins.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerSeeds(seedRegistry));
	}
	
	public static void registerFertilizers(IAgriAdapterRegistry<IAgriFertilizer> fertilizerRegistry) {
		plugins.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerFertilizers(fertilizerRegistry));
	}
	
	/**
	 * Loads classes with a specific annotation from an asm data table.
	 * 
	 * Borrowed from JEI's source code, which is licensed under the MIT license.
	 * 
	 * @param <T> The type of class to load.
	 * @param asm The asm data table to load classes from.
	 * @param anno The annotation marking classes of interest.
	 * @param type The class type to load, as to get around Type erasure.
	 * @return A list of the loaded classes, instantiated.
	 */
	private static <T> List<T> getInstances(ASMDataTable asm, Class anno, Class<T> type) {
		List<T> instances = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asm.getAll(anno.getCanonicalName())) {
			try {
				T instance = Class.forName(asmData.getClassName()).asSubclass(type).newInstance();
				instances.add(instance);
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				AgriCore.getLogger("AgriCraft-Plugins").debug(
						"\nFailed to load class: {0}!\n\tFor annotation: {1}!\n\tAs Instanceof: {2}!",
						asmData.getClassName(),
						anno.getCanonicalName(),
						type.getCanonicalName()
				);
			}
		}
		return instances;
	}

}
