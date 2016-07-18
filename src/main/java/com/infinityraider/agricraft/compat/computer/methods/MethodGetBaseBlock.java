package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.util.BlockWithMeta;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

public class MethodGetBaseBlock extends MethodBaseGrowthReq {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        if(plant==null) {
            return null;
        }
        BlockWithMeta block = plant.getGrowthRequirement().getRequiredBlock();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
