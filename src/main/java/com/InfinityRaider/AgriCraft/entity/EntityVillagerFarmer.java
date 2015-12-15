package com.InfinityRaider.AgriCraft.entity;

import com.InfinityRaider.AgriCraft.init.WorldGen;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;

public class EntityVillagerFarmer extends EntityVillager {
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
    public boolean isAIEnabled() {
        return true;
    }
}
