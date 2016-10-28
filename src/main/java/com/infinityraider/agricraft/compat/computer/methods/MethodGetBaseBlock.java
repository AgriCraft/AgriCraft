package com.infinityraider.agricraft.compat.computer.methods;

import java.util.Optional;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.requirement.BlockCondition;
import com.infinityraider.agricraft.api.util.FuzzyStack;

public class MethodGetBaseBlock extends MethodBaseGrowthReq {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        if(plant==null) {
            return null;
        }
        Optional<FuzzyStack> block = plant.getGrowthRequirement().getConditions().stream()
                .filter(c -> c instanceof BlockCondition)
                .map(c -> ((BlockCondition) c).getStack())
                .findFirst();
        String msg = block.map(b -> b.toStack().getDisplayName()).orElse("null");
        return new Object[] {msg};
    }
}
