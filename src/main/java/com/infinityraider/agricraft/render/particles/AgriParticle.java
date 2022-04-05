package com.infinityraider.agricraft.render.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AgriParticle extends TextureSheetParticle {
    protected AgriParticle(ClientLevel world, double x, double y, double z, float scale, float gravity, Vec3 vector, ResourceLocation texture) {
        this(world, x, y, z, scale, gravity, vector, getIcon(texture));
    }

    protected AgriParticle(ClientLevel world, double x, double y, double z, float scale, float gravity, Vec3 vector, TextureAtlasSprite icon) {
        super(world, x, y, z, 0, 0, 0);
        this.setSprite(icon);
        this.gravity = gravity;
        this.scale(scale);
        this.xd = vector.x;
        this.yd = vector.y;
        this.zd = vector.z;
    }

    protected static TextureAtlasSprite getIcon(ResourceLocation texture) {
        return Minecraft.getInstance().getModelManager()
                .getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(texture);
    }
}
