package com.infinityraider.agricraft.renderers.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSeedAnalyzerBook extends ModelBase {
	
	private static final ResourceLocation texture = new ResourceLocation("agricraft:textures/blocks/seedAnalyzerBook.png");
	
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
		Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
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
