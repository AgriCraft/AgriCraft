package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import net.minecraft.item.ItemStack;

public class MethodGetNeededSoil extends MethodBaseGrowthReq {
    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) {
        BlockWithMeta block = plant.getGrowthRequirement().getSoil();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }
}
