package com.infinityraider.agricraft.entity.villager;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class EntityVillagerFarmer extends EntityVillager {
    public static final VillagerRegistry.VillagerProfession PROFESSION;
    public static final VillagerRegistry.VillagerCareer CAREER;

    public EntityVillagerFarmer(World world) {
        super(world, 0);
        this.setProfession(PROFESSION);
        this.tasks.addTask(5, new EntityAIClearWeeds(this));
    }

    @Override
    public boolean isAIDisabled() {
        return super.isAIDisabled();
    }

    static {
        PROFESSION  = new VillagerRegistry.VillagerProfession(
                "farmer.agricraft",
                "agricraft:textures/entities/villager.png",
                "minecraft:textures/entity/zombie_villager/zombie_villager.png"
        );
        CAREER  = new VillagerRegistry.VillagerCareer(PROFESSION, "farmer.agricraft");
    }
}
