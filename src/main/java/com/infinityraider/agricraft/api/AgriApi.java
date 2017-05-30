/*
 */
package com.infinityraider.agricraft.api;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import com.infinityraider.agricraft.api.util.FakeApiConnector;
import com.infinityraider.agricraft.api.util.LazyFinal;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * The AgriCraft API.
 *
 * @since 2.0.0
 */
public final class AgriApi {

    public static final String MOD_ID = "agricraft";

    public static final String API_ID = "agricraft-api";

    private static final LazyFinal<IAgriApiConnector> connector = new LazyFinal<>(AgriApi::connect);

    @Nonnull
    public static Optional<IAgriRegistry<IAgriPlant>> PlantRegistry() {
        return connector.get().connectPlantRegistry();
    }

    @Nonnull
    public static Optional<IAgriMutationRegistry> MutationRegistry() {
        return connector.get().connectMutationRegistry();
    }

    @Nonnull
    public static Optional<IAgriSoilRegistry> SoilRegistry() {
        return connector.get().connectSoilRegistry();
    }

    @Nonnull
    public static Optional<IAgriAdapterizer<IAgriStat>> StatRegistry() {
        return connector.get().connectStatRegistry();
    }

    @Nonnull
    public static Optional<IAgriAdapterizer<IAgriStatCalculator>> StatCalculatorRegistry() {
        return connector.get().connectStatCalculatorRegistry();
    }

    @Nonnull
    public static Optional<IAgriMutationEngine> MutationEngine() {
        return connector.get().connectMutationEngine();
    }

    @Nonnull
    public static Optional<IAgriAdapterizer<AgriSeed>> SeedRegistry() {
        return connector.get().connectSeedRegistry();
    }

    @Nonnull
    public static Optional<IAgriAdapterizer<IAgriFertilizer>> FertilizerRegistry() {
        return connector.get().connectFertilizerRegistry();
    }

    @Nonnull
    private static IAgriApiConnector connect() {
        // Step I. Setup Variables.
        final Class<? extends IAgriApiConnector> clazz;
        final Constructor<? extends IAgriApiConnector> constructor;
        final IAgriApiConnector instance;

        // Step II. Attempt To Find Class
        try {
            clazz = Class.forName("com.infinityraider.agricraft.apiimpl.AgriApiConnector").asSubclass(IAgriApiConnector.class);
        } catch (ClassNotFoundException exception) {
            FMLLog.log(API_ID, Level.INFO, "The AgriCraft API was unable find AgriCraft! Is AgriCraft missing from your modpack?");
            return FakeApiConnector.INSTANCE;
        } catch (ClassCastException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, but instead found an invalid class! This is a serious error that should never happen! Report this error immediately!", exception);
        }

        // Step III. Attempt To Find Constructor
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, found a connection class, but couldn't find a valid no-args constructor! This is a serious error that should never happen! Report this error immediately!", exception);
        } catch (SecurityException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, but instead ran into a security exception! This is a very unusual error that should not have happened! Report this error immediately!", exception);
        }

        // Step IV. Attempt To Instantiate Constructor
        try {
            instance = constructor.newInstance();
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, but instead was prevented from accessing the constructor required to create a connection! This is a very unusual error that should not have happened! Report this error immediately!", exception);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, but instead discovered something is wrong with the JVM! This error should never occur! Report this error immediately to Oracle!", exception);
        } catch (InstantiationException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, found a connection class, but it was abstract! This is a serious error that should never happen! Report this error immediately!", exception);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("The AgriCraft API attempted to connect to AgriCraft, found a valid connection class, started instantiation, but then the connector threw an error! This is a serious error that should never happen! Report this error immediately!", exception);
        }

        // Step V. Celebrate Connection Success
        FMLLog.log(API_ID, Level.INFO, "The AgriCraft API successfully connected to AgriCraft! Thank you for including AgriCraft in your modpack!");

        // Step VI. Return The Connector
        return instance;
    }

    private AgriApi() {
    }

}
