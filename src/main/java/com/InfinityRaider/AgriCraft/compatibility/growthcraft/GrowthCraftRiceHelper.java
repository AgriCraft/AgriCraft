package com.InfinityRaider.AgriCraft.compatibility.growthcraft;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;

public class GrowthCraftRiceHelper extends ModHelper {
    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {
        try {
            CropPlant ricePlant = new CropPlantGrowthCraftRice();
            CropPlantHandler.registerPlant(ricePlant);
            GrowthRequirementHandler.getGrowthRequirement(ricePlant.getSeed().getItem(), ricePlant.getSeed().getItemDamage()).setSoil(new BlockWithMeta(Blocks.blockWaterPadFull));

        } catch (DuplicateCropPlantException e) {
            e.printStackTrace();
        } catch (BlacklistedCropPlantException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.growthcraft+"|Rice";
    }
}
