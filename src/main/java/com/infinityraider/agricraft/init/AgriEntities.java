package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.entity.villager.EntityVillagerFarmer;
import com.infinityraider.infinitylib.entity.EntityRegistryEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class AgriEntities {
    private static final AgriEntities INSTANCE = new AgriEntities();

    public static AgriEntities getInstance() {
        return INSTANCE;
    }

    public final EntityRegistryEntry<EntityVillagerFarmer> farmer;

    private AgriEntities() {
        this.farmer = new EntityRegistryEntry<>(EntityVillagerFarmer.class, "villager_farmer")
                .setTrackingDistance(64)
                .setVelocityUpdates(true)
                .setUpdateFrequency(1)
                .onRegisterCallBack(() ->  VillagerRegistry.instance().register(EntityVillagerFarmer.PROFESSION));
    }
}
