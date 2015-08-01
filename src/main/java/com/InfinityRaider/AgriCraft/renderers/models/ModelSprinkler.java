package com.InfinityRaider.AgriCraft.renderers.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSprinkler extends ModelBase {
	public ModelSprinkler() {
        crossBar1 = new ModelRenderer(this, 0, 0);
        crossBar1.addBox(0F, 0F, 0F, 9, 1, 1, 0F);
        crossBar1.setRotationPoint(-4.5F, 19F, -0.5F);
        crossBar1.rotateAngleX = 0F;
        crossBar1.rotateAngleY = 0F;
        crossBar1.rotateAngleZ = 0F;
        crossBar1.mirror = false;
        crossBar2 = new ModelRenderer(this, 0, 2);
        crossBar2.addBox(0F, 0F, 0F, 1, 1, 9, 0F);
        crossBar2.setRotationPoint(-0.5F, 19F, -4.5F);
        crossBar2.rotateAngleX = 0F;
        crossBar2.rotateAngleY = 0F;
        crossBar2.rotateAngleZ = 0F;
        crossBar2.mirror = false;
        connectingRod = new ModelRenderer(this, 20, 0);
        connectingRod.addBox(0F, 0F, 0F, 3, 11, 3, 0F);
        connectingRod.setRotationPoint(-1.5F, 10F, -1.5F);
        connectingRod.rotateAngleX = 0F;
        connectingRod.rotateAngleY = 0F;
        connectingRod.rotateAngleZ = 0F;
        connectingRod.mirror = false;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        crossBar1.render(f5);
        crossBar2.render(f5);
        connectingRod.render(f5);
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

    //fields
    public ModelRenderer crossBar1;
    public ModelRenderer crossBar2;
    public ModelRenderer connectingRod;
}
