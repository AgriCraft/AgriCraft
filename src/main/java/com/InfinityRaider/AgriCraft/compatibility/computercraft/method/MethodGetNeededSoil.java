package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetNeededSoil extends MethodCropBase {
    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        BlockWithMeta block = GrowthRequirementHandler.getGrowthRequirement(crop.getPlant()).getSoil();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
