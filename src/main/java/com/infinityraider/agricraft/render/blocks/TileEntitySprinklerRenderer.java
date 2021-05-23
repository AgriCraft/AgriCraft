package com.infinityraider.agricraft.render.blocks;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.content.irrigation.TileEntitySprinkler;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class TileEntitySprinklerRenderer implements ITileRenderer<TileEntitySprinkler>, IRenderUtilities {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:block/iron_block");

    private final Map<IRenderTypeBuffer.Impl, ThreadLocal<ITessellator>> tessellators;
    private TextureAtlasSprite sprite;

    public TileEntitySprinklerRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    @Override
    public void render(TileEntitySprinkler tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        if(buffer instanceof IRenderTypeBuffer.Impl) {
            // Apply transformation
            transforms.push();
            transforms.translate(0.5, 0, 0.5);
            transforms.rotate(Vector3f.YP.rotationDegrees(tile.getAngle(partialTicks)));
            transforms.translate(-0.5, 0, -0.5);

            // Fetch tessellator and start drawing
            ITessellator tessellator = this.getTessellator((IRenderTypeBuffer.Impl) buffer);
            tessellator.startDrawingQuads();

            // Configure tessellator
            tessellator.pushMatrix();
            tessellator.applyTransformation(transforms.getLast().getMatrix());
            tessellator.setBrightness(light).setOverlay(overlay);

            // Draw
            this.drawGeometry(tessellator);

            // End drawing
            tessellator.popMatrix().draw();
            transforms.pop();
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

    protected ITessellator getTessellator(IRenderTypeBuffer.Impl buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    protected RenderType getRenderType() {
        return RenderType.getSolid();
    }
}
