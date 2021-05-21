package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
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
        float level = tile.getRenderLevel(partialTicks) - tile.getPos().getY();
        float min = tile.getMinLevel() - tile.getPos().getY();
        float max = tile.getMaxLevel() - tile.getPos().getY();

        ITessellator tessellator = this.getTessellator(buffer);
        this.applyWaterColor(tile, tessellator.startDrawingQuads()).setBrightness(light).setOverlay(overlay).pushMatrix();
        tessellator.applyTransformation(transforms.getLast().getMatrix());

        if(tile.getContent() > 0) {
            tessellator.drawScaledFace(6, 6, 10, 10, Direction.UP, this.getWaterTexture(), Constants.WHOLE * level);
        }

        this.drawConnectionNorth(tessellator, tile, level, min, max, partialTicks);
        this.drawConnectionEast(tessellator, tile, level, min, max, partialTicks);
        this.drawConnectionSouth(tessellator, tile, level, min, max, partialTicks);
        this.drawConnectionWest(tessellator, tile, level, min, max, partialTicks);

        tessellator.popMatrix().draw();
    }

    protected void drawConnectionNorth(ITessellator tessellator, TileEntityIrrigationChannel tile, float y1, float min, float max,
                                       float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.NORTH);
        if(component == null) {
            return;
        }
        float y2 = this.getConnectionLevel(component, y1, min, max, partialTicks);

        float minX = 6;
        float maxX = 10;
        float minZ = 0;
        float maxZ = 6;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionEast(ITessellator tessellator, TileEntityIrrigationChannel tile, float y1, float min, float max, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.EAST);
        if(component == null) {
            return;
        }
        float y2 = this.getConnectionLevel(component, y1, min, max, partialTicks);

        float minX = 10;
        float maxX = 16;
        float minZ = 6;
        float maxZ = 10;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionSouth(ITessellator tessellator, TileEntityIrrigationChannel tile, float y1, float min, float max, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.SOUTH);
        if(component == null) {
            return;
        }
        float y2 = this.getConnectionLevel(component, y1, min, max, partialTicks);

        float minX = 6;
        float maxX = 10;
        float minZ = 10;
        float maxZ = 16;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionWest(ITessellator tessellator, TileEntityIrrigationChannel tile, float y1, float min, float max, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.WEST);
        if(component == null) {
            return;
        }
        float y2 = this.getConnectionLevel(component, y1, min, max, partialTicks);

        float minX = 0;
        float maxX = 6;
        float minZ = 6;
        float maxZ = 10;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected float getConnectionLevel(TileEntityIrrigationComponent component, float ownLevel, float minLevel, float maxLevel, float partialTicks) {
        float level = component.getRenderLevel(partialTicks) - component.getPos().getY();
        if(component instanceof TileEntityIrrigationChannel) {
            return (ownLevel + level)/2;
        } else if(component instanceof TileEntityIrrigationTank) {
            if(level < minLevel) {
                return minLevel;
            } else if(level > maxLevel) {
                return maxLevel;
            } else {
                return level;
            }
        } else {
            return ownLevel;
        }
    }
}
