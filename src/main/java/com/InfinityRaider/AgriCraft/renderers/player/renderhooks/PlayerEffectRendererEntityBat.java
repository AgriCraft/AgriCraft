package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererEntityBat extends PlayerEffectRendererEntity {
    protected PlayerEffectRendererEntityBat() {
        super();
    }

    @Override
    protected IWrappedEntity getEntityWrapper() {
        return new EntityBat();
    }

    @Override
    protected ModelBase getModel() {
        return new ModelBat();
    }

    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation("textures/entity/bat.png");
    }

    @Override
    protected float getScale() {
        return 0.01F;
    }

    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("chbachman");
        return names;
    }

    protected class EntityBat extends net.minecraft.entity.passive.EntityBat implements IWrappedEntity<net.minecraft.entity.passive.EntityBat> {
        public EntityBat() {
            super(Minecraft.getMinecraft().theWorld);
            this.setIsBatHanging(false);
        }

        @Override
        public boolean getIsBatHanging() {
            return false;
        }

        @Override
        public net.minecraft.entity.passive.EntityBat getEntity() {
            return this;
        }

        @Override
        public void performAnimationUpdates() {
        }

        @Override
        public float[] getModelParameters() {
            float wingAngle = (float) (20*(360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL));
            return new float[] {0, 0, wingAngle, 0, 0, 1};
        }

        @Override
        public int getFloatingVelocity() {
            return 15;
        }


    }
}
