package com.InfinityRaider.AgriCraft.renderers.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class PlayerEffectRendererEntity extends PlayerEffectRenderer {
    private IWrappedEntity entityWrapper;
    private EntityLiving entity;
    private ModelBase model;
    private ResourceLocation texture;

    protected PlayerEffectRendererEntity() {
        this.entityWrapper = getEntityWrapper();
        this.entity = entityWrapper.getEntity();
        this.model = getModel();
        this.texture = getTexture();
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick) {
        entity.prevPosX = entity.posX;
        entity.prevPosY = entity.posY;
        entity.prevPosZ = entity.posZ;
        entity.prevRotationPitch = entity.rotationPitch;
        entity.prevRotationYaw = entity.rotationYaw;
        entity.posX = player.posX;
        entity.posY = player.posY;
        entity.posZ = player.posZ;
        entity.rotationYaw = player.rotationYaw;
        entity.rotationPitch = player.rotationPitch;

        entityWrapper.performAnimationUpdates();
        double arg = Math.toRadians(360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double vY = 10;
        double aY = 0.2F;

        double dx = 0.5;
        double dy = -1 + aY*Math.cos((vY*arg)-Math.PI);
        double dz = 0.5;
        float scale = getScale();

        GL11.glTranslated(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        model.render(entity, 0, 0, 0, 0, 0, 1);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslated(-dx, -dy, -dz);

    }

    protected abstract IWrappedEntity getEntityWrapper();

    protected abstract ModelBase getModel();

    protected abstract ResourceLocation getTexture();

    protected abstract float getScale();

    protected interface IWrappedEntity<T extends EntityLiving> {
        T getEntity();

        void performAnimationUpdates();
    }
}
