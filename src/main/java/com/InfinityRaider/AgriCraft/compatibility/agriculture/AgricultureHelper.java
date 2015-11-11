package com.InfinityRaider.AgriCraft.compatibility.agriculture;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

public class AgricultureHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.agriculture;
    }

    @Override
    protected void initPlants() {
        try {
            Class registry = Class.forName("com.teammetallurgy.agriculture.BlockList");
            for (Field field : registry.getDeclaredFields()) {
                Object obj = field.get(null);
                if (!(obj instanceof BlockCrops)) {
                    continue;
                }
                Class plantClass = Class.forName("com.teammetallurgy.agriculture.block.plant.BlockPlant");
                Field plantField = plantClass.getDeclaredField("harvestItemStack");
                plantField.setAccessible(true);
                CropPlantHandler.registerPlant(new CropPlantAgriCulture((BlockCrops) obj, (ItemStack) plantField.get(obj)));
            }

        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }
}
