package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.resources.ResourceLocation;

public class StructureGreenHouseIrrigated extends StructureGreenHouse {
    public StructureGreenHouseIrrigated(ResourceLocation id, ResourceLocation target) {
        super(id, target);
    }

    @Override
    public int weight() {
        return AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight();
    }
}
