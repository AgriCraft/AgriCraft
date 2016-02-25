package com.InfinityRaider.AgriCraft.api.v3;

/**
 * This interface can be used to alter or override the mutation logic for mutations within Agricraft.
 * Only one IMutationLogic instance can be active at each time.
 *
 * Use IMutationHandler.setMutationLogic(IMutationLogic logic) to set the currently used logic
 * Use IMutationHandler.getMutationLogic() to get the currently used logic
 */
public interface IMutationLogic {
    /**
     * This method is called when a spread action happens (spreads are mainly used to increase the stats on crops).
     *
     * @param engine the IMutationEngine object for the crop for which a spreading has been triggered
     * @return an ICrossOverResult object which will be applied to the crop
     */
    ICrossOverResult getSpreadingResult(IMutationEngine engine);

    /**
     * This method is called when a mutation happens (mutations are mainly used to mutate two crops into a new type of crop).
     *
     * @param engine the IMutationEngine object for the crop for which a mutation has been triggered
     * @return an ICrossOverResult object which will be applied to the crop
     */
    ICrossOverResult getMutationResult(IMutationEngine engine);
}
