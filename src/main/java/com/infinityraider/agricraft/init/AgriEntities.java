package com.infinityraider.agricraft.init;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.entity.EntityLeashKnotAgricraft;

public class AgriEntities {
    public static void init() {
        //EntityRegistry.registerModEntity(EntityVillagerFarmer.class, "villager_farmer", 0, AgriCraft.instance, 64, 1, true);   //TODO: fix villager
        EntityRegistry.registerModEntity(EntityLeashKnotAgricraft.class, "leash", 1, AgriCraft.instance, 16, 100, false);
    }
}
