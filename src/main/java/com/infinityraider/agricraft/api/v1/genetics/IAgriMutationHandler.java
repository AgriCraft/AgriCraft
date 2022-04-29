package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

/**
 * Central class which controls the mutation logic of AgriCraft, which is controlled by three sub-classes:
 * - The first is the IAgriCrossBreedEngine, which handles the spreading / cross breeding logic between crops in the world
 * - The other two are IMutator objects for the plant species and stats respectively
 *
 * Any of these implementations can be obtained, replaced, overridden, etc... via the accessors provided in this class
 * The implementation of this class can be obtained via AgriApi.getAgriMutationHandler()
 */
public interface IAgriMutationHandler {
    /**
     * @return the AgriCraft IAgriMutationHandler instance
     */
    @SuppressWarnings("unused")
    static IAgriMutationHandler getInstance() {
        return AgriApi.getAgriMutationHandler();
    }

    /**
     * Sets the active IAgriCrossBreedEngine object to be used by AgriCraft for cloning and mutating of plants;
     * @param engine the IAgriCrossBreedEngine object
     * @return this
     */
    IAgriMutationHandler setActiveCrossBreedEngine(IAgriCrossBreedEngine engine);

    /**
     * @return the currently active IAgriCrossBreedEngine object
     */
    IAgriCrossBreedEngine getActiveCrossBreedEngine();

    /**
     * @return AgriCraft's default IAgriCrossBreedEngine object
     */
    IAgriCrossBreedEngine getDefaultCrossBreedEngine();

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
    IAgriMutationHandler setActiveStatMutator(IMutator<Integer> mutator);

    /**
     * @return the currently active IMutator<Byte> object
     */
    IMutator<Integer> getActiveStatMutator();

    /**
     * @return AgriCraft's default IMutator<Byte> object
     */
    IMutator<Integer> getDefaultStatMutator();
}
