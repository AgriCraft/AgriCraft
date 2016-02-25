package com.InfinityRaider.AgriCraft.api.v3;

import com.InfinityRaider.AgriCraft.api.v1.IMutation;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * The MutationHandler is a centralized class within agricraft which handles everything related to mutations, it has only one instance.
 * This instance can be retrieved via APIv3.getMutationHandler().
 *
 * IMPORTANT:
 * The IMutationHandler instance is initialized in onServerAboutToStartEvent() on the server, and its data is sent to clients connecting to it.
 */
public interface IMutationHandler {
    /**
     * This method constructs an array of IMutations containing possible crossovers for the list of ICrop objects passed to it.
     * The argument may be empty but can not be null.
     * @param crops a list of ICrop objects.
     * @return an array with the possible mutation results for this list, can be length 0, but will never be null.
     */
    IMutation[] getCrossOvers(List<? extends ICrop> crops);

    /**
     * @return an array containing all currently registered mutations, may be length 0, but will never be null.
     */
    IMutation[] getMutations();

    /**
     * Gets an array of IMutations which all have the argument as a parent
     * @param stack the parent for a mutation
     * @return an array containing all registered mutations with the argument as parent, may be length 0, but will never be null.
     */
    IMutation[] getMutationsFromParent(ItemStack stack);

    /**
     * Gets an array of IMutations which all have the argument as the result
     * @param stack the result of a mutation
     * @return an array containing all registered mutations with the argument as result, may be length 0, but will never be null.
     */
    IMutation[] getMutationsFromChild(ItemStack stack);

    /**
     * This method removes registered mutations which have the argument as result
     * @param result the resulting stack to remove all mutations for
     * @return A list with the removed mutations
     */
    List<IMutation> removeMutationsByResult(ItemStack result);

    /**
     * Registers a new mutation
     * @param mutation the new mutation to register
     */
    void add(IMutation mutation);

    /**
     * Removes a currently registered mutation
     * @param mutation the mutation to remove
     */
    void remove(IMutation mutation);

    /**
     * Registers a collection of mutations
     * @param mutationsToAdd the mutations to register
     */
    void addAll(Collection<? extends IMutation> mutationsToAdd);

    /**
     * Creates a new IMutation instance which has the default Agricraft implementation.
     * This is the recommended approach to create new IMutation objects.
     * @param result the resulting stack
     * @param parent1 the first parent stack
     * @param parent2 the second parent stack
     * @return a new IMutation object
     */
    IMutation createNewMutation(ItemStack result, ItemStack parent1, ItemStack parent2);

    /**
     * Creates a new IMutation instance which has the default Agricraft implementation.
     * This is the recommended approach to create new IMutation objects.
     * @param result the resulting stack
     * @param parent1 the first parent stack
     * @param parent2 the second parent stack
     * @param chance the chance for the mutation to occur
     * @return a new IMutation object
     */
    IMutation createNewMutation(ItemStack result, ItemStack parent1, ItemStack parent2, double chance);

    /**
     * Sets the currently used IMutationLogic object to the argument.
     * This will effectively change the behaviour of Agricraft mutations to the logic within the passed object.
     * @param logic an IMutationLogic object
     */
    void setMutationLogic(IMutationLogic logic);

    /**
     * @return the currently used IMutationLogic object
     */
    IMutationLogic getMutationLogic();
}
