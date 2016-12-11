package com.infinityraider.agricraft.entity.villager;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class EntityVillagerFarmer extends EntityVillager {
    public static final VillagerRegistry.VillagerProfession PROFESSION = new VillagerRegistry.VillagerProfession(
            "farmer.agricraft",
            "agricraft:textures/entities/villager.png",
            "minecraft:textures/entity/zombie_villager/zombie_villager.png"
            );

    public EntityVillagerFarmer(World world) {
        super(world, 0);
        this.setProfession(PROFESSION);
        this.tasks.addTask(5, new EntityAIClearWeeds(this));
    }

    @Override
    public boolean isAIDisabled() {
        return super.isAIDisabled();
    }
}
