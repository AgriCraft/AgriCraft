package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.util.ResourceLocation;

public class StructureGreenHouseIrrigated extends StructureGreenHouse {
    public StructureGreenHouseIrrigated(ResourceLocation id, ResourceLocation target) {
        super(id, target);
    }

    @Override
    public int weight() {
        return AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight();
    }
}
