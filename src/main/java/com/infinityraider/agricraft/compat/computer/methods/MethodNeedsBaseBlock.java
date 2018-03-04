package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Optional;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {

    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(Optional<IAgriPlant> plant) throws InvocationException {
        return new Object[]{plant.flatMap(p -> p.getGrowthRequirement().getConditionStack()).isPresent()};
    }

}
