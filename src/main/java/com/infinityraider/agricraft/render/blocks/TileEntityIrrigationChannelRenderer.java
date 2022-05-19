package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class TileEntityIrrigationChannelRenderer extends TileEntityIrrigationComponentRenderer<TileEntityIrrigationChannel> implements IRenderUtilities {
    public  static final ResourceLocation MODEL_HANDWHEEL = new ResourceLocation(AgriCraft.instance.getModId(), "block/channel/valve_wheel");
    public  static final ResourceLocation TEXTURE_VALVE = new ResourceLocation("minecraft", "block/iron_block");

    private BakedModel handWheelModel;
    private TextureAtlasSprite valveSprite;

    @Override
    protected void renderWater(TileEntityIrrigationChannel tile, float partialTicks, PoseStack transforms, MultiBufferSource.BufferSource buffer, int light, int overlay) {
        float level = tile.getRenderLevel(partialTicks) - tile.getBlockPos().getY();
        float min = tile.getMinLevel() - tile.getBlockPos().getY();
        level = level == 0 ? min : level;

        ITessellator tessellator = this.getTessellator(buffer);
        this.applyWaterColor(tile, tessellator.startDrawingQuads()).setBrightness(light).setOverlay(overlay).pushMatrix();
        tessellator.applyTransformation(transforms.last().pose());

        if(tile.getContent() > 0 && tile.isOpen()) {
            tessellator.drawScaledFace(6, 6, 10, 10, Direction.UP, this.getWaterTexture(), Constants.WHOLE * level);
        }

        this.drawConnectionNorth(tessellator, tile, partialTicks);
        this.drawConnectionEast(tessellator, tile, partialTicks);
        this.drawConnectionSouth(tessellator, tile, partialTicks);
        this.drawConnectionWest(tessellator, tile, partialTicks);

        if(tile.hasValve() && tile.hasHandWheel()) {
            tessellator.popMatrix().pushMatrix();
            this.renderValveHandWheel(tessellator, tile, transforms, partialTicks);
        }

        tessellator.popMatrix().draw();
    }

    protected void drawConnectionNorth(ITessellator tessellator, TileEntityIrrigationChannel tile, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.NORTH);
        if(component == null) {
            return;
        }
        float y1 = tile.getRenderLevel(partialTicks) - tile.getBlockPos().getY();
        float y2 = this.getConnectionLevel(tile, component, partialTicks);
        if(dontRenderConnection(tile, component, y2)) {
            return;
        }
        if(tile.isClosed()) {
            y1 = y2;
        }

        float minX = 6;
        float maxX = 10;
        float minZ = 0;
        float maxZ = 6;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionEast(ITessellator tessellator, TileEntityIrrigationChannel tile, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.EAST);
        if(component == null) {
            return;
        }
        float y1 = tile.getRenderLevel(partialTicks) - tile.getBlockPos().getY();
        float y2 = this.getConnectionLevel(tile, component, partialTicks);
        if(dontRenderConnection(tile, component, y2)) {
            return;
        }
        if(tile.isClosed()) {
            y1 = y2;
        }

        float minX = 10;
        float maxX = 16;
        float minZ = 6;
        float maxZ = 10;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionSouth(ITessellator tessellator, TileEntityIrrigationChannel tile, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.SOUTH);
        if(component == null) {
            return;
        }
        float y1 = tile.getRenderLevel(partialTicks) - tile.getBlockPos().getY();
        float y2 = this.getConnectionLevel(tile, component, partialTicks);
        if(dontRenderConnection(tile, component, y2)) {
            return;
        }
        if(tile.isClosed()) {
            y1 = y2;
        }

        float minX = 6;
        float maxX = 10;
        float minZ = 10;
        float maxZ = 16;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected void drawConnectionWest(ITessellator tessellator, TileEntityIrrigationChannel tile, float partialTicks) {
        TileEntityIrrigationComponent component = tile.getNeighbour(Direction.WEST);
        if(component == null) {
            return;
        }
        float y1 = tile.getRenderLevel(partialTicks) - tile.getBlockPos().getY();
        float y2 = this.getConnectionLevel(tile, component, partialTicks);
        if(dontRenderConnection(tile, component, y2)) {
            return;
        }
        if(tile.isClosed()) {
            y1 = y2;
        }

        float minX = 0;
        float maxX = 6;
        float minZ = 6;
        float maxZ = 10;

        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, minZ, this.getWaterTexture(), maxX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, minZ, this.getWaterTexture(), minX, minZ);
        tessellator.addScaledVertexWithUV(minX, Constants.WHOLE*y2, maxZ, this.getWaterTexture(), minX, maxZ);
        tessellator.addScaledVertexWithUV(maxX, Constants.WHOLE*y1, maxZ, this.getWaterTexture(), maxX, maxZ);
    }

    protected boolean dontRenderConnection(TileEntityIrrigationChannel self, TileEntityIrrigationComponent other, float otherLevel) {
        if(self.getContent() == 0) {
            if(other.getContent() == 0) {
                return true;
            }
            if(other instanceof TileEntityIrrigationChannel) {
                return ((TileEntityIrrigationChannel) other).isClosed();
            }
            return other instanceof TileEntityIrrigationTank && otherLevel < other.getMinLevel();
        }
        return self.isClosed() && other.getContent() == 0;
    }

    protected float getConnectionLevel(TileEntityIrrigationChannel self, TileEntityIrrigationComponent other, float partialTicks) {
        float ownLevel = self.getRenderLevel(partialTicks) - self.getBlockPos().getY();
        float level = other.getRenderLevel(partialTicks) - other.getBlockPos().getY();
        float minLevel = self.getMinLevel() - self.getBlockPos().getY();
        float maxLevel = self.getMaxLevel() - self.getBlockPos().getY();
        if(other instanceof TileEntityIrrigationChannel) {
            if(((TileEntityIrrigationChannel) other).isOpen()) {
                return (ownLevel + level) / 2;
            } else {
                return self.isOpen() ? ownLevel : (ownLevel + level) / 2;
            }
        } else if(other instanceof TileEntityIrrigationTank) {
            return Math.max(minLevel, Math.min(level, maxLevel));
        } else {
            return ownLevel;
        }
    }

    protected void renderValveHandWheel(ITessellator tessellator, TileEntityIrrigationChannel channel, PoseStack transforms, float partialTicks) {
        // fetch animation state
        float f = channel.getValveAnimationProgress(partialTicks);
        float minY = Mth.lerp(f, 6, 9);
        float maxY = Mth.lerp(f, 10, 13);

        // draw valve block
        tessellator.setColorRGBA(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.applyTransformation(transforms.last().pose());
        tessellator.pushMatrix();
        tessellator.drawScaledPrism(6, minY, 6, 10, maxY, 10, this.getValveSprite());

        // draw hand wheel
        tessellator.pushMatrix();
        tessellator.translate(0.5F, 0, 0.5F);
        tessellator.rotate(Vector3f.YP.rotationDegrees(Mth.lerp(f, 0, 180)));
        tessellator.translate(-0.5F, 0, -0.5F);
        tessellator.addQuads(this.getHandWheelModel().getQuads(null, null, channel.getRandom(), EmptyModelData.INSTANCE));
        tessellator.popMatrix();

        tessellator.popMatrix();
    }

    protected BakedModel getHandWheelModel() {
        if(this.handWheelModel == null) {
            this.handWheelModel = this.getModelManager().getModel(MODEL_HANDWHEEL);
        }
        return this.handWheelModel;
    }

    protected TextureAtlasSprite getValveSprite() {
        if(this.valveSprite == null) {
            this.valveSprite = this.getSprite(TEXTURE_VALVE);
        }
        return this.valveSprite;
    }
}
