package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

/**
 * Central class which controls the mutation logic of AgriCraft, which is controlled by three sub-classes:
 * - The first is the IAgriMutationEngine, which handles the spreading / mutation logic between crops in the world
 * - The other two are IMutator objects for the plant species and stats respectively
 *
 * Any of these implementations can be obtained, replaced, overridden, etc... via the accessors provided in this class
 * The implementation of this class can be obtained via AgriApi.getAgriMutationHandler()
 */
public interface IAgriMutationHandler {
    /**
     * Sets the active IAgriMutationEngine object to be used by AgriCraft for cloning and mutating of plants;
     * @param engine the IAgriMutationEngine object
     * @return this
     */
    IAgriMutationHandler setActiveMutationEngine(IAgriMutationEngine engine);

    /**
     * @return the currently active IAgriMutationEngine object
     */
    IAgriMutationEngine getActiveMutationEngine();

    /**
     * @return AgriCraft's default IAgriMutationEngine object
     */
    IAgriMutationEngine getDefaultMutationEngine();

    /**
     * Sets the active IMutator<IAgriPlant> object to be used by AgriCraft for mutation of plant species genes;
     * @param mutator the IMutator<IAgriPlant> object
     * @return this
     */
    IAgriMutationHandler setActivePlantMutator(IMutator<IAgriPlant> mutator);

    /**
     * @return the currently active IMutator<IAgriPlant> object
     */
    IMutator<IAgriPlant> getActivePlantMutator();

    /**
     * @return AgriCraft's default IMutator<IAgriPlant> object
     */
    IMutator<IAgriPlant> getDefaultPlantMutator();

    /**
     * Sets the active IMutator<Byte> object to be used by AgriCraft for mutation of stats genes;
     * @param mutator the IMutator<Byte> object
     * @return this
     */
    IAgriMutationHandler setActiveStatMutator(IMutator<Byte> mutator);

    /**
     * @return the currently active IMutator<Byte> object
     */
    IMutator<Byte> getActiveStatMutator();

    /**
     * @return AgriCraft's default IMutator<Byte> object
     */
    IMutator<Byte> getDefaultStatMutator();
}
