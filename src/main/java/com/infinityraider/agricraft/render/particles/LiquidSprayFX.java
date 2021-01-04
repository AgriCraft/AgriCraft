package com.infinityraider.agricraft.render.particles;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class LiquidSprayFX extends AgriCraftFX {

    public LiquidSprayFX(ClientWorld world, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vector3d velocity) {
        this(world, getIcon(fluid.getFluid()), x, y, z, scale, gravity, velocity);
    }

    public LiquidSprayFX(ClientWorld world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vector3d velocity) {
        this(world, getIcon(fluid), x, y, z, scale, gravity, velocity);
    }

    public LiquidSprayFX(ClientWorld world, TextureAtlasSprite icon, double x, double y, double z, float scale, float gravity, Vector3d velocity) {
        super(world, x, y, z, scale, gravity, velocity, icon);
    }

    private static TextureAtlasSprite getIcon(Fluid fluid) {
        return null;    //TODO
    }
}
