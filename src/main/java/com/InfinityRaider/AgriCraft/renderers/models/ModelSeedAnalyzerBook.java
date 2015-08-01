package com.InfinityRaider.AgriCraft.renderers.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelSeedAnalyzerBook extends ModelBase {
	public ModelSeedAnalyzerBook() {
		Book = new ModelRenderer(this, 0, 0);
		Book.addBox(0F, 0F, 0F, 4, 1, 5, 0F);
		Book.setRotationPoint(-7F, 19F, 3F);
		Book.rotateAngleX = 0F;
		Book.rotateAngleY = 0.5759587F;
		Book.rotateAngleZ = 0F;
		Book.mirror = false;
	}
	
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Book.render(f5);
	}
	
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}
	
	//fields
	public ModelRenderer Book;
}
