package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityIrrigationComponentRenderer<T extends TileEntityIrrigationComponent> implements ITileRenderer<T>, IRenderUtilities {
    @Override
    public void render(T tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        // Render the water
        this.renderWater(tile, partialTicks, transforms, buffer, light, overlay);

    }

    protected abstract void renderWater(T tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay);
}
