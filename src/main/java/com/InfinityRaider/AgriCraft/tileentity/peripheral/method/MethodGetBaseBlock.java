package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.api.v1.BlockWithMeta;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import net.minecraft.item.ItemStack;

public class MethodGetBaseBlock extends MethodBaseGrowthReq {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) {
        if(plant==null) {
            return null;
        }
        BlockWithMeta block = plant.getGrowthRequirement().getRequiredBlock();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
