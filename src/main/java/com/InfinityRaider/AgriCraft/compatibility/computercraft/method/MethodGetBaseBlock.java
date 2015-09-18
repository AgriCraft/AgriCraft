package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetBaseBlock extends MethodCropBase {
    public MethodGetBaseBlock() {
        super("getBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        CropPlant plant = crop.getPlant();
        if(plant==null) {
            return null;
        }
        BlockWithMeta block = GrowthRequirementHandler.getGrowthRequirement(plant).getRequiredBlock();
        String msg = block==null?"null":(new ItemStack(block.getBlock(), 1, block.getMeta())).getDisplayName();
        return new Object[] {msg};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
