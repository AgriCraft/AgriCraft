package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import com.InfinityRaider.AgriCraft.renderers.particles.RuneFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererParticlesEnchanted extends PlayerEffectRendererParticles {
    protected PlayerEffectRendererParticlesEnchanted() {
        super();
    }

    @Override
    protected ResourceLocation getParticleTexture() {
        return new ResourceLocation("agricraft", "textures/entities/player/particles/runes.png");
    }

    @Override
    protected short getSpawnDelay() {
        return (short) 10;
    }

    @Override
    protected EntityFX getParticle(EntityPlayer player, float partialTick) {
        double x = player.posX-0.5F;
        double y = player.posY + (Minecraft.getMinecraft().thePlayer==player?0:1);
        double z = player.posZ-0.5F;

        Vec3 vector = Vec3.createVectorHelper(0, 0, 0);
        double radius = 2*player.worldObj.rand.nextDouble();
        double angle = Math.toRadians(player.worldObj.rand.nextInt(360));
        double height = player.worldObj.rand.nextDouble();
        x = x + radius*Math.cos(angle);
        y = y + height;
        z = z + radius*Math.sin(angle);

        short u = (short) player.worldObj.rand.nextInt(4);
        short v = (short) player.worldObj.rand.nextInt(4);

        float f6 = ((float) u)/4.0F;
        float f7 = ((float) (u+1))/4.0F;
        float f8 = ((float) v)/4.0F;
        float f9 = ((float) (v+1))/4.0F;
        return new RuneFX(player.worldObj, x, y, z, 0.01F, vector, texture, f6, f8, f7, f9);
    }

    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Gideonseymour");
        return list;
    }
}
