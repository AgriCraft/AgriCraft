package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Optional;

public class MethodGetNeededSoil extends MethodBaseGrowthReq {

    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(Optional<IAgriPlant> plant) {
        return new Object[]{plant.flatMap(this::getMainSoil).orElse("null")};
    }

    private Optional<String> getMainSoil(IAgriPlant plant) {
        return plant.getGrowthRequirement().getSoils().stream()
                .findFirst()
                .map(s -> s.getName());
    }
}
