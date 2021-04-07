package com.infinityraider.agricraft.render.world;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.content.core.ItemDebugger;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.OptionalDouble;

@OnlyIn(Dist.CLIENT)
public class IrrigationNetworkDebugRenderer {
    private static final IrrigationNetworkDebugRenderer INSTANCE = new IrrigationNetworkDebugRenderer();

    private static final int RANGE = 16;
    private static final float MID = 0.5F;
    private static final float MIN = 0.01F;
    private static final float MAX = 1.00F - MIN;

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
        // Fetch component position
        BlockPos pos = component.getTile().getPos();
        // Stream all directions
        Arrays.stream(Direction.values()).forEach(dir ->
                // Check if the component contains a node for the direction
                component.getNode(dir).ifPresent(node -> {
                    // Fetch the network for the node
                    IAgriIrrigationNetwork network = component.getNetwork(dir);
                    // Check if the node has a connection in the network for the current direction
                    boolean connection = network.getConnectionsFrom(node).stream().anyMatch(con -> con.direction() == dir);
                    // Render the debug lines based on the network type
                    if (network.isValid()) {
                        if (network instanceof IrrigationNetwork) {
                            // Multi-component network: green
                            this.drawSideGreen(builder, matrix, pos, dir, connection);
                        } else {
                            // Single component network: blue
                            this.drawSideBlue(builder, matrix, pos, dir, connection);
                        }
                    } else {
                        // Invalid network: red
                        this.drawSideRed(builder, matrix, pos, dir, connection);
                    }
                }));
    }

    protected void drawSideRed(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, Direction side, boolean connection) {
        this.drawSide(builder, matrix, offset, side, connection, 1.0F, 0.0F, 0.0F);
    }

    protected void drawSideGreen(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, Direction side, boolean connection) {
        this.drawSide(builder, matrix, offset, side, connection, 0.0F, 1.0F, 0.0F);
    }

    protected void drawSideBlue(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, Direction side, boolean connection) {
        this.drawSide(builder, matrix, offset, side, connection, 0.0F, 0.0F, 1.0F);
    }

    protected void drawSide(IVertexBuilder builder, Matrix4f matrix, BlockPos offset, Direction side, boolean connection, float r, float g, float b) {
        switch (side) {
            case DOWN:
                // Draw bottom face
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MAX, MIN, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MAX, MAX, MIN, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MIN, MIN, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MIN, MIN, MAX, MIN, MAX, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MID, MIN, MID, r, g, b);
                }
                //break
                break;
            case UP:
                // Draw top face
                this.drawLine(builder, matrix, offset, MIN, MAX, MIN, MAX, MAX, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MAX, MAX, MAX, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MAX, MIN, MIN, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MAX, MIN, MAX, MAX, MAX, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MID, MAX, MID, r, g, b);
                }
                //break
                break;
            case NORTH:
                // Draw north face
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MAX, MIN, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MAX, MIN, MAX, MAX, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MIN, MAX, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MIN, MIN, MAX, MAX, MIN, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MID, MID, MIN, r, g, b);
                }
                //break
                break;
            case SOUTH:
                // Draw south face
                this.drawLine(builder, matrix, offset, MIN, MIN, MAX, MAX, MIN, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MAX, MAX, MAX, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MAX, MIN, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MIN, MAX, MAX, MAX, MAX, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MID, MID, MAX, r, g, b);
                }
                //break
                break;
            case WEST:
                // Draw west face
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MIN, MAX, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MAX, MIN, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MIN, MIN, MIN, MIN, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MIN, MAX, MIN, MIN, MAX, MAX, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MIN, MID, MID, r, g, b);
                }
                //break
                break;
            case EAST:
                // Draw east face
                this.drawLine(builder, matrix, offset, MAX, MIN, MIN, MAX, MAX, MIN, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MIN, MAX, MAX, MAX, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MIN, MIN, MAX, MIN, MAX, r, g, b);
                this.drawLine(builder, matrix, offset, MAX, MAX, MIN, MAX, MAX, MAX, r, g, b);
                // Draw line from center
                if (connection) {
                    this.drawLine(builder, matrix, offset, MID, MID, MID, MAX, MID, MID, r, g, b);
                }
                //break
                break;
        }
    }

    protected void drawLine(IVertexBuilder builder, Matrix4f matrix, BlockPos offset,
                            float x1, float y1, float z1, float x2, float y2, float z2,
                            float r, float g, float b) {
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

        private static final RenderType INSTANCE = makeType("overlay_lines",
                DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                RenderType.State.getBuilder().line(new LineState(OptionalDouble.of(3.0D)))
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
