package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v3.ICrossOverResult;
import com.InfinityRaider.AgriCraft.api.v3.IMutationEngine;
import com.InfinityRaider.AgriCraft.api.v3.IMutationLogic;

public class MutationLogicAgriCraft implements IMutationLogic {
    private static final MutationLogicAgriCraft INSTANCE = new MutationLogicAgriCraft();

    public static MutationLogicAgriCraft getInstance() {
        return INSTANCE;
    }

    private MutationLogicAgriCraft() {}

    @Override
    public ICrossOverResult getSpreadingResult(IMutationEngine engine) {
        return (new SpreadStrategy(engine)).executeStrategy();
    }

    @Override
    public ICrossOverResult getMutationResult(IMutationEngine engine) {
        return (new MutateStrategy(engine)).executeStrategy();
    }
}
