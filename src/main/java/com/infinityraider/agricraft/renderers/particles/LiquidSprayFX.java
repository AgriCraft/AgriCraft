package com.infinityraider.agricraft.renderers.particles;

//heavily inspired by the OpenBlocks sprinkler

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LiquidSprayFX extends AgriCraftFX {
    public LiquidSprayFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector) {
        super(world, x, y, z, scale, gravity, vector, FluidRegistry.WATER.getStill());
        this.particleMaxAge = 15;
        this.setSize(0.2f, 0.2f);
        this.noClip = false;
    }
}
