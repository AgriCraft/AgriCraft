package com.infinityraider.agricraft.render.blocks;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.content.irrigation.TileEntitySprinkler;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TileEntitySprinklerRenderer implements ITileRenderer<TileEntitySprinkler>, IRenderUtilities {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:block/iron_block");

    private final Map<MultiBufferSource.BufferSource, ThreadLocal<ITessellator>> tessellators;
    private TextureAtlasSprite sprite;

    public TileEntitySprinklerRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    @Override
    public void render(TileEntitySprinkler tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        if(buffer instanceof MultiBufferSource.BufferSource) {
            // Apply transformation
            transforms.pushPose();
            transforms.translate(0.5, 0, 0.5);
            transforms.mulPose(Vector3f.YP.rotationDegrees(tile.getAngle(partialTicks)));
            transforms.translate(-0.5, 0, -0.5);

            // Fetch tessellator and start drawing
            ITessellator tessellator = this.getTessellator((MultiBufferSource.BufferSource) buffer);
            tessellator.startDrawingQuads();

            // Configure tessellator
            tessellator.pushMatrix();
            tessellator.applyTransformation(transforms.last().pose());
            tessellator.setBrightness(light).setOverlay(overlay);

            // Draw
            this.drawGeometry(tessellator);

            // End drawing
            tessellator.popMatrix().draw();
            transforms.popPose();
        }
    }

    protected void drawGeometry(ITessellator tessellator) {
        tessellator.drawScaledPrism(6.5F, 4.0F, 6.5F, 9.5F, 15.0F, 9.5F, this.getSprite());
        tessellator.drawScaledPrism(7.5F, 5.0F, 3.5F, 8.5F, 6.0F, 12.5F, this.getSprite());
        tessellator.drawScaledPrism(3.5F, 5.0F, 7.5F, 12.5F, 6.0F, 8.5F, this.getSprite());
    }

    protected TextureAtlasSprite getSprite() {
        if(this.sprite == null) {
            this.sprite = this.getSprite(TEXTURE);
        }
        return sprite;
    }

    protected ITessellator getTessellator(MultiBufferSource.BufferSource buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    protected RenderType getRenderType() {
        return RenderType.solid();
    }
}
