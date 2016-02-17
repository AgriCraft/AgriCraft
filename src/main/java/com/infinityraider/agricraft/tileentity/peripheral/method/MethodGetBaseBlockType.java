package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.farming.cropplant.CropPlant;

public class MethodGetBaseBlockType extends MethodBaseGrowthReq {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) {
        if(plant==null) {
            return null;
        }
        return new Object[] {plant.getGrowthRequirement().getRequiredType().name()};
    }
}
