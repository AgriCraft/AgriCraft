package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.naming.OperationNotSupportedException;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
     * Fetches the AgriCraft Seed Registry.
     * <p>
     * Notice: This method will throw an {@link OperationNotSupportedException} if the corresponding
     * version of AgriCraft is not currently installed.
     *
     * @return the AgriCraft Seed Registry.
     */
    @Nonnull
    public static IAgriAdapterizer<AgriSeed> getSeedRegistry() {
        return AgriApi.CONNECTOR.connectSeedRegistry();
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
    public static IAgriAdapterizer<IAgriFertilizer> getFertilizerRegistry() {
        return AgriApi.CONNECTOR.connectFertilizerRegistry();
    }

    /**
     * Fetches an IAgriCrop instance from a position in the world
     *
     * @param world the World object
     * @param pos the BlockPos holding the coordinates
     * @return Optional containing an IAgriCrop object, or empty if the coordinates do not correspond with a crop
     */
    public static Optional<IAgriCrop> getCrop(World world, BlockPos pos) {
        return AgriApi.CONNECTOR.getCrop(world, pos);
    }

    /**
     * Fetches an IAgriCrop instance from a state at a position in the world (more efficient in case the BlockState is already known)
     *
     * @param state the BlockState at the given coordinates
     * @param world the World object
     * @param pos the BlockPos holding the coordinates
     * @return Optional containing an IAgriCrop object, or empty if the coordinates do not correspond with a crop
     */
    public static Optional<IAgriCrop> getCrop(BlockState state, World world, BlockPos pos) {
        return AgriApi.CONNECTOR.getCrop(state, world, pos);
    }

    /**
     * @return the IDefaultGrowConditionFactory which can be used to construct native AgriCraft IGrowConditions
     */
    public static IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
        return AgriApi.CONNECTOR.getDefaultGrowConditionFactory();
    }

    /**
     * @return The current IAgriMutationEngine object which controls the mutation logic of crops
     */
    public static IAgriMutationHandler getAgriMutationHandler() {
        return AgriApi.CONNECTOR.getAgriMutationHandler();
    }

    /**
     * @return A new IAgriGenome.Builder object to construct AgriCraft IAgriGenome objects
     */
    public static IAgriGenome.Builder getAgriGenomeBuilder() {
        return AgriApi.CONNECTOR.getAgriGenomeBuilder();
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
