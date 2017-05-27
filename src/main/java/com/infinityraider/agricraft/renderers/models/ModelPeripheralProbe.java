package com.infinityraider.agricraft.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPeripheralProbe extends ModelBase {

    //fields
    ModelRenderer Shaft;
    ModelRenderer Bar1;
    ModelRenderer Probe1;
    ModelRenderer Probe2;
    ModelRenderer Bar2;
    ModelRenderer Bar3;
    ModelRenderer Probe3Left;
    ModelRenderer Probe3Right;

    public ModelPeripheralProbe() {
        textureWidth = 32;
        textureHeight = 32;
        Shaft = new ModelRenderer(this, 0, 0);
        Shaft.addBox(-4F, 0F, 0F, 8, 1, 1);
        Shaft.setRotationPoint(0F, 21F, -6F);
        Shaft.setTextureSize(32, 32);
        Shaft.mirror = true;
        setRotation(Shaft, 0F, 0F, 0F);
        Bar1 = new ModelRenderer(this, 0, 2);
        Bar1.addBox(0F, 0F, 0F, 1, 5, 1);
        Bar1.setRotationPoint(1F, 16F, -6F);
        Bar1.setTextureSize(32, 32);
        Bar1.mirror = true;
        setRotation(Bar1, 0F, 0F, 0F);
        Probe1 = new ModelRenderer(this, 4, 2);
        Probe1.addBox(0F, 0F, 0F, 1, 1, 2);
        Probe1.setRotationPoint(1F, 16F, -8F);
        Probe1.setTextureSize(32, 32);
        Probe1.mirror = true;
        setRotation(Probe1, 0F, 0F, 0F);
        Probe2 = new ModelRenderer(this, 10, 2);
        Probe2.addBox(0F, 0F, 0F, 1, 1, 2);
        Probe2.setRotationPoint(-2F, 16F, -8F);
        Probe2.setTextureSize(32, 32);
        Probe2.mirror = true;
        setRotation(Probe2, 0F, 0F, 0F);
        Bar2 = new ModelRenderer(this, 0, 2);
        Bar2.addBox(0F, 0F, 0F, 1, 5, 1);
        Bar2.setRotationPoint(-2F, 16F, -6F);
        Bar2.setTextureSize(32, 32);
        Bar2.mirror = true;
        setRotation(Bar2, 0F, 0F, 0F);
        Bar3 = new ModelRenderer(this, 0, 2);
        Bar3.addBox(0F, 0F, 0F, 1, 7, 1);
        Bar3.setRotationPoint(-0.5F, 14F, -6F);
        Bar3.setTextureSize(32, 32);
        Bar3.mirror = true;
        setRotation(Bar3, 0F, 0F, 0F);
        Probe3Left = new ModelRenderer(this, 4, 5);
        Probe3Left.addBox(0F, 0F, 0F, 1, 3, 1);
        Probe3Left.setRotationPoint(0.5F, 12F, -6F);
        Probe3Left.setTextureSize(32, 32);
        Probe3Left.mirror = true;
        setRotation(Probe3Left, 0F, 0F, 0F);
        Probe3Right = new ModelRenderer(this, 8, 5);
        Probe3Right.addBox(0F, 0F, 0F, 1, 2, 1);
        Probe3Right.setRotationPoint(-1.5F, 13F, -6F);
        Probe3Right.setTextureSize(32, 32);
        Probe3Right.mirror = true;
        setRotation(Probe3Right, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Shaft.render(f5);
        Bar1.render(f5);
        Probe1.render(f5);
        Probe2.render(f5);
        Bar2.render(f5);
        Bar3.render(f5);
        Probe3Left.render(f5);
        Probe3Right.render(f5);
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
