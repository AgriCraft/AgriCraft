package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.content.world.BlockGreenHouseAir;
import com.infinityraider.agricraft.util.debug.DebugModeGreenHouse;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
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
    public void render(RenderLevelLastEvent event) {
        Player player = AgriCraft.instance.getClientPlayer();
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() != AgriItemRegistry.debugger) {
            return;
        }
        if(AgriItemRegistry.debugger.getDebugMode(stack) instanceof DebugModeGreenHouse) {
            this.highlightGreenHouseAirBlocks(player.getLevel(), player.blockPosition(), event.getPoseStack());
        }
    }

    protected void highlightGreenHouseAirBlocks(Level world, BlockPos origin, PoseStack transforms) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(this.getRenderType());

        transforms.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);
        Matrix4f matrix4f = transforms.last().pose();

        BlockPos.MutableBlockPos pos = origin.mutable();
        for(int x = -RANGE; x <= RANGE; x++) {
            for(int y = -RANGE; y <= RANGE; y++) {
                for(int z = -RANGE; z <= RANGE; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if(world.getBlockState(pos).getBlock() instanceof BlockGreenHouseAir) {
                        this.renderWireFrameCube(builder, matrix4f, pos);
                    }
                }
            }
        }

        transforms.popPose();
        buffer.endBatch(this.getRenderType());
    }

    protected void renderWireFrameCube(VertexConsumer builder, Matrix4f transforms, BlockPos pos) {
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

    protected void drawLine(VertexConsumer builder, Matrix4f transforms, float x1, float y1, float z1, float x2, float y2, float z2) {
        builder.vertex(transforms, x1, y1, z1)
                .normal(x2, y2, z2)
                .color(0.0F, 1.0F, 0.0F, 1.0F)
                .endVertex();
    }

    protected RenderType getRenderType() {
        return RenderType.lineStrip();
    }
}
