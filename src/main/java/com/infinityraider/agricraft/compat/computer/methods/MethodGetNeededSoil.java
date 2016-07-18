package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.util.BlockWithMeta;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

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
