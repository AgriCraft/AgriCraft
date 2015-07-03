package com.InfinityRaider.AgriCraft.entity;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;

public class EntityVillagerFarmer extends EntityVillager {
    public EntityVillagerFarmer(World world) {
        super(world);
    }

    public EntityVillagerFarmer(World world, int profession) {
        super(world, profession);
    }

    @Override
    protected void updateAITick() {
        super.updateAITick();
        //TODO: Make villager find crops with weeds and make him remove the weeds
    }
}
