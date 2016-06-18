package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v3.util.BlockWithMeta;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v3.core.IAgriPlant;

public class MethodGetNeededSoil extends MethodBaseGrowthReq {
    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        BlockWithMeta block = plant.getGrowthRequirement().getSoil();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
