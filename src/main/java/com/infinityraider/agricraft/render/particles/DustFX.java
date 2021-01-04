package com.infinityraider.agricraft.render.particles;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustFX extends AgriCraftFX {

    public DustFX(ClientWorld world, double x, double y, double z, float scale, float gravity, Vector3d vector, ResourceLocation texture) {
        super(world, x, y, z, scale, gravity, vector, texture);
        this.maxAge = 50;
        this.setSize(0.2f, 0.2f);
    }
}
