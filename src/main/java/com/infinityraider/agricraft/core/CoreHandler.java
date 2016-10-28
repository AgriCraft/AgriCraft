package com.infinityraider.agricraft.core;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.apiimpl.MutationRegistry;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.SoilRegistry;
import com.infinityraider.agricraft.reference.Reference;

import com.agricraft.agricore.config.AgriConfigAdapter;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.json.AgriLoader;
import com.agricraft.agricore.registry.AgriPlants;
import com.agricraft.agricore.util.ResourceHelper;
import com.agricraft.agricore.util.TypeHelper;

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

    @EventHandler
    public static void preinit(FMLPreInitializationEvent event) {

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

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

        // Load the core!
        AgriCore.getLogger("AgriCraft").info("Attempting to load plants!");
        AgriLoader.loadDirectory(
                defaultDir,
                AgriCore.getSoils(),
                AgriCore.getPlants(),
                AgriCore.getMutations()
        );
        AgriCore.getLogger("AgriCraft").info("Finished trying to load plants!");

        // See if plants are valid...
        AgriCore.getCoreLogger().debug("Unvalidated Soils: {0}", AgriCore.getSoils().getAll().size());
        AgriCore.getSoils().validate();
        AgriCore.getCoreLogger().debug("Validated Soils: {0}", AgriCore.getSoils().getAll().size());

        // See if plants are valid...
        AgriCore.getCoreLogger().debug("Unvalidated Plants: {0}", AgriCore.getPlants().getAll().size());
        AgriCore.getPlants().validate();
        AgriCore.getCoreLogger().debug("Validated Plants: {0}", AgriCore.getPlants().getAll().size());

        // See if mutations are valid...
        AgriCore.getCoreLogger().debug("Unvalidated Mutations: {0}", AgriCore.getMutations().getAll().size());
        AgriCore.getMutations().validate();
        AgriCore.getCoreLogger().debug("Validated Mutations: {0}", AgriCore.getMutations().getAll().size());

        // Save settings!
        AgriCore.getConfig().save();

        // Load JSON Stuff
        initSoils();
        initPlants();
        initMutations();

    }

    public static void initSoils() {
        AgriCore.getLogger("AgriCraft").info("Registering Custom Soils!");
        AgriCore.getSoils().validate();
        AgriCore.getSoils().getAll().stream()
                .map(JsonSoil::new)
                .forEach(SoilRegistry.getInstance()::addSoil);
        AgriCore.getLogger("AgriCraft").info("Custom Soils registered!");
    }

    public static void initPlants() {
        AgriCore.getLogger("AgriCraft").info("Registering Custom Plants!");
        AgriCore.getPlants().validate();
        AgriCore.getPlants().getAll().stream()
                .map(JsonPlant::new)
                .forEach(PlantRegistry.getInstance()::addPlant);
        AgriCore.getLogger("AgriCraft").info("Custom Plants registered!");
    }

    public static void initMutations() {
        AgriCore.getMutations().getAll().stream()
                .map(JsonMutation::new)
                .forEach(MutationRegistry.getInstance()::addMutation);
        //print registered mutations to the log
        AgriCore.getLogger("AgriCraft").info("Registered Mutations:");
        for (IAgriMutation mutation : MutationRegistry.getInstance().getMutations()) {
            AgriCore.getLogger("AgriCraft").info(" - {0}", mutation);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void loadTextures(Consumer<ResourceLocation> register) {
        final AgriPlants preloaded = new AgriPlants();
        AgriLoader.loadDirectory(jsonDir, preloaded);
        preloaded.getAll().stream()
                .flatMap(p -> p.getTexture().getAllTextures().stream())
                .filter(TypeHelper::isNonNull)
                .map(ResourceLocation::new)
                .forEach(register);
    }

}
