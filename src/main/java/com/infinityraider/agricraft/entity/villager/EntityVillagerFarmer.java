package com.infinityraider.agricraft.entity.villager;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public static class RenderFactory implements IRenderFactory<EntityVillagerFarmer> {
        private static final RenderFactory INSTANCE = new RenderFactory();

        public static RenderFactory getInstance() {
            return INSTANCE;
        }

        private RenderFactory() {}

        @Override
        @SideOnly(Side.CLIENT)
        public Render<? super EntityVillagerFarmer> createRenderFor(RenderManager manager) {
            return new RenderVillager(manager);
        }
    }
}
