package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityIrrigationChannelRenderer extends TileEntityIrrigationComponentRenderer<TileEntityIrrigationChannel> {
    @Override
    protected void renderWater(TileEntityIrrigationChannel tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {

    }
}
