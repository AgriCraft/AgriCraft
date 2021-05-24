package com.infinityraider.agricraft.render.particles;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleHelper {
    public static void spawnSprinklerParticles(World world, double x, double y, double z, float angle) {
        if (AgriCraft.instance.getConfig().disableParticles()) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            float alpha = -(angle + 90 * i) * ((float) Math.PI) / 180;
            double xOffset = (4 * Constants.UNIT) * Math.cos(alpha);
            double zOffset = (4 * Constants.UNIT) * Math.sin(alpha);
            float radius = 0.3F;
            for (int j = 0; j <= 4; j++) {
                float beta = -j * ((float) Math.PI) / (8.0F);
                Vector3d vector = new Vector3d(radius * Math.cos(alpha), radius * Math.sin(beta), radius * Math.sin(alpha));
                spawnSprinklerParticle(world, x + xOffset * (4 - j) / 4, y, z + zOffset * (4 - j) / 4, vector);
            }
        }
    }

    private static void spawnSprinklerParticle(World world, double x, double y, double z, Vector3d vector) {
        if(world instanceof ClientWorld) {
            final SprinklerParticle spray = new SprinklerParticle((ClientWorld) world, Fluids.WATER, x, y, z, 0.25F, 0.7F, vector);
            Minecraft.getInstance().particles.addEffect(spray);
        }
    }
}
