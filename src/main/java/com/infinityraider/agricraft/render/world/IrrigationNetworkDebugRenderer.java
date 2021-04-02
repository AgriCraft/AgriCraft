package com.infinityraider.agricraft.render.world;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.content.core.ItemDebugger;
import com.infinityraider.agricraft.util.debug.DebugModeIrrigationNetwork;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

@OnlyIn(Dist.CLIENT)
public class IrrigationNetworkDebugRenderer {
    private static final IrrigationNetworkDebugRenderer INSTANCE = new IrrigationNetworkDebugRenderer();

    private static final int RANGE = 16;

    public static IrrigationNetworkDebugRenderer getInstance() {
        return INSTANCE;
    }

    private IrrigationNetworkDebugRenderer() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRenderWorldEvent(RenderWorldLastEvent event) {
        if(AgriCraft.instance.getConfig().debugMode()) {
            PlayerEntity player = Minecraft.getInstance().player;
            ItemDebugger debugger = AgriCraft.instance.getModItemRegistry().debugger;
            if (player != null && player.getHeldItemMainhand().getItem() == debugger) {
                if (debugger.getDebugMode(player.getHeldItemMainhand()) instanceof DebugModeIrrigationNetwork) {
                    this.renderIrrigationNetworkDebug(player, event.getMatrixStack());
                }
            }
        }
    }

    protected void renderIrrigationNetworkDebug(PlayerEntity player, MatrixStack transforms) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(this.getRenderType());

        BlockPos zero = player.getPosition();
        World world = player.getEntityWorld();

        transforms.push();

        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dy = -RANGE; dy <= RANGE; dy++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    pos.setPos(zero.getX() + dx, zero.getY() + dy, zero.getZ() + dz);
                    TileEntity tile = world.getTileEntity(pos);
                    CapabilityIrrigationComponent.getInstance().getIrrigationComponent(tile).ifPresent(component ->
                            this.renderIrrigationComponentDebug(component, builder, transforms.getLast().getMatrix()));
                }
            }
        }

        transforms.pop();

        RenderSystem.disableDepthTest();
        buffer.finish(this.getRenderType());
    }

    protected void renderIrrigationComponentDebug(IAgriIrrigationComponent component, IVertexBuilder builder, Matrix4f matrix) {
        BlockPos pos = component.getTile().getPos();
        // TODO
    }

    protected void drawCubeRed(IVertexBuilder builder, Matrix4f matrix, BlockPos pos) {
        drawLineRed(builder, matrix, pos, 0, 0, 0, 1, 0, 0);
        drawLineRed(builder, matrix, pos, 0, 1, 0, 1, 1, 0);
        drawLineRed(builder, matrix, pos, 0, 0, 1, 1, 0, 1);
        drawLineRed(builder, matrix, pos, 0, 1, 1, 1, 1, 1);

        drawLineRed(builder, matrix, pos, 0, 0, 0, 0, 0, 1);
        drawLineRed(builder, matrix, pos, 1, 0, 0, 1, 0, 1);
        drawLineRed(builder, matrix, pos,  0, 1, 0, 0, 1, 1);
        drawLineRed(builder, matrix, pos, 1, 1, 0, 1, 1, 1);

        drawLineRed(builder, matrix, pos, 0, 0, 0, 0, 1, 0);
        drawLineRed(builder, matrix, pos,  1, 0, 0, 1, 1, 0);
        drawLineRed(builder, matrix, pos, 0, 0, 1, 0, 1, 1);
        drawLineRed(builder, matrix, pos, 1, 0, 1, 1, 1, 1);
    }

    protected void drawCubeGreen(IVertexBuilder builder, Matrix4f matrix, BlockPos pos) {
        drawLineGreen(builder, matrix, pos, 0, 0, 0, 1, 0, 0);
        drawLineGreen(builder, matrix, pos, 0, 1, 0, 1, 1, 0);
        drawLineGreen(builder, matrix, pos, 0, 0, 1, 1, 0, 1);
        drawLineGreen(builder, matrix, pos, 0, 1, 1, 1, 1, 1);

        drawLineGreen(builder, matrix, pos, 0, 0, 0, 0, 0, 1);
        drawLineGreen(builder, matrix, pos, 1, 0, 0, 1, 0, 1);
        drawLineGreen(builder, matrix, pos,  0, 1, 0, 0, 1, 1);
        drawLineGreen(builder, matrix, pos, 1, 1, 0, 1, 1, 1);

        drawLineGreen(builder, matrix, pos, 0, 0, 0, 0, 1, 0);
        drawLineGreen(builder, matrix, pos,  1, 0, 0, 1, 1, 0);
        drawLineGreen(builder, matrix, pos, 0, 0, 1, 0, 1, 1);
        drawLineGreen(builder, matrix, pos, 1, 0, 1, 1, 1, 1);
    }

    protected void drawCubeBlue(IVertexBuilder builder, Matrix4f matrix, BlockPos pos) {
        drawLineBlue(builder, matrix, pos, 0, 0, 0, 1, 0, 0);
        drawLineBlue(builder, matrix, pos, 0, 1, 0, 1, 1, 0);
        drawLineBlue(builder, matrix, pos, 0, 0, 1, 1, 0, 1);
        drawLineBlue(builder, matrix, pos, 0, 1, 1, 1, 1, 1);

        drawLineBlue(builder, matrix, pos, 0, 0, 0, 0, 0, 1);
        drawLineBlue(builder, matrix, pos, 1, 0, 0, 1, 0, 1);
        drawLineBlue(builder, matrix, pos,  0, 1, 0, 0, 1, 1);
        drawLineBlue(builder, matrix, pos, 1, 1, 0, 1, 1, 1);

        drawLineBlue(builder, matrix, pos, 0, 0, 0, 0, 1, 0);
        drawLineBlue(builder, matrix, pos,  1, 0, 0, 1, 1, 0);
        drawLineBlue(builder, matrix, pos, 0, 0, 1, 0, 1, 1);
        drawLineBlue(builder, matrix, pos, 1, 0, 1, 1, 1, 1);
    }

    protected void drawLineRed(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, float x1, float y1, float z1, float x2, float y2, float z2) {
        this.drawLine(builder, matrix, offset, x1, y1, z1, x2, y2, z2, 1.0F, 0, 0);
    }

    protected void drawLineGreen(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, float x1, float y1, float z1, float x2, float y2, float z2) {
        this.drawLine(builder, matrix, offset, x1, y1, z1, x2, y2, z2, 0, 1.0F, 10);
    }

    protected void drawLineBlue(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, float x1, float y1, float z1, float x2, float y2, float z2) {
        this.drawLine(builder, matrix, offset, x1, y1, z1, x2, y2, z2, 0, 0, 1.0F);
    }

    protected void drawLine(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b) {
        builder.pos(matrix, offset.getX() + x1, offset.getY() + y1, offset.getZ() + z1)
                .color(r, g, b, 1.0f)
                .endVertex();
        builder.pos(matrix, offset.getX() + x2, offset.getY() + y2, offset.getZ() + z2)
                .color(r, g, b, 1.0f)
                .endVertex();
    }

    protected RenderType getRenderType() {
        return LineRenderType.INSTANCE;
    }

    private static class LineRenderType extends RenderType {
        // Need to have a constructor...
        public LineRenderType(String name, VertexFormat format, int i1, int i2, boolean b1, boolean b2, Runnable runnablePre, Runnable runnablePost) {
            super(name, format, i1, i2, b1, b2, runnablePre, runnablePost);
        }

        private static final LineState THICK_LINES = new LineState(OptionalDouble.of(3.0D));

        private static final RenderType INSTANCE = makeType("overlay_lines",
                DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                RenderType.State.getBuilder().line(THICK_LINES)
                        .layer(field_239235_M_)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .texture(NO_TEXTURE)
                        .depthTest(DEPTH_ALWAYS)
                        .cull(CULL_DISABLED)
                        .lightmap(LIGHTMAP_DISABLED)
                        .writeMask(COLOR_WRITE)
                        .build(false));
    }
}
