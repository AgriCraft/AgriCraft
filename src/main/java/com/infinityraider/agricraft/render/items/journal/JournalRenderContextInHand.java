package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableFloat;

@OnlyIn(Dist.CLIENT)
public class JournalRenderContextInHand implements IJournalDataDrawer.IPageRenderContext, IRenderUtilities {
    private static final Quaternion ROTATION_180;
    private static final Matrix4f PROJECTION;
    private final int page_width;
    private final int page_height;
    private final float scale_width;
    private final float scale_height;
    private final float colorModifier;

    protected JournalRenderContextInHand(float width, float height, float colorModifier) {
        this.page_width = (int) (32*(width - 1));
        this.page_height = (int) (32*(height - 1));
        this.scale_width = (width - 1.0F)/16.0F;
        this.scale_height = (height - 1.0F)/16.0F;
        this.colorModifier = colorModifier;
    }

    @Override
    public int getPageWidth() {
        return this.page_width;
    }

    @Override
    public int getPageHeight() {
        return this.page_height;
    }

    @Override
    public void draw(MatrixStack transforms, ResourceLocation texture,
                     float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
        this.bindTexture(texture);
        this.draw(transforms, x, y, w, h, u1, v1, u2, v2, this.colorModifier, this.colorModifier, this.colorModifier, 1.0F);
    }

    @Override
    public void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
        this.bindTextureAtlas();
        this.draw(transforms, x, y, w, h, texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV(),
                this.colorModifier*r, this.colorModifier*g, this.colorModifier*b, a);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void draw(MatrixStack transforms, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        Matrix4f matrix = transforms.getLast().getMatrix();
        float x1 = (this.scale_width*x)/this.getPageWidth();
        float y1 = (this.scale_height*y)/this.getPageHeight();
        float x2 = (this.scale_width*(x + w))/this.getPageWidth();
        float y2 = (this.scale_height*(y + h))/this.getPageHeight();
        bufferbuilder.pos(matrix, x1, y2, 0).tex(u1, v2).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, x2, y2, 0).tex(u2, v2).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, x2, y1, 0).tex(u2, v1).color(r, g, b, a).endVertex();
        bufferbuilder.pos(matrix, x1, y1, 0).tex(u1, v1).color(r, g, b, a).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    @Override
    public float drawText(MatrixStack transforms, ITextComponent text, float x, float y, float scale) {
        transforms.push();
        transforms.translate((this.scale_width*x)/this.getPageWidth(), (this.scale_height*y)/this.getPageHeight(), 0F);
        float f = scale*this.scale_width/this.getPageWidth();
        transforms.scale(f, f, 1);
        // Split string
        int l = (int) ((this.getPageWidth() - x - 2)/scale);
        MutableFloat dy = new MutableFloat();
        this.getFontRenderer().trimStringToWidth(text, l).forEach(t -> {
            this.getFontRenderer().func_238422_b_(transforms, t, 0.0F, 0.0F, 0);
            dy.add(this.getFontRenderer().FONT_HEIGHT);
            transforms.translate(0, this.getFontRenderer().FONT_HEIGHT, 0);
        });
        transforms.pop();
        return dy.getValue()*scale;
    }

    @Override
    public void drawItem(MatrixStack transforms, ItemStack item, float x, float y) {
        transforms.push();
        transforms.translate((this.scale_width*(x + 8))/this.getPageWidth(), (this.scale_height*(y + 8))/this.getPageHeight(), 0);
        transforms.rotate(ROTATION_180);
        transforms.push();
        float f = this.scale_width/8.0F;
        transforms.scale(f, f, 1);
        transforms.push();
        transforms.getLast().getMatrix().mul(PROJECTION);
        this.renderItem(item, ItemCameraTransforms.TransformType.GUI, 15728880, transforms, this.getRenderTypeBuffer());
        transforms.pop();
        transforms.pop();
        transforms.pop();
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return IRenderUtilities.super.getSprite(location);
    }

    static {
        ROTATION_180 = Vector3f.XP.rotationDegrees(180);
        // Projection
        PROJECTION = new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 1
        });}
}
