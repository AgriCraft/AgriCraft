package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriLoader;
import com.agricraft.agricore.plant.*;
import com.agricraft.agricore.util.ResourceHelper;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.impl.v1.fertilizer.JsonFertilizer;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlant;
import com.infinityraider.agricraft.impl.v1.plant.JsonWeed;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;
import com.infinityraider.agricraft.reference.Reference;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public final class CoreHandler {
    public static final Pattern JSON_FILE_PATTERN = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
    public static final Pattern AGRI_FOLDER_PATTERN = Pattern.compile("json/defaults/.*", Pattern.CASE_INSENSITIVE);
    public static final Predicate<String> MOD_FILTER = (path) -> {
        String id = path.substring(path.indexOf("json/defaults/") + 14);
        id = id.substring(0, id.indexOf("/"));
        if(id.equalsIgnoreCase("vanilla")) {
            return true;
        }
        if(id.equalsIgnoreCase("resource")) {
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

        // Transfer Defaults
        if(AgriCraft.instance.getConfig().generateMissingDefaultJsons()) {
            ResourceHelper.copyResources(
                    ModList.get().getModFiles().stream().map(ModFileInfo::getFile).map(ModFile::getFilePath),
                    JSON_FILE_PATTERN.asPredicate(),
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
                .map(JsonSoil::new)
                .forEach(AgriApi.getSoilRegistry()::add);

        // Display Soils
        AgriCore.getLogger("agricraft").info("Registered Soils ({0}/{1}):", count, raw);
        for (IAgriSoil soil : AgriApi.getSoilRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", soil.getName());
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
                .map(JsonPlant::new)
                .forEach(AgriApi.getPlantRegistry()::add);

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
                .map(JsonWeed::new)
                .forEach(AgriApi.getWeedRegistry()::add);

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

        // Display Mutations
        AgriCore.getLogger("agricraft").info("Registered Mutations ({0}/{1}):", count, raw);
        for (IAgriMutation mutation : AgriApi.getMutationRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", mutation);
        }
    }

    private static void initFertilizers() {
        // Announce Progress
        AgriCore.getLogger("agricraft").info("Registering Fertilizers!");

        // See if soils are valid...
        final int raw = AgriCore.getFertilizers().getAll().size();
        AgriCore.getFertilizers().validate();
        final int count = AgriCore.getFertilizers().getAll().size();

        // Transfer
        AgriCore.getFertilizers().getAll().stream()
                .filter(AgriFertilizer::isEnabled)
                .map(JsonFertilizer::new)
                .forEach(AgriApi.getFertilizerRegistry()::add);

        // Display Soils
        AgriCore.getLogger("agricraft").info("Registered Fertilizers ({0}/{1}):", count, raw);
        for (IAgriFertilizer fertilizer : AgriApi.getFertilizerRegistry().all()) {
            AgriCore.getLogger("agricraft").info(" - {0}", fertilizer.getName());
        }
    }
}
