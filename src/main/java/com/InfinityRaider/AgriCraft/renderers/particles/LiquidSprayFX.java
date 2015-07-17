package com.InfinityRaider.AgriCraft.renderers.particles;

//heavily inspired by the OpenBlocks sprinkler

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

@SideOnly(Side.CLIENT)
public class LiquidSprayFX extends AgriCraftFX {
    public LiquidSprayFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector) {
        super(world, x, y, z, scale, gravity, vector, FluidRegistry.WATER.getStillIcon());
        this.particleMaxAge = 15;
        this.setSize(0.2f, 0.2f);
        this.noClip = false;
    }
}
