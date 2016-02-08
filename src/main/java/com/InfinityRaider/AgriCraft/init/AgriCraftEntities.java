package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.entity.EntityLeashKnotAgricraft;
import com.InfinityRaider.AgriCraft.entity.EntityVillagerFarmer;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class AgriCraftEntities {
    public static void init() {
        EntityRegistry.registerModEntity(EntityVillagerFarmer.class, Names.Objects.villager, 0, AgriCraft.instance, 64, 1, true);
        EntityRegistry.registerModEntity(EntityLeashKnotAgricraft.class, Names.Objects.leash, 1, AgriCraft.instance, 16, 100, false);
    }
}
