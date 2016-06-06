package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v1.BlockWithMeta;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class MethodGetBaseBlock extends MethodBaseGrowthReq {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) {
        if(plant==null) {
            return null;
        }
        BlockWithMeta block = plant.getGrowthRequirement().getRequiredBlock();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
