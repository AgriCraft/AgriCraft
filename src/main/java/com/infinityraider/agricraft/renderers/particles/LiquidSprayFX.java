package com.infinityraider.agricraft.renderers.particles;

//heavily inspired by the OpenBlocks sprinkler
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LiquidSprayFX extends AgriCraftFX {

    public LiquidSprayFX(World world, double x, double y, double z, float scale, float gravity, Vec3d vector) {
        super(world, x, y, z, scale, gravity, vector, new ResourceLocation("minecraft:textures/blocks/water_still.png"));
        this.particleMaxAge = 15;
        this.setSize(0.2f, 0.2f);
    }

    @Override
    public void renderParticle(BufferBuilder worldRenderer, Entity entity, float partialTicks, float f0, float f1, float f2, float f3, float f4) {
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        super.renderParticle(worldRenderer, entity, partialTicks, f0, f1, f2, f3, f4);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }
}
