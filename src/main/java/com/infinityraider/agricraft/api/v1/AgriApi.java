package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.naming.OperationNotSupportedException;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
     *
     * @return the current state of the AgriCraft API.
     */
    @Nonnull
    AgriApiState getState() {
        return AgriApi.CONNECTOR.getState();
    }

    /**
     * Fetches the AgriCraft IAgriConfig implementation, giving read access to the config options as defined by the end user.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft configuration
     */
    @Nonnull
    IAgriConfig getAgriConfig() {
        return AgriApi.CONNECTOR.connectAgriConfig();
    }

    /**
     * Fetches the AgriCraft Plant Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Plant Registry.
     */
    @Nonnull
    public static IAgriRegistry<IAgriPlant> getPlantRegistry() {
        return AgriApi.CONNECTOR.connectPlantRegistry();
    }

    /**
     * Fetches the AgriCraft Weed Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Weed Registry.
     */
    @Nonnull
    public static IAgriRegistry<IAgriWeed> getWeedRegistry() {
        return AgriApi.CONNECTOR.connectWeedRegistry();
    }

    /**
     * Fetches the AgriCraft Growth Stage Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Growth Stage Registry.
     */
    @Nonnull
    public static IAgriRegistry<IAgriGrowthStage> getGrowthStageRegistry() {
        return AgriApi.CONNECTOR.connectGrowthStageRegistry();
    }

    /**
     * Fetches the AgriCraft Mutation Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
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
     *
     * @return the AgriCraft Gene Registry.
     */
    @Nonnull
    public static IAgriGeneRegistry getGeneRegistry() {
        return AgriApi.CONNECTOR.connectGeneRegistry();
    }

    /**
     * Fetches the AgriCraft Soil Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Soil Registry.
     */
    @Nonnull
    public static IAgriSoilRegistry getSoilRegistry() {
        return AgriApi.CONNECTOR.connectSoilRegistry();
    }

    /**
     * Fetches the AgriCraft Stat Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Stat Registry.
     */
    @Nonnull
    public static IAgriStatRegistry getStatRegistry() {
        return AgriApi.CONNECTOR.connectStatRegistry();
    }

    /**
     * Fetches the AgriCraft Seed Adapterizer.
     *
     * Seeds are ItemStacks which can be planted on crops, and carry genes.
     * Each plant corresponds to exactly one seed
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Seed Registry.
     */
    @Nonnull
    public static IAgriAdapterizer<AgriSeed> getSeedAdapterizer() {
        return AgriApi.CONNECTOR.connectSeedAdapterizer();
    }

    /**
     * Fetches the AgriCraft Fertilizer Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Fertilizer Registry.
     */
    @Nonnull
    public static IAgriAdapterizer<IAgriFertilizer> getFertilizerAdapterizer() {
        return AgriApi.CONNECTOR.connectFertilizerRegistry();
    }

    @Nonnull
    public static ItemStack seedToStack(AgriSeed seed, int amount) {
        return AgriApi.CONNECTOR.seedToStack(seed, amount);
    }

    @Nonnull
    public static ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        return AgriApi.CONNECTOR.plantToSeedStack(plant, amount);
    }

    /**
     * Fetches an IAgriCrop instance from a position in the world
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
     *
     * @param stages the number of required stages (must be greater than 0)
     * @return non-null, ordered list containing the requested amount of growth stages
     */
    @Nonnull
    public static List<IAgriGrowthStage> getDefaultGrowthStages(int stages) {
        return AgriApi.CONNECTOR.getDefaultGrowthStages(stages);
    }

    /**
     * @return The current IAgriMutationEngine object which controls the mutation logic of crops
     */
    @Nonnull
    public static IAgriMutationHandler getAgriMutationHandler() {
        return AgriApi.CONNECTOR.getAgriMutationHandler();
    }

    /**
     * @param plant the plant for which to construct a new genome
     * @return A new IAgriGenome.Builder object to construct AgriCraft IAgriGenome objects
     */
    @Nonnull
    public static IAgriGenome.Builder getAgriGenomeBuilder(@Nonnull IAgriPlant plant) {
        return AgriApi.CONNECTOR.getAgriGenomeBuilder(plant);
    }

    /**
     * @return AgriCraft's internal quad generator for plant rendering
     */
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static IAgriPlantQuadGenerator getPlantQuadGenerator() {
        return AgriApi.CONNECTOR.getPlantQuadGenerator();
    }

    /**
     * Finds a registered json plant callback from their id
     * @param id the id
     * @return optional containing the callback, or empty if no such callback is registered
     */
    @Nonnull
    public static Optional<IJsonPlantCallback> getJsonPlantCallback(String id) {
        return AgriApi.CONNECTOR.getJsonPlantCallback(id);
    }

    /**
     * Tries to register a json plant callback behaviour
     * @param callback the callback to register
     * @return true if successful (will fail in case a callback with the same id is already registered)
     */
    public static boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback callback) {
        return AgriApi.CONNECTOR.registerJsonPlantCallback(callback);
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
