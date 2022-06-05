package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriLoader;
import com.agricraft.agricore.templates.*;
import com.agricraft.agricore.templates.AgriFertilizer;
import com.agricraft.agricore.util.ResourceHelper;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.reference.Reference;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

public final class CoreHandler {
    public static final Pattern JSON_FILE_PATTERN = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
    public static final Pattern AGRI_FOLDER_PATTERN = Pattern.compile("json/defaults/.*", Pattern.CASE_INSENSITIVE);
    public static final Predicate<String> MOD_FILTER = (path) -> {
        String id = path.substring(path.indexOf("json/defaults/") + 14);
        id = id.substring(0, id.indexOf("/"));
        if(id.equalsIgnoreCase("minecraft")) {
            return true;
        }
        if(id.equalsIgnoreCase("mod_agricraft")) {
            return AgriCraft.instance.getConfig().generateResourceCropJsons();
        }
        id = id.replace("mod_", "");
        return  ModList.get().isLoaded(id);
    };

    private static Path configDir;
    private static Path jsonDir;
    private static Path defaultDir;
    private static Config config;

    private static boolean initialized = false;

    private CoreHandler() {}

    public static boolean isInitialized() {
        return initialized;
    }

    public static Config getConfig() {
        return config;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static Path getJsonDir() {
        return jsonDir;
    }

    public static void loadJsons() {
        // Setup Config.
        configDir = FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID);
        config = AgriCraft.instance.getConfig();

        // Setup Plant Dir.
        jsonDir = getConfigDir().resolve("json");
        defaultDir = getJsonDir().resolve("defaults");

        // Initialize AgriCore
        AgriCore.init(new AgriLoggerImpl(), new AgriValidatorImpl(), new AgriConverterImpl(), AgriCraft.instance.getConfig());

        // rename 1.16 directory vanilla->minecraft & resource->mod_agricraft
        ResourceHelper.renameDirectory(defaultDir, "vanilla", "minecraft", false);
        ResourceHelper.renameDirectory(defaultDir, "resource", "mod_agricraft", false);

        // Transfer Defaults
        if(AgriCraft.instance.getConfig().generateMissingDefaultJsons()) {
            ResourceHelper.copyResources(
                    ModList.get().getModFiles().stream().map(IModFileInfo::getFile).map(IModFile::getFilePath),
                    JSON_FILE_PATTERN,
                    AGRI_FOLDER_PATTERN.asPredicate().and(MOD_FILTER),
                    configDir::resolve,
                    false);
        }

        // Load the JSON files.
        AgriCore.getLogger("agricraft").info("Attempting to read AgriCraft JSONs!");
        AgriLoader.loadDirectory(
                defaultDir,
                AgriCore.getSoils(),
                AgriCore.getPlants(),
                AgriCore.getWeeds(),
                AgriCore.getMutations(),
                AgriCore.getFertilizers()
        );
        AgriCore.getLogger("agricraft").info("Finished trying to read AgriCraft JSONs!");
    }

    public static void init() {
        // Load JSON Stuff
        initSoils();
        initPlants();
        initWeeds();
        initMutations();
        initFertilizers();
        // Set flag
        initialized = true;
    }

    public static void onSyncComplete() {
        // fire events
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Soil(LogicalSide.CLIENT));
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Plant(LogicalSide.CLIENT));
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Weed(LogicalSide.CLIENT));
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Mutation(LogicalSide.CLIENT));
        // set flag
        initialized = true;
    }

    public static void onLogout() {
        initialized = false;
    }

    private static void initSoils() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Soils!");

        // See if soils are valid...
        final int raw = AgriCore.getSoils().getAll().size();
        AgriCore.getSoils().validate();
        final int count = AgriCore.getSoils().getAll().size();

        // Transfer
        AgriCore.getSoils().getAll().stream()
                .filter(AgriSoil::isEnabled)
                .map(soil -> AgriCraft.instance.proxy().jsonObjectFactory().createSoil(soil))
                .forEach(AgriApi.getSoilRegistry()::add);

        // Fire initiation completion
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Soil(LogicalSide.SERVER));

        // Display Soils
        AgriCore.getLogger("agricraft").info("Registered Soils ({0}/{1}):", count, raw);
        for (IAgriSoil soil : AgriApi.getSoilRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", soil.getId());
        }
    }

    private static void initPlants() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Plants!");

        // See if plants are valid...
        final int raw = AgriCore.getPlants().getAllElements().size();
        AgriCore.getPlants().validate();
        final int count = AgriCore.getPlants().getAllElements().size();

        // Transfer
        AgriCore.getPlants().validate();
        AgriCore.getPlants().getAllElements().stream()
                .filter(AgriPlant::isEnabled)
                .map(plant -> AgriCraft.instance.proxy().jsonObjectFactory().createPlant(plant))
                .forEach(AgriApi.getPlantRegistry()::add);

        // Fire initiation completion
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Plant(LogicalSide.SERVER));

        // Display Plants
        AgriCore.getLogger("agricraft").info("Registered Plants ({0}/{1}):", count, raw);
        for (IAgriPlant plant : AgriApi.getPlantRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", plant.getId());
        }
    }

    private static void initWeeds() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Weeds!");

        // See if plants are valid...
        final int raw = AgriCore.getWeeds().getAllElements().size();
        AgriCore.getWeeds().validate();
        final int count = AgriCore.getWeeds().getAllElements().size();

        // Transfer
        AgriCore.getWeeds().validate();
        AgriCore.getWeeds().getAllElements().stream()
                .filter(AgriWeed::isEnabled)
                .map(weed -> AgriCraft.instance.proxy().jsonObjectFactory().createWeed(weed))
                .forEach(AgriApi.getWeedRegistry()::add);

        // Fire initiation completion
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Weed(LogicalSide.SERVER));

        // Display Plants
        AgriCore.getLogger("agricraft").info("Registered Weeds ({0}/{1}):", count, raw);
        for (IAgriWeed weed : AgriApi.getWeedRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", weed.getId());
        }
    }


    private static void initMutations() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Mutations!");

        // See if mutations are valid...
        final int raw = AgriCore.getMutations().getAll().size();
        AgriCore.getMutations().validate();
        final int count = AgriCore.getMutations().getAll().size();

        // Transfer
        AgriCore.getMutations().getAll().stream()
                .filter(AgriMutation::isEnabled)
                .map(JsonHelper::wrap)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(AgriApi.getMutationRegistry()::add);

        // Fire initiation completion
        MinecraftForge.EVENT_BUS.post(new AgriRegistryEvent.Initialized.Mutation(LogicalSide.SERVER));

        // Display Mutations
        AgriCore.getLogger("agricraft").info("Registered Mutations ({0}/{1}):", count, raw);
        for (IAgriMutation mutation : AgriApi.getMutationRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", mutation);
        }
    }

    private static void initFertilizers() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Fertilizers!");

        // See if fertilizers are valid...
        final int raw = AgriCore.getFertilizers().getAll().size();
        AgriCore.getFertilizers().validate();
        final int count = AgriCore.getFertilizers().getAll().size();

        //Transfer and display fertilizers
        AgriCore.getLogger("agricraft").info("Registered Fertilizers ({0}/{1}):", count, raw);
        AgriCore.getFertilizers().getAll().stream()
                .filter(AgriFertilizer::isEnabled)
                .map(fertilizer -> AgriCraft.instance.proxy().jsonObjectFactory().createFertilizer(fertilizer))
                .forEach(fertilizer -> {
                    AgriApi.getFertilizerAdapterizer().registerAdapter(fertilizer);
                    AgriCore.getLogger("agricraft").info(" - {0}", fertilizer.getId());
                });
    }
}
