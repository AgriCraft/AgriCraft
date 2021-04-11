package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.block.BlockState;
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
                .forEach(plugin -> plugin.onCommonSetupEvent(event));
    }

    private static void executeForPlugins(Consumer<IAgriPlugin> consumer) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach(consumer);
    }

    public static void onInterModEnqueueEvent(InterModEnqueueEvent event) {
        executeForPlugins(plugin -> plugin.onInterModEnqueueEvent(event));
    }

    public static void onInterModProcessEvent(InterModProcessEvent event) {
        executeForPlugins(plugin -> plugin.onInterModProcessEvent(event));
    }

    public static void populateRegistries() {
        registerSoils(AgriApi.getSoilRegistry());
        registerWeeds(AgriApi.getWeedRegistry());
        registerPlants(AgriApi.getPlantRegistry());
        registerMutations(AgriApi.getMutationRegistry());
        registerStats(AgriApi.getStatRegistry());
        registerGenes(AgriApi.getGeneRegistry());
        registerSoilAdapters(AgriApi.getSoilAdapterizer());
        registerSeeds(AgriApi.getSeedAdapterizer());
        registerFertilizers(AgriApi.getFertilizerAdapterizer());
        registerSeasonLogic(AgriApi.getSeasonLogic());
    }

    public static void registerSoils(IAgriSoilRegistry soilRegistry) {
        executeForPlugins(plugin -> plugin.registerSoils(soilRegistry));
    }

    public static void registerWeeds(IAgriRegistry<IAgriWeed> weedRegistry) {
        executeForPlugins(plugin -> plugin.registerWeeds(weedRegistry));
    }

    public static void registerPlants(IAgriRegistry<IAgriPlant> plantRegistry) {
        executeForPlugins(plugin -> plugin.registerPlants(plantRegistry));
    }

    public static void registerMutations(IAgriMutationRegistry mutationRegistry) {
        executeForPlugins(plugin -> plugin.registerMutations(mutationRegistry));
    }

    public static void registerStats(IAgriStatRegistry statRegistry) {
        executeForPlugins(plugin -> plugin.registerStats(statRegistry));
    }

    public static void registerGenes(IAgriGeneRegistry geneRegistry) {
        executeForPlugins(plugin -> plugin.registerGenes(geneRegistry));
    }

    public static void registerSoilAdapters(IAgriAdapterizer<BlockState> adapterizer) {
        executeForPlugins(plugin -> plugin.registerSoilAdapters(adapterizer));
    }

    public static void registerSeeds(IAgriAdapterizer<AgriSeed> adapterizer) {
        executeForPlugins(plugin -> plugin.registerSeeds(adapterizer));
    }

    public static void registerFertilizers(IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        executeForPlugins(plugin -> plugin.registerFertilizers(adapterizer));
    }

    public static void registerSeasonLogic(IAgriSeasonLogic seasonLogic) {
        executeForPlugins(plugin -> plugin.registerSeasonLogic(seasonLogic));
    }

    /**
     * Loads classes with a specific annotation the modfile's FML scan data
     *
     * @param <A> The annotation type to load.
     * @param <T> The type of class to load.
     * @param data The modfile FML scan data to load classes from.
     * @param anno The annotation marking classes of interest.
     * @param type The class type to load, as to get around Type erasure.
     * @return A list of the loaded classes, instantiated.
     */
    @Nonnull
    private static <A, T> List<T> getInstances(ModFileScanData data, Class<A> anno, Class<T> type) {
        final List<T> instances = new ArrayList<>();
        if(data == null) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to load AgriPlugins, ModFileScanData not found");
        } else if(data.getAnnotations() == null || data.getAnnotations().size() <= 0) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to load AgriPlugins, no PlugIn Annotations found in ModFileScanData");
        } else {
            data.getAnnotations().stream().
                    filter(annotationData -> Type.getType(anno).equals(annotationData.getAnnotationType())).
                    forEach(annotationData -> {
                        try {
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
        }
        return instances;
    }

    private static ModFileScanData getScanData(FMLCommonSetupEvent event) {
        FMLModContainer container = null;
        try {
            Field field = ModLifecycleEvent.class.getDeclaredField("container");
            field.setAccessible(true);
            container = (FMLModContainer) field.get(event);
        } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
            AgriCore.getLogger("agricraft-plugins").error(
                    "Failed to fetch ModContainer from FMLCommonSetupEvent");
        }
        if(container == null) {
            return null;
        }
        try {
            Field field = FMLModContainer.class.getDeclaredField("scanResults");
            field.setAccessible(true);
            return (ModFileScanData) field.get(container);
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
