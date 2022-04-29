package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.util.Tuple;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * AgriCraft's cross breeding logic is executed by the implementation of this interface.
 *
 * It's active / default implementations can be obtained from the IAgriCrossBreedEngine instance
 * Overriding implementations can be activated with the IAgriCrossBreedEngine instance as well
 */
public interface IAgriCrossBreedEngine {
    /**
     * @return the active AgriCraft IAgriCrossBreedEngine instance
     */
    @SuppressWarnings("unused")
    static IAgriCrossBreedEngine getActiveInstance() {
        return AgriApi.getAgriMutationHandler().getActiveCrossBreedEngine();
    }

    /**
     * @return the default AgriCraft IAgriCrossBreedEngine instance
     */
    @SuppressWarnings("unused")
    static IAgriCrossBreedEngine getDefaultInstance() {
        return AgriApi.getAgriMutationHandler().getDefaultCrossBreedEngine();
    }

    /**
     * Handles a growth tick resulting in cross breeding, is only fired for cross crops.
     * Any results from the success or failure of cross breeding, or clone, such as setting of the plant and genome must be fired from within this method as well.
     *
     * The default IAgriCrossBreedEngine allows its behaviour to be overridden:
     *  - The selector represented by IParentSelector filters out valid crops which can contribute to cross breeding / cloning, and rolls for their probability to contribute
     *  - The clone logic represented by ICloneLogic handles the cloning of a genome (offspring from one single parent)
     *  - The combine logic represented by ICombineLogic handles the combining of two parent genomes (offspring from two different parents)
     *
     * @param crop the crop for which the cross breeding tick has been fired
     * @param neighbours A stream of the crop's neighbouring crops (this includes all crops, regardless if these contain plants, weeds, are fertile, or mature)
     * @param random pseudo-random generator to take decisions
     * @return true if the cross breeding / spread succeeded, false if it failed
     */
    boolean handleCrossBreedTick(IAgriCrop crop, Stream<IAgriCrop> neighbours, Random random);

    /**
     * Sets the selection logic to be used by the cross breeding engine
     *
     * It is not obligatory to use this logic, in case you do not want your selection logic to be replaced.
     * The default AgriCraft cross breeding engine does allow replacing of its selection logic
     *
     * This method should always be safe to call
     *
     * @param selector the new selection logic
     * @return this
     */
    IAgriCrossBreedEngine setSelectionLogic(@Nonnull IParentSelector selector);

    /**
     * Sets the cloning logic to be used by the cross breeding engine
     *
     * It is not obligatory to use this logic, in case you do not want your cloning logic to be replaced.
     * The default AgriCraft cross breeding engine does allow replacing of its cloning logic
     *
     * This method should always be safe to call
     *
     * @param cloneLogic the new cloning logic
     * @return this
     */
    IAgriCrossBreedEngine setCloneLogic(@Nonnull ICloneLogic cloneLogic);

    /**
     * Sets the combining logic to be used by the cross breeding engine
     *
     * It is not obligatory to use this logic, in case you do not want your combining logic to be replaced.
     * The default AgriCraft cross breeding engine does allow replacing of its combining logic
     *
     * This method should always be safe to call
     *
     * @param combineLogic the new combining logic
     * @return this
     */
    IAgriCrossBreedEngine setCombineLogic(@Nonnull ICombineLogic combineLogic);

    /**
     * Functional interface for selection logic
     */
    @FunctionalInterface
    interface IParentSelector {
        /**
         * Selects potential parent candidates from all neighbouring crops
         *
         * In case an empty list is returned, nothing will happen.
         * In case a list with 1 crop is returned, a cloning event will be attempted
         * In case a list with 2 or more crops is returned, a combining event will be attempted with the first two items in the list
         *
         * @param candidates all candidates
         * @param random pseudo-random generator for decision making
         * @return ordered list of remaining candidates
         */
        @Nonnull
        List<IAgriCrop> selectAndOrder(@Nonnull Stream<IAgriCrop> candidates, @Nonnull Random random);
    }

    /**
     * Functional interface for cloning logic
     */
    @FunctionalInterface
    interface ICloneLogic {
        /**
         * Clones the parent genome to a new genome.
         *
         * Note that in the default implementation this will be an exact copy.
         * This, however, need not forcibly be the case, one could implement cloning logic which deteriorates the genes for instance
         *
         * @param crop the crop onto which the plant is cloned
         * @param parent the parent genome
         * @param random pseudo-random generator for decision making
         * @return the child genome
         */
        @Nonnull
        IAgriGenome clone(@Nonnull IAgriCrop crop, @Nonnull IAgriGenome parent, @Nonnull Random random);
    }

    /**
     * Functional interface for combining logic
     */
    @FunctionalInterface
    interface ICombineLogic {
        /**
         * Combines the genomes of two parents into a child genome
         *
         * @param crop the crop onto which the plant is cross bred
         * @param parents a tuple holding the genomes of the parents
         * @param random pseudo-random generator for decision making
         * @return the child genome
         */
        @Nonnull
        IAgriGenome combine(@Nonnull IAgriCrop crop,@Nonnull Tuple<IAgriGenome, IAgriGenome> parents, @Nonnull Random random);
    }
}
