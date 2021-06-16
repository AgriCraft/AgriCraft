package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;

import java.lang.annotation.Annotation;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

public final class PluginHandler {
    private static final Deque<IAgriPlugin> PLUGINS = new ConcurrentLinkedDeque<>();

    public static void initPlugins() {
        ModList.get().getAllScanData().forEach(data -> PLUGINS.addAll(getInstances(
                data, AgriPlugin.class, IAgriPlugin.class, plugin -> plugin.alwaysLoad() || ModList.get().isLoaded(plugin.modId())
        )));
    }

    public static void onAgriCraftConstructed() {
        PLUGINS.stream().peek(PluginHandler::logPlugin).filter(IAgriPlugin::isEnabled).forEach(IAgriPlugin::onAgriCraftConstructed);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        executeForPlugins(plugin -> plugin.onCommonSetupEvent(event));
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        executeForPlugins(plugin -> plugin.onClientSetupEvent(event));
    }

    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        executeForPlugins(plugin -> plugin.onServerSetupEvent(event));
    }

    public static void onInterModEnqueueEvent(InterModEnqueueEvent event) {
        executeForPlugins(plugin -> plugin.onInterModEnqueueEvent(event));
    }

    public static void onInterModProcessEvent(InterModProcessEvent event) {
        executeForPlugins(plugin -> plugin.onInterModProcessEvent(event));
    }

    private static void executeForPlugins(Consumer<IAgriPlugin> consumer) {
        PLUGINS.stream().filter(IAgriPlugin::isEnabled).forEach(consumer);
    }

    public static void populateRegistries() {
        registerSoils(AgriApi.getSoilRegistry());
        registerWeeds(AgriApi.getWeedRegistry());
        registerPlants(AgriApi.getPlantRegistry());
        registerMutations(AgriApi.getMutationRegistry());
        registerStats(AgriApi.getStatRegistry());
        registerGenes(AgriApi.getGeneRegistry());
        registerGenomes(AgriApi.getGenomeAdapterizer());
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

    public static void registerGenomes(IAgriAdapterizer<IAgriGenome> adapterizer) {
        executeForPlugins(plugin -> plugin.registerGenomes(adapterizer));
    }

    public static void registerFertilizers(IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        executeForPlugins(plugin -> plugin.registerFertilizers(adapterizer));
    }

    public static void registerSeasonLogic(IAgriSeasonLogic seasonLogic) {
        executeForPlugins(plugin -> plugin.registerSeasonLogic(seasonLogic));
    }

    /**
     * Loads classes with a specific annotation from FML scan data
     *
     * @param <A> The annotation type to load.
     * @param <T> The type of class to load.
     * @param data The modfile FML scan data to load classes from.
     * @param anno The annotation marking classes of interest.
     * @param type The class type to load, as to get around Type erasure.
     * @return A list of the loaded classes, instantiated.
     */
    @Nonnull
    private static <A extends Annotation, T> List<T> getInstances(ModFileScanData data, Class<A> anno, Class<T> type, Predicate<A> predicate) {
        return data.getAnnotations().stream()
                .filter(annotationData -> Type.getType(anno).equals(annotationData.getAnnotationType()))
                .filter(annotationData -> checkAnnotationPredicate(anno, predicate, annotationData))
                .map(annotationData -> {
                    try {
                        return Class.forName(annotationData.getClassType().getClassName()).asSubclass(type).newInstance();
                    } catch (ClassNotFoundException | NoClassDefFoundError | IllegalAccessException | InstantiationException e) {
                        AgriCore.getLogger("agricraft-plugins").debug(
                                "%nFailed to load AgriPlugin%n\tOf class: {0}!%n\tFor annotation: {1}!%n\tAs Instanceof: {2}!",
                                annotationData.getTargetType(),
                                anno.getCanonicalName(),
                                type.getCanonicalName()
                        );
                        return null;
                    }})
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static <A extends Annotation> boolean checkAnnotationPredicate(Class<A> anno, Predicate<A> predicate, ModFileScanData.AnnotationData data) {
        try {
            return predicate.test(Class.forName(data.getMemberName()).getAnnotation(anno));
        } catch (Exception e) {
            AgriCore.getLogger("agricraft-plugins").error("Failed to check plugin " + data.getMemberName());
            return false;
        }
    }
    
    private static void logPlugin(IAgriPlugin plugin) {
        AgriCore.getLogger("agricraft").info("\nFound AgriCraft Plugin:\n\t- Id: {0}\n\t- Description: {1}\n\t- Status: {2}",
                plugin.getId(),
                plugin.getDescription(),
                plugin.isEnabled() ? "Enabled" : "Disabled"
        );
    }
}
