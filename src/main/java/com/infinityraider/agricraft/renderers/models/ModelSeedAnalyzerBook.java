package com.infinityraider.agricraft.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSeedAnalyzerBook extends ModelBase {

    //fields
    public ModelRenderer book;

    public ModelSeedAnalyzerBook() {
        textureWidth = 64;
        textureHeight = 64;
        book = new ModelRenderer(this, 46, 26);
        book.addBox(0F, 0F, 0F, 4, 1, 5, 0F);
        book.setRotationPoint(-7F, 19F, 3F);
        book.setTextureSize(textureWidth, textureHeight);
        this.setRotation(book, 0F, 0.5759587F, 0F);
        book.mirror = true;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        book.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}
