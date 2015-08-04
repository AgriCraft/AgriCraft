package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.item.ItemStack;

public class MethodGetNeededSoil extends MethodCropBase {
    protected MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        BlockWithMeta block = GrowthRequirementHandler.getGrowthRequirement(crop.getPlant()).getSoil();
        return new Object[] {(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName()};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
