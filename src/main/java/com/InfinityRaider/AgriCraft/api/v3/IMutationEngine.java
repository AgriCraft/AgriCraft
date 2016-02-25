package com.InfinityRaider.AgriCraft.api.v3;

import java.util.Random;

/**
 * This interface is used to bridge the gap between the ICrop object and the IMutationLogic class,
 * You do not have to implement this interface, it is passed to your IMutationLogic class when needed.
 * It contains a pointer to the relevant ICrop object, a Random object, the IMutationHandler instance and the IStatCalculator object.
 */
public interface IMutationEngine {
    /**
     * @return the ICrop object for which a mutation has been triggered, the mutation can be a spread from neighbouring crops or an actual mutation.
     */
    ICrop getCrop();

    /**
     * @return a crop specific Random object
     */
    Random getRandom();

    /**
     * @return the IMutationHandler instance
     */
    IMutationHandler getMutationHandler();

    /**
     * This method returns the currently used IStatCalculator object by agricraft,
     * it is recommended to use this to calculate the new seed stats after a mutation.
     * This can either be the default agricraft stat logic, or stat logic registered via the API.
     * If you want to do your own stat logic as well, register a new IStatCalculator object via APIv3.setStatCalculator(IStatCalculator calculator)
     *
     * It is implemented this way to give you the freedom to either follow this recommendation or not.
     * If you want to allow other addons to override your stat logic, while still using your mutation logic, use this.
     * If you want to have your own stat calculation logic without the possibility of others meddling with it, ignore this and do stat calculation internally.
     *
     * @return the IStatCalculator instance
     */
    IStatCalculator getStatCalculator();
}
