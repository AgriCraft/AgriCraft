package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.content.world.greenhouse.BlockGreenHouseAir;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseState;
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
import net.minecraft.world.level.block.state.BlockState;
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
        if(stack.getItem() != AgriItemRegistry.getInstance().debugger.get()) {
            return;
        }
        if(AgriItemRegistry.getInstance().debugger.get().getDebugMode(stack) instanceof DebugModeGreenHouse) {
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
                    BlockState air = world.getBlockState(pos);
                    if(air.getBlock() instanceof BlockGreenHouseAir) {
                        GreenHouseState state = BlockGreenHouseAir.getState(air);
                        this.renderWireFrameCube(builder, matrix4f, pos, state);
                    }
                }
            }
        }

        transforms.popPose();
        buffer.endBatch(this.getRenderType());
    }

    protected void renderWireFrameCube(VertexConsumer builder, Matrix4f transforms, BlockPos pos, GreenHouseState state) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        // Lines along X
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P2, y + P1, z + P1, state);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P1, x + P2, y + P2, z + P1, state);
        this.drawLine(builder, transforms, x + P1, y + P1, z + P2, x + P2, y + P1, z + P2, state);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P2, x + P2, y + P2, z + P2, state);
        // Lines along y
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P1, y + P2, z + P1, state);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P1, x + P2, y + P2, z + P1, state);
        this.drawLine(builder, transforms, x + P1, y + P1, z + P2, x + P1, y + P2, z + P2, state);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P2, x + P2, y + P2, z + P2, state);
        // Lines along z
        this.drawLine(builder, transforms, x + P1, y + P1, z + P1, x + P1, y + P1, z + P2, state);
        this.drawLine(builder, transforms, x + P1, y + P2, z + P1, x + P1, y + P2, z + P2, state);
        this.drawLine(builder, transforms, x + P2, y + P1, z + P1, x + P2, y + P1, z + P2, state);
        this.drawLine(builder, transforms, x + P2, y + P2, z + P1, x + P2, y + P2, z + P2, state);
    }

    protected void drawLine(VertexConsumer builder, Matrix4f transforms, float x1, float y1, float z1, float x2, float y2, float z2, GreenHouseState state) {
        builder.vertex(transforms, x1, y1, z1)
                .color(state.getRed(), state.getGreen(), state.getBlue(), 1.0F)
                .normal(x2 - x1, y2 - y1, z2 - z1)
                .endVertex();
        builder.vertex(transforms, x2, y2, z2)
                .color(state.getRed(), state.getGreen(), state.getBlue(), 1.0F)
                .normal(x2 - x1, y2 - y1, z2 - z1)
                .endVertex();
    }

    protected RenderType getRenderType() {
        return RenderType.lines();
    }
}
