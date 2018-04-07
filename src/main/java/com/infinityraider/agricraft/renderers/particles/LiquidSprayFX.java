package com.infinityraider.agricraft.renderers.particles;

//heavily inspired by the OpenBlocks sprinkler
import com.infinityraider.agricraft.utility.IconHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LiquidSprayFX extends Particle {

    public LiquidSprayFX(World world, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
        this(world, IconHelper.getIcon(fluid), x, y, z, scale, gravity, velocity);
    }

    public LiquidSprayFX(World world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
        this(world, IconHelper.getIcon(fluid), x, y, z, scale, gravity, velocity);
    }

    public LiquidSprayFX(World world, TextureAtlasSprite icon, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
        super(world, x, y, z, velocity.x, velocity.y, velocity.z);

        particleGravity = gravity;
        this.particleMaxAge = 50;
        setSize(0.2f, 0.2f);
        this.particleScale = scale;
        this.canCollide = true;
        motionX = velocity.x;
        motionY = velocity.y;
        motionZ = velocity.z;

        setParticleTexture(icon);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

}
