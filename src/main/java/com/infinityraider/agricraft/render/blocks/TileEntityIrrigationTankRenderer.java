package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityIrrigationTankRenderer extends TileEntityIrrigationComponentRenderer<TileEntityIrrigationTank> {
    @Override
    protected void renderWater(TileEntityIrrigationTank tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {

    }
}
