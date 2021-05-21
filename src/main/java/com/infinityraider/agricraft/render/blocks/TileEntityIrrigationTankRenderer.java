package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityIrrigationTankRenderer extends TileEntityIrrigationComponentRenderer<TileEntityIrrigationTank> {
    @Override
    protected void renderWater(TileEntityIrrigationTank tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        float level = tile.getRenderLevel(partialTicks) - tile.getPos().getY();
        if(level > 1) {
            return;
        }

        ITessellator tessellator = this.getTessellator(buffer);
        this.applyWaterColor(tile, tessellator.startDrawingQuads()).setBrightness(light).setOverlay(overlay).pushMatrix();
        tessellator.applyTransformation(transforms.getLast().getMatrix());

        tessellator.drawScaledFace(0, 0, 16, 16, Direction.UP, this.getWaterTexture(), Constants.WHOLE*level);

        tessellator.popMatrix().draw();
    }
}
