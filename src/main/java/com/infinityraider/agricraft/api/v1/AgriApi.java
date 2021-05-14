package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.naming.OperationNotSupportedException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.*;

/**
 * The AgriCraft APIv1.
 *
 * @since 2.0.0
 */
public final class AgriApi {

    public static final String MOD_ID = "agricraft";

    public static final String API_ID = "agricraft-api-v1";

    private static final IAgriApiConnector CONNECTOR = connect();

    /**
     * Determines the current state of the AgriCraft API.
     * <p>
     * Notice, unlike the other API methods, this one is always safe to use, and will not throw an
     * exception when AgriCraft is not installed, opting instead to return
     * {@link AgriApiState#INVALID}.
     * </p>
     *
     * @return the current state of the AgriCraft API.
     */
    @Nonnull
    public static AgriApiState getState() {
        return AgriApi.CONNECTOR.getState();
    }

    /**
     * Fetches the AgriCraft IAgriConfig implementation, giving read access to the config options as defined by the end user.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft configuration
     */
    @Nonnull
    public static IAgriConfig getAgriConfig() {
        return AgriApi.CONNECTOR.connectAgriConfig();
    }

    /**
     * Fetches the AgriCraft Plant Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Plant Registry.
     */
    @Nonnull
    public static IAgriPlantRegistry getPlantRegistry() {
        return AgriApi.CONNECTOR.connectPlantRegistry();
    }

    /**
     * Fetches the AgriCraft Weed Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Weed Registry.
     */
    @Nonnull
    public static IAgriWeedRegistry getWeedRegistry() {
        return AgriApi.CONNECTOR.connectWeedRegistry();
    }

    /**
     * Fetches the AgriCraft Growth Stage Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Growth Stage Registry.
     */
    @Nonnull
    public static IAgriGrowthRegistry getGrowthStageRegistry() {
        return AgriApi.CONNECTOR.connectGrowthStageRegistry();
    }

    /**
     * Fetches the AgriCraft Mutation Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Mutation Registry.
     */
    @Nonnull
    public static IAgriMutationRegistry getMutationRegistry() {
        return AgriApi.CONNECTOR.connectMutationRegistry();
    }

    /**
     * Fetches the AgriCraft Gene Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Gene Registry.
     */
    @Nonnull
    public static IAgriGeneRegistry getGeneRegistry() {
        return AgriApi.CONNECTOR.connectGeneRegistry();
    }

    /**
     * Fetches the AgriCraft Stat Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Stat Registry.
     */
    @Nonnull
    public static IAgriStatRegistry getStatRegistry() {
        return AgriApi.CONNECTOR.connectStatRegistry();
    }

    /**
     * Fetches the AgriCraft Soil Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Soil Registry.
     */
    @Nonnull
    public static IAgriSoilRegistry getSoilRegistry() {
        return AgriApi.CONNECTOR.connectSoilRegistry();
    }

    /**
     * Fetches the AgriCraft Genome Adapterizer.
     *
     * Genomes are ItemStacks which can be planted on crops, and carry genes.
     * Each plant corresponds to exactly one seed
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Genome Adapterizer.
     */
    @Nonnull
    public static IAgriAdapterizer<IAgriGenome> getGenomeAdapterizer() {
        return AgriApi.CONNECTOR.connectGenomeAdapterizer();
    }

    /**
     * Fetches the AgriCraft Fertilizer Adapterizer.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft Fertilizer Adapterizer.
     */
    @Nonnull
    public static IAgriAdapterizer<IAgriFertilizer> getFertilizerAdapterizer() {
        return AgriApi.CONNECTOR.connectFertilizerRegistry();
    }

    /**
     * Fetches the AgriCraft Season Logic manager
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the AgriCraft season logic instance
     */
    @Nonnull
    public static IAgriSeasonLogic getSeasonLogic() {
        return AgriApi.CONNECTOR.connectSeasonLogic();
    }

    /**
     * Converts an IAgriPlant instance to an ItemStack
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param plant the plant
     * @param amount the desired stack size
     * @return ItemStack holding the seed for the plant (with default genome)
     */
    @Nonnull
    public static ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        return AgriApi.CONNECTOR.plantToSeedStack(plant, amount);
    }

    /**
     * Fetches the AgriSeedIngredient serializer
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the serializer
     */
    @Nonnull
    public static IIngredientSerializer<AgriPlantIngredient> getPlantIngredientSerializer() {
        return AgriApi.CONNECTOR.connectPlantIngredientSerializer();
    }

    /**
     * Fetches an IAgriCrop instance from a position in the world
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param world the World object
     * @param pos the BlockPos holding the coordinates
     * @return Optional containing an IAgriCrop object, or empty if the coordinates do not correspond with a crop
     */
    @Nonnull
    public static Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        return AgriApi.CONNECTOR.getCrop(world, pos);
    }

    /**
     * Fetches an IAgriSoil instance from a position in the world
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param world the World object
     * @param pos the BlockPos holding the coordinates
     * @return Optional containing an IAgriSoil object, or empty if the coordinates do not correspond with a soil
     */
    @Nonnull
    public static Optional<IAgriSoil> getSoil(IBlockReader world, BlockPos pos) {
        return AgriApi.CONNECTOR.getSoil(world, pos);
    }

    /**
     * Fetches the IFluidHandler instance for an IAgriIrrigationComponent
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param component the irrigation component
     * @return the fluid handler to push and pull water in / out the irrigation system from that component
     */
    @Nonnull
    public static IFluidHandler getIrrigationComponentFluidHandler(IAgriIrrigationComponent component) {
        return AgriApi.CONNECTOR.getIrrigationComponentFluidHandler(component);
    }

    /**
     * Makes the item ignore the vanilla planting override rule.
     * The Vanilla planting override rule converts Agricraft-compatible crops to Agricraft crops when planted
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param seed the seed
     */
    public static void registerVanillaPlantingOverrideException(Item seed) {
        AgriApi.CONNECTOR.registerVanillaPlantingOverrideException(seed);
    }

    /**
     * Registers a capability instance which AgriCraft will attach automatically to the respective tile entities
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param instance the instance
     * @param <T> the exact type of the TileEntity to attach to
     * @param <C> the parent type of the IAgriCrop being attached
     */
    public static <T extends TileEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance) {
        AgriApi.CONNECTOR.registerCapabilityCropInstance(instance);
    }

    /**
     * Creates a new the AgriCraft growth requirement builder
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return a new IAgriGrowthRequirement.Builder object to instantiate a IAgriGrowthRequirement
     */
    @Nonnull
    public static IAgriGrowthRequirement.Builder getGrowthRequirementBuilder() {
        return AgriApi.CONNECTOR.getGrowthRequirementBuilder();
    }

    /**
     * Fetches the AgriCraft grow condition factory
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return the IDefaultGrowConditionFactory which can be used to construct native AgriCraft IGrowConditions
     */
    @Nonnull
    public static IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
        return AgriApi.CONNECTOR.getDefaultGrowConditionFactory();
    }

    /**
     * Fetches an ordered list containing the predefined amount of stages, following the default, internal AgriCraft growth scheme.
     * This scheme starts with the first stage in the list (at index 0), which returns the next item in the list from IAgriGrowthStage.getNextStage(),
     * except for the last stage which returns itself.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param stages the number of required stages (must be greater than 0)
     * @return non-null, ordered list containing the requested amount of growth stages
     */
    @Nonnull
    public static List<IAgriGrowthStage> getDefaultGrowthStages(int stages) {
        return AgriApi.CONNECTOR.getDefaultGrowthStages(stages);
    }

    /**
     * Fetches the AgriCraft mutation handler
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return The current IAgriMutationHandler object which controls the mutation logic of crops
     */
    @Nonnull
    public static IAgriMutationHandler getAgriMutationHandler() {
        return AgriApi.CONNECTOR.getAgriMutationHandler();
    }

    /**
     * Creates a new genome builder object
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param plant the plant for which to construct a new genome
     * @return A new IAgriGenome.Builder object to construct AgriCraft IAgriGenome objects
     */
    @Nonnull
    public static IAgriGenome.Builder getAgriGenomeBuilder(@Nonnull IAgriPlant plant) {
        return AgriApi.CONNECTOR.getAgriGenomeBuilder(plant);
    }

    /**
     * Fetches the AgriCraft plant quad generator
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @return AgriCraft's internal quad generator for plant rendering
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static IAgriPlantQuadGenerator getPlantQuadGenerator() {
        return AgriApi.CONNECTOR.getPlantQuadGenerator();
    }

    /**
     * Finds a registered json plant callback from their id
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param id the id
     * @return optional containing the callback, or empty if no such callback is registered
     */
    @Nonnull
    public static Optional<IJsonPlantCallback> getJsonPlantCallback(String id) {
        return AgriApi.CONNECTOR.getJsonPlantCallback(id);
    }

    /**
     * Tries to register a json plant callback behaviour
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param callback the callback to register
     * @return true if successful (will fail in case a callback with the same id is already registered)
     */
    public static boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback callback) {
        return AgriApi.CONNECTOR.registerJsonPlantCallback(callback);
    }

    /**
     * Checks if a player is looking through a magnifying glass
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     * </p>
     *
     * @param player the player
     * @return true if the player is currently looking through the magnifying glass
     */
    public static boolean isObservingWithMagnifyingGlass(PlayerEntity player) {
        return AgriApi.CONNECTOR.isObservingWithMagnifyingGlass(player);
    }

    /**
     * Establishes a connection to AgriCraft, if it is present, or returns a fake connection to
     * nothing.
     *
     * @return a connection to AgriCraft, or a connection to nothing.
     */
    @Nonnull
    private static IAgriApiConnector connect() {
        // Step I. Setup Variables.
        final Class<? extends IAgriApiConnector> clazz;
        final Constructor<? extends IAgriApiConnector> constructor;
        final IAgriApiConnector instance;
        final Logger logger = LogManager.getLogger();
        final Marker marker = MarkerManager.getMarker(API_ID);

        // Step II. Attempt To Find Class
        try {
            clazz = Class.forName("com.infinityraider.agricraft.impl.v1.AgriApiConnector").asSubclass(IAgriApiConnector.class);
        } catch (ClassNotFoundException exception) {
            logger.log(Level.INFO, marker, "The AgriCraft APIv1 was unable find AgriCraft! Is AgriCraft missing from your modpack?");
            return IAgriApiConnector.FAKE;
        } catch (ClassCastException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, but instead found an invalid class! This is a serious error that should never happen! Report this error immediately!", exception);
        }

        // Step III. Attempt To Find Constructor
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, found a connection class, but couldn't find a valid no-args constructor! This is a serious error that should never happen! Report this error immediately!", exception);
        } catch (SecurityException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, but instead ran into a security exception! This is a very unusual error that should not have happened! Report this error immediately!", exception);
        }

        // Step IV. Attempt To Instantiate Constructor
        try {
            instance = constructor.newInstance();
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, but instead was prevented from accessing the constructor required to create a connection! This is a very unusual error that should not have happened! Report this error immediately!", exception);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, but instead discovered something is wrong with the JVM! This error should never occur! Report this error immediately to Oracle!", exception);
        } catch (InstantiationException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, found a connection class, but it was abstract! This is a serious error that should never happen! Report this error immediately!", exception);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("The AgriCraft APIv1 attempted to connect to AgriCraft, found a valid connection class, started instantiation, but then the AgriApi.connector threw an error! This is a serious error that should never happen! Report this error immediately!", exception);
        }

        // Step V. Celebrate Connection Success
        logger.log(Level.INFO, marker, "The AgriCraft APIv1 successfully connected to AgriCraft! Thank you for including AgriCraft in your modpack!");

        // Step VI. Return The Connector
        return instance;
    }

    /**
     * A private constructor to prevent instantiation of this class.
     */
    private AgriApi() {
    }

}
