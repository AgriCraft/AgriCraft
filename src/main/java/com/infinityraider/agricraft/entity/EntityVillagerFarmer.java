package com.infinityraider.agricraft.entity;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;

import com.infinityraider.agricraft.init.WorldGen;

public class EntityVillagerFarmer extends EntityVillager {
    @SuppressWarnings("unused")
    public EntityVillagerFarmer(World world) {
        this(world, 0);
    }

    @SuppressWarnings("unused")
    public EntityVillagerFarmer(World world, int profession) {
        super(world, WorldGen.getVillagerId());
        this.tasks.addTask(5, new EntityAIClearWeeds(this));
    }

    @Override
    public int getProfession() {
        return WorldGen.getVillagerId();
    }

    @Override
    public boolean isAIDisabled() {
        return super.isAIDisabled();
    }
}
