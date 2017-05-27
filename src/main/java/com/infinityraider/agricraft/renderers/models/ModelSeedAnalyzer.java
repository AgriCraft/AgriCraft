package com.infinityraider.agricraft.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSeedAnalyzer extends ModelBase {

    //fields
    ModelRenderer base;
    ModelRenderer stick1;
    ModelRenderer stick2;
    ModelRenderer frame1;
    ModelRenderer frame2;
    ModelRenderer window;

    public ModelSeedAnalyzer() {
        textureWidth = 64;
        textureHeight = 64;

        base = new ModelRenderer(this, 0, 0);
        base.addBox(0F, 0F, 0F, 14, 4, 14);
        base.setRotationPoint(-7F, 20F, -7F);
        base.setTextureSize(textureWidth, textureHeight);
        base.mirror = true;
        setRotation(base, 0F, 0F, 0F);

        stick1 = new ModelRenderer(this, 57, 0);
        stick1.addBox(0F, -13F, 0F, 1, 13, 1);
        stick1.setRotationPoint(7F, 22F, -5F);
        stick1.setTextureSize(textureWidth, textureHeight);
        stick1.mirror = true;
        setRotation(stick1, -1.07818F, 0F, 0F);

        stick2 = new ModelRenderer(this, 57, 0);
        stick2.addBox(0F, 0F, 0F, 1, 13, 1);
        stick2.setRotationPoint(6F, 16.16667F, 6.9F);
        stick2.setTextureSize(textureWidth, textureHeight);
        stick2.mirror = true;

        setRotation(stick2, -1.896109F, 0F, 0F);
        frame1 = new ModelRenderer(this, 0, 19);
        frame1.addBox(0F, 0F, 0F, 1, 10, 1);
        frame1.setRotationPoint(-6F, 9F, 1.7F);
        frame1.setTextureSize(textureWidth, textureHeight);
        frame1.mirror = true;

        setRotation(frame1, -1.115358F, 0F, 0F);
        frame2 = new ModelRenderer(this, 0, 19);
        frame2.addBox(0F, 0F, 0F, 1, 10, 1);
        frame2.setRotationPoint(5F, 9F, 1.7F);
        frame2.setTextureSize(textureWidth, textureHeight);
        frame2.mirror = true;
        setRotation(frame2, -1.115358F, 0F, 0F);

        window = new ModelRenderer(this, 5, 19);
        window.addBox(0F, 0F, 0F, 10, 10, 0);
        window.setRotationPoint(-5F, 9.4F, 1.9F);
        window.setTextureSize(textureWidth, textureHeight);
        window.mirror = true;
        setRotation(window, -1.115358F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        base.render(f5);
        stick1.render(f5);
        stick2.render(f5);
        frame1.render(f5);
        frame2.render(f5);
        window.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }
}
