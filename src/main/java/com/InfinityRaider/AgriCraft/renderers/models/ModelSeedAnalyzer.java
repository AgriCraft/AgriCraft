package com.InfinityRaider.AgriCraft.renderers.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSeedAnalyzer extends ModelBase {
    public ModelSeedAnalyzer() {
            base = new ModelRenderer(this, 0, 0);
            base.addBox(0F, 0F, 0F, 14, 4, 14, 0F);
            base.setRotationPoint(-7F, 20F, -7F);
            base.rotateAngleX = 0F;
            base.rotateAngleY = 0F;
            base.rotateAngleZ = 0F;
            base.mirror = false;
            stick1 = new ModelRenderer(this, 57, 0);
            stick1.addBox(0F, -13F, 0F, 1, 13, 1, 0F);
            stick1.setRotationPoint(7F, 22F, -5F);
            stick1.rotateAngleX = -1.07818F;
            stick1.rotateAngleY = 0F;
            stick1.rotateAngleZ = 0F;
            stick1.mirror = false;
            stick2 = new ModelRenderer(this, 57, 0);
            stick2.addBox(0F, 0F, 0F, 1, 13, 1, 0F);
            stick2.setRotationPoint(6F, 16.16667F, 6.9F);
            stick2.rotateAngleX = -1.896109F;
            stick2.rotateAngleY = 0F;
            stick2.rotateAngleZ = 0F;
            stick2.mirror = false;
            frame1 = new ModelRenderer(this, 0, 19);
            frame1.addBox(0F, 0F, 0F, 1, 10, 1, 0F);
            frame1.setRotationPoint(-6F, 9F, 1.7F);
            frame1.rotateAngleX = -1.115358F;
            frame1.rotateAngleY = 0F;
            frame1.rotateAngleZ = 0F;
            frame1.mirror = false;
            frame2 = new ModelRenderer(this, 0, 19);
            frame2.addBox(0F, 0F, 0F, 1, 10, 1, 0F);
            frame2.setRotationPoint(5F, 9F, 1.7F);
            frame2.rotateAngleX = -1.115358F;
            frame2.rotateAngleY = 0F;
            frame2.rotateAngleZ = 0F;
            frame2.mirror = false;
            window = new ModelRenderer(this, 5, 19);
            window.addBox(0F, 0F, 0F, 10, 10, 0, 0F);
            window.setRotationPoint(-5F, 9.4F, 1.9F);
            window.rotateAngleX = -1.115358F;
            window.rotateAngleY = 0F;
            window.rotateAngleZ = 0F;
            window.mirror = false;
        }

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

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    //fields
    public ModelRenderer base;
    public ModelRenderer stick1;
    public ModelRenderer stick2;
    public ModelRenderer frame1;
    public ModelRenderer frame2;
    public ModelRenderer window;
}