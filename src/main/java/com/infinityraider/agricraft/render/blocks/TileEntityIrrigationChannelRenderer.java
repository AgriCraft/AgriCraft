package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityIrrigationChannelRenderer extends TileEntityIrrigationComponentRenderer<TileEntityIrrigationChannel> {
    @Override
    protected void renderWater(TileEntityIrrigationChannel tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        float level = (float) (tile.getLevel() - tile.getPos().getY());

        ITessellator tessellator = this.getTessellator(buffer);
        this.applyWaterColor(tile, tessellator.startDrawingQuads()).setBrightness(light).setOverlay(overlay).pushMatrix();
        tessellator.applyTransformation(transforms.getLast().getMatrix());

        tessellator.drawScaledFace(6, 6, 10, 10, Direction.UP, this.getWaterTexture(), Constants.WHOLE*level);
        // TODO: connections

        tessellator.popMatrix().draw();
    }
}
