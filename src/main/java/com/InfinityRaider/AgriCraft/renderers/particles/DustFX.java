package com.InfinityRaider.AgriCraft.renderers.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class DustFX extends AgriCraftFX {
    public DustFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector, ResourceLocation texture) {
        super(world, x, y, z, scale, gravity, vector, texture);
        this.particleMaxAge = 50;
        this.setSize(0.2f, 0.2f);
        this.noClip = true;
    }
}
