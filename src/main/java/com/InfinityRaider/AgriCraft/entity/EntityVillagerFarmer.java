package com.InfinityRaider.AgriCraft.entity;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;

public class EntityVillagerFarmer extends EntityVillager {
    public EntityVillagerFarmer(World world) {
        super(world);
    }

    public EntityVillagerFarmer(World world, int profession) {
        super(world, profession);
        this.tasks.addTask(5, new EntityAIClearWeeds(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }
}
