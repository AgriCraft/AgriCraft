package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.api.v1.BlockWithMeta;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class MethodGetNeededSoil extends MethodBaseGrowthReq {
    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) {
        BlockWithMeta block = plant.getGrowthRequirement().getSoil();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
