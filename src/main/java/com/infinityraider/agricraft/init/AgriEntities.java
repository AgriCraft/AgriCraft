package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.entity.villager.EntityVillagerFarmer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class AgriEntities {
    public static void init() {
        EntityRegistry.registerModEntity(EntityVillagerFarmer.class, "villager_farmer", 0, AgriCraft.instance, 64, 1, true);
        VillagerRegistry.instance().register(EntityVillagerFarmer.PROFESSION);
    }
}
