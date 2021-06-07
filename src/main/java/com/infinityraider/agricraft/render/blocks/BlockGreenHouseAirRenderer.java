package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.BlockGreenHouseAir;
import com.infinityraider.agricraft.util.debug.DebugModeGreenHouse;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class BlockGreenHouseAirRenderer {
    private static final BlockGreenHouseAirRenderer INSTANCE = new BlockGreenHouseAirRenderer();

    private static final int RANGE = 32;
    private static final float CUBE_SIZE = Constants.UNIT*6;

    private static final float P1 = (1 - CUBE_SIZE)/2;
    private static final float P2 = P1 + CUBE_SIZE;

    public static BlockGreenHouseAirRenderer getInstance() {
        return INSTANCE;
    }

    private BlockGreenHouseAirRenderer() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void render(RenderWorldLastEvent event) {
        PlayerEntity player = AgriCraft.instance.getClientPlayer();
        ItemStack stack = player.getHeldItemMainhand();
        if(stack.getItem() != AgriCraft.instance.getModItemRegistry().debugger) {
            return;
        }
        if(AgriCraft.instance.getModItemRegistry().debugger.getDebugMode(stack) instanceof DebugModeGreenHouse) {
            this.highlightGreenHouseAirBlocks(player.getEntityWorld(), player.getPosition(), event.getMatrixStack());
        }
    }

    protected void highlightGreenHouseAirBlocks(World world, BlockPos origin, MatrixStack transforms) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(this.getRenderType());

        transforms.push();
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);
        Matrix4f matrix4f = transforms.getLast().getMatrix();

        BlockPos.Mutable pos = origin.toMutable();
        for(int x = -RANGE; x <= RANGE; x++) {
            for(int y = -RANGE; y <= RANGE; y++) {
                for(int z = -RANGE; z <= RANGE; z++) {
                    pos.setPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if(world.getBlockState(pos).getBlock() instanceof BlockGreenHouseAir) {
                        this.renderWireFrameCube(builder, matrix4f, pos);
                    }
                }
            }
        }

        transforms.pop();
        buffer.finish(this.getRenderType());
    }

    protected void renderWireFrameCube(IVertexBuilder builder, Matrix4f transforms, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        // Lines along X
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P2, y + P1, z + P1);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P1, x + P2, y + P2, z + P1);
        this.drawLine(builder, transforms, x + P1, y + P1, z + P2, x + P2, y + P1, z + P2);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P2, x + P2, y + P2, z + P2);
        // Lines along y
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P1, y + P2, z + P1);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P1, x + P2, y + P2, z + P1);
        this.drawLine(builder, transforms, x + P1, y + P1, z + P2, x + P1, y + P2, z + P2);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P2, x + P2, y + P2, z + P2);
        // Lines along z
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P1, y + P1, z + P2);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P1, x + P1, y + P2, z + P2);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P1, x + P2, y + P1, z + P2);
        this.drawLine(builder, transforms, x + P2, y + P2, z + P1, x + P2, y + P2, z + P2);
    }

    protected void drawLine(IVertexBuilder builder, Matrix4f transforms, float x1, float y1, float z1, float x2, float y2, float z2) {
        builder.pos(transforms, x1, y1, z1)
                .color(0.0F, 1.0F, 0.0F, 1.0F)
                .endVertex();
        builder.pos(transforms, x2, y2, z2)
                .color(0.0F, 1.0F, 0.0F, 1.0F)
                .endVertex();
    }

    protected RenderType getRenderType() {
        return RenderType.getLines();
    }
}
