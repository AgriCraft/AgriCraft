package com.infinityraider.agricraft.render.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AgriParticle extends SpriteTexturedParticle {
    protected AgriParticle(ClientWorld world, double x, double y, double z, float scale, float gravity, Vector3d vector, ResourceLocation texture) {
        this(world, x, y, z, scale, gravity, vector, getIcon(texture));
    }

    protected AgriParticle(ClientWorld world, double x, double y, double z, float scale, float gravity, Vector3d vector, TextureAtlasSprite icon) {
        super(world, x, y, z, 0, 0, 0);
        this.setSprite(icon);
        this.particleGravity = gravity;
        this.multiplyParticleScaleBy(scale);
        this.motionX = vector.x;
        this.motionY = vector.y;
        this.motionZ = vector.z;
    }

    protected static TextureAtlasSprite getIcon(ResourceLocation texture) {
        return Minecraft.getInstance().getModelManager()
                .getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .getSprite(texture);
    }
}
