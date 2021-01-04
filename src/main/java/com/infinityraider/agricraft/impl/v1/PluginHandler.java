package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

public final class PluginHandler {

    @Nonnull
    private static final Deque<IAgriPlugin> PLUGINS = new ConcurrentLinkedDeque<>();

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PLUGINS.addAll(getInstances(getScanData(event), AgriPlugin.class, IAgriPlugin.class));
        PLUGINS.stream()
                .peek(PluginHandler::logPlugin)
                .filter(IAgriPlugin::isEnabled)
                .forEach(MinecraftForge.EVENT_BUS::register);
    }

    public static void onInterModEnqueueEvent(InterModEnqueueEvent event) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach(plugin -> plugin.onInterModEnqueueEvent(event));
    }

    public static void onInterModProcessEvent(InterModProcessEvent event) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach(plugin -> plugin.onInterModProcessEvent(event));
    }

    public static void populateRegistries() {
        registerSoils(AgriApi.getSoilRegistry());
        registerPlants(AgriApi.getPlantRegistry());
        registerMutations(AgriApi.getMutationRegistry());
        registerStats(AgriApi.getStatRegistry());
        registerSeeds(AgriApi.getSeedRegistry());
        registerFertilizers(AgriApi.getFertilizerRegistry());
    }

    public static void loadTextures(Consumer<ResourceLocation> registry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerTextures(registry));
    }

    public static void registerSoils(IAgriSoilRegistry soilRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerSoils(soilRegistry));
    }

    public static void registerPlants(IAgriRegistry<IAgriPlant> plantRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerPlants(plantRegistry));
    }

    public static void registerMutations(IAgriMutationRegistry mutationRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerMutations(mutationRegistry));
    }

    public static void registerStats(IAgriStatRegistry statRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerStats(statRegistry));
    }

    public static void registerSeeds(IAgriAdapterizer<AgriSeed> seedRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerSeeds(seedRegistry));
    }

    public static void registerFertilizers(IAgriAdapterizer<IAgriFertilizer> fertilizerRegistry) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach((p) -> p.registerFertilizers(fertilizerRegistry));
    }

    /**
     * Loads classes with a specific annotation the modfile's FML scan data
     *
     * @param <T> The type of class to load.
     * @param data The modfile FML scan data to load classes from.
     * @param anno The annotation marking classes of interest.
     * @param type The class type to load, as to get around Type erasure.
     * @return A list of the loaded classes, instantiated.
     */
    @Nonnull
    private static <T> List<T> getInstances(ModFileScanData data, Class anno, Class<T> type) {
        if(data == null) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to load AgriPlugins");
        }
        final List<T> instances = new ArrayList<>();
        data.getAnnotations().stream().
                filter(annotationData -> Type.getType(anno).equals(annotationData.getAnnotationType())).
                forEach(annotationData -> {
                    try{
                        T instance = Class.forName(annotationData.getClassType().getClassName()).asSubclass(type).newInstance();
                        instances.add(instance);
                    } catch (ClassNotFoundException | NoClassDefFoundError | IllegalAccessException | InstantiationException e) {
                        AgriCore.getLogger("agricraft-plugins").debug(
                                "%nFailed to load AgriPlugin%n\tOf class: {0}!%n\tFor annotation: {1}!%n\tAs Instanceof: {2}!",
                                annotationData.getTargetType(),
                                anno.getCanonicalName(),
                                type.getCanonicalName()
                        );
                    }
                });
        return instances;
    }

    private static ModFileScanData getScanData(FMLCommonSetupEvent event) {
        FMLModContainer container = null;
        try {
            container = (FMLModContainer) ModLifecycleEvent.class.getDeclaredField("container").get(event);
        } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to fetch ModContainer from FMLCommonSetupEvent");
        }
        if(container == null) {
            return null;
        }
        try {
            return (ModFileScanData) FMLModContainer.class.getDeclaredField("scanResults").get(container);
        } catch(IllegalAccessException | NoSuchFieldException | ClassCastException e) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to fetch ModFileScanData from FMLModContainer");
        }
        return null;
    }
    
    private static void logPlugin(IAgriPlugin plugin) {
        AgriCore.getLogger("agricraft").info("\nFound AgriCraft Plugin:\n\t- Id: {0}\n\t- Name: {1}\n\t- Status: {2}", plugin.getId(), plugin.getName(), plugin.isEnabled() ? "Enabled" : "Disabled");
    }
}
