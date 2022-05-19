package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
    public void draw(PoseStack transforms, ResourceLocation texture,
                     float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
        this.bindTexture(texture);
        this.draw(transforms, x, y, w, h, u1, v1, u2, v2, this.colorModifier, this.colorModifier, this.colorModifier, 1.0F);
    }

    @Override
    public void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
        this.bindTextureAtlas();
        this.draw(transforms, x, y, w, h, texture.getU0(), texture.getV0(), texture.getU1(), texture.getV1(),
                this.colorModifier*r, this.colorModifier*g, this.colorModifier*b, a);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void draw(PoseStack transforms, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
        MultiBufferSource.BufferSource buffer = this.getRenderTypeBuffer();
        VertexConsumer builder = buffer.getBuffer(IconRenderType.INSTANCE);
        Matrix4f matrix = transforms.last().pose();
        float x1 = (this.scale_width*x)/this.getPageWidth();
        float y1 = (this.scale_height*y)/this.getPageHeight();
        float x2 = (this.scale_width*(x + w))/this.getPageWidth();
        float y2 = (this.scale_height*(y + h))/this.getPageHeight();
        builder.vertex(matrix, x1, y2, 0).color(r, g, b, a).uv(u1, v2).endVertex();
        builder.vertex(matrix, x2, y2, 0).color(r, g, b, a).uv(u2, v2).endVertex();
        builder.vertex(matrix, x2, y1, 0).color(r, g, b, a).uv(u2, v1).endVertex();
        builder.vertex(matrix, x1, y1, 0).color(r, g, b, a).uv(u1, v1).endVertex();
        buffer.endBatch(IconRenderType.INSTANCE);
    }

    @Override
    public float drawText(PoseStack transforms, Component text, float x, float y, float scale) {
        transforms.pushPose();
        transforms.translate((this.scale_width*x)/this.getPageWidth(), (this.scale_height*y)/this.getPageHeight(), 0F);
        float f = scale*this.scale_width/this.getPageWidth();
        transforms.scale(f, f, 1);
        // Split string
        int l = (int) ((this.getPageWidth() - x - 2)/scale);
        MutableFloat dy = new MutableFloat();
        this.getFontRenderer().split(text, l).forEach(t -> {
            this.getFontRenderer().draw(transforms, t, 0.0F, 0.0F, 0);
            dy.add(this.getFontRenderer().lineHeight);
            transforms.translate(0, this.getFontRenderer().lineHeight, 0);
        });
        transforms.popPose();
        return dy.getValue()*scale;
    }

    @Override
    public void drawItem(PoseStack transforms, ItemStack item, float x, float y) {
        transforms.pushPose();
        transforms.translate((this.scale_width*(x + 8))/this.getPageWidth(), (this.scale_height*(y + 8))/this.getPageHeight(), 0);
        transforms.mulPose(ROTATION_180);
        transforms.pushPose();
        float f = this.scale_width/8.0F;
        transforms.scale(f, f, 1);
        transforms.pushPose();
        transforms.last().pose().multiply(PROJECTION);
        this.renderItem(item, ItemTransforms.TransformType.GUI, 15728880, transforms, this.getRenderTypeBuffer());
        transforms.popPose();
        transforms.popPose();
        transforms.popPose();
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return IRenderUtilities.super.getSprite(location);
    }

    private static class IconRenderType extends RenderType {
        // Need to have a constructor...
        public IconRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean crumbling, boolean sorted, Runnable runnablePre, Runnable runnablePost) {
            super(name, format, mode, bufferSize, crumbling, sorted, runnablePre, runnablePost);
        }

        private static final RenderType INSTANCE = create("agri_journal_icons",
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .createCompositeState(false));
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
