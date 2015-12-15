package com.InfinityRaider.AgriCraft.entity;

import com.InfinityRaider.AgriCraft.blocks.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class EntityLeashKnotAgricraft extends EntityLeashKnot {
    @SuppressWarnings("unused")
    public EntityLeashKnotAgricraft(World world) {
        super(world);
    }

    public EntityLeashKnotAgricraft(World world, int x, int y, int z) {
        super(world, x, y, z);
    }

    public boolean onValidSurface() {
        return super.onValidSurface() || this.worldObj.getBlock(this.field_146063_b, this.field_146064_c, this.field_146062_d) instanceof BlockFence;
    }

    public static EntityLeashKnotAgricraft getKnotForBlock(World world, int x, int y, int z) {
        List list = world.getEntitiesWithinAABB(EntityLeashKnotAgricraft.class, AxisAlignedBB.getBoundingBox((double) x - 1.0D, (double) y - 1.0D, (double) z - 1.0D, (double) x + 1.0D, (double) y + 1.0D, (double) z + 1.0D));
        if (list != null) {
            for (Object obj : list) {
                EntityLeashKnotAgricraft entityleashknot = (EntityLeashKnotAgricraft) obj;
                if (entityleashknot.field_146063_b == x && entityleashknot.field_146064_c == y && entityleashknot.field_146062_d == z) {
                    return entityleashknot;
                }
            }
        }
        return null;
    }

    public static EntityLeashKnotAgricraft func_110129_a(World world, int x, int y, int z) {
        EntityLeashKnotAgricraft entityleashknot = new EntityLeashKnotAgricraft(world, x, y, z);
        entityleashknot.forceSpawn = true;
        world.spawnEntityInWorld(entityleashknot);
        return entityleashknot;
    }
}
