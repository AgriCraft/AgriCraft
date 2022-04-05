package com.infinityraider.agricraft.render.blocks;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityIrrigationComponentRenderer<T extends TileEntityIrrigationComponent> implements ITileRenderer<T>, IRenderUtilities {
    private static TextureAtlasSprite waterTexture;

    private final Map<MultiBufferSource.BufferSource, ThreadLocal<ITessellator>> tessellators;

    protected TileEntityIrrigationComponentRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    @Override
    public void render(T tile, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        // Render the water
        if(buffer instanceof MultiBufferSource.BufferSource) {
            this.renderWater(tile, partialTicks, transforms, (MultiBufferSource.BufferSource) buffer, light, overlay);
        }
    }

    protected abstract void renderWater(T tile, float partialTicks, PoseStack transforms, MultiBufferSource.BufferSource buffer, int light, int overlay);

    protected ITessellator getTessellator(MultiBufferSource.BufferSource buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    protected TextureAtlasSprite getWaterTexture() {
        if(waterTexture == null) {
            waterTexture = this.getSprite(Fluids.WATER.getAttributes().getStillTexture());
        }
        return waterTexture;
    }

    protected ITessellator applyWaterColor(T tile, ITessellator tessellator) {
        int color = tile.getLevel() == null
                ? Fluids.WATER.getAttributes().getColor()
                : Fluids.WATER.getAttributes().getColor(tile.getLevel(), tile.getBlockPos());
        return tessellator.setColorRGB(
                (color >> 16 & 0xFF) / 255.0F,
                (color >> 8 & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F
        );
    }

    protected RenderType getRenderType() {
        return RenderType.translucent();
    }
}
