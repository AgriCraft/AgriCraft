package com.infinityraider.agricraft.render.items.journal;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class JournalItemRenderer implements InfItemRenderer, IRenderUtilities {
    private static final JournalItemRenderer INSTANCE = new JournalItemRenderer();

    private static final Vector3f COLOR_COVER;
    private static final Vector3f COLOR_PAGE;

    private static final Quaternion ROTATION_RIGHT;
    private static final Quaternion ROTATION_LEFT;

    private static final float HEIGHT = 7.0F;
    private static final float WIDTH = 5.0F;
    private static final float T_COVER = 0.5F;
    private static final float T_PAPER = 1.0F;
    private static final float T_TOTAL = 2*T_COVER + T_PAPER;
    private static final float OPEN_ANGLE = 75.0F;

    protected static final float SHADE_LEFT = 0.7F;
    protected static final float SHADE_RIGHT = 1.0F;

    private static final JournalRenderContextInHand RENDERER_LEFT = new JournalRenderContextInHand(WIDTH, HEIGHT, SHADE_LEFT);
    private static final JournalRenderContextInHand RENDERER_RIGHT = new JournalRenderContextInHand(WIDTH, HEIGHT , SHADE_RIGHT);

    public static JournalItemRenderer getInstance() {
        return INSTANCE;
    }

    private final Map<MultiBufferSource.BufferSource, ThreadLocal<ITessellator>> tessellators;

    private JournalItemRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    @Override
    public void render(ItemStack stack, ItemTransforms.TransformType perspective, PoseStack transforms,
                       MultiBufferSource buffer, int light, int overlay) {
        if(stack.getItem() instanceof IAgriJournalItem) {
            IAgriJournalItem journal = (IAgriJournalItem) stack.getItem();
            if (this.renderFull3D(perspective)) {
                if (buffer instanceof MultiBufferSource.BufferSource) {
                    if (JournalViewPointHandler.getInstance().getJournal() == stack) {
                        this.renderJournalOpen(perspective, transforms, stack, journal, (MultiBufferSource.BufferSource) buffer, light, overlay);
                    } else {
                        this.renderJournalClosed(perspective, transforms, (MultiBufferSource.BufferSource) buffer, light, overlay);
                    }
                } else {
                    this.renderFlat(stack, perspective, transforms, buffer, light, overlay);
                }
            } else {
                this.renderFlat(stack, perspective, transforms, buffer, light, overlay);
            }
        }
    }

    protected void renderFlat(ItemStack stack, ItemTransforms.TransformType perspective, PoseStack transforms,
                              MultiBufferSource buffer, int light, int overlay) {
        this.renderItem(stack, perspective, light, overlay, transforms, buffer);
    }

    protected void renderJournalClosed(ItemTransforms.TransformType perspective, PoseStack transforms,
                                       MultiBufferSource.BufferSource buffer, int light, int overlay) {
        // Apply transformations to the stack
        transforms.pushPose();
        this.applyTransformations(perspective, transforms);

        // Fetch tessellator and start drawing
        ITessellator tessellator = this.getTessellator(buffer);
        tessellator.startDrawingQuads();

        // Configure tessellator
        tessellator.pushMatrix();
        tessellator.applyTransformation(transforms.last().pose());
        tessellator.setBrightness(light).setOverlay(overlay);

        // Draw Book
        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, -T_TOTAL/2, -HEIGHT, WIDTH, T_COVER - T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_PAGE)
                .drawScaledPrism(0.5F, T_COVER - T_TOTAL/2 , -HEIGHT + 0.5F, WIDTH - 0.5F, T_COVER + T_PAPER - T_TOTAL/2, -0.5F);
        tessellator.setColorRGB(COLOR_COVER).
                drawScaledPrism(0, T_COVER - T_TOTAL/2, -HEIGHT, 0.5F, T_COVER + T_PAPER - T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, T_COVER + T_PAPER - T_TOTAL/2, -HEIGHT, WIDTH, T_TOTAL/2, 0);

        // Finalize drawing
        tessellator.popMatrix();
        tessellator.draw();
        transforms.popPose();
    }

    protected void renderJournalOpen(ItemTransforms.TransformType perspective, PoseStack transforms,
                                     ItemStack stack, IAgriJournalItem journal,
                                     MultiBufferSource.BufferSource buffer, int light, int overlay) {
        // Fetch animation progress
        float openProgress = JournalViewPointHandler.getInstance().getOpeningProgress(this.getPartialTick());
        float flipProgress = JournalViewPointHandler.getInstance().getFlippingProgress(this.getPartialTick());

        // Apply transformations to the stack
        transforms.pushPose();
        this.applyTransformations(perspective, transforms);
        if(!perspective.firstPerson()) {
            transforms.mulPose(Vector3f.ZP.rotationDegrees(90*openProgress));
        }

        // Fetch tessellator
        ITessellator tessellator = this.getTessellator(buffer);

        // Draw book back
        this.drawOpenBack(tessellator, transforms, openProgress, light, overlay);

        // Draw book right side
        this.drawOpenRight(tessellator, transforms, stack, journal, openProgress, flipProgress, light, overlay);

        //Draw left side
        this.drawOpenLeft(tessellator, transforms, stack, journal, openProgress, light, overlay);

        // Draw page flip
        this.drawOpenFlipped(tessellator, transforms, stack, journal, flipProgress, light, overlay);

        // Pop transformations from the stack
        transforms.popPose();
    }

    protected void applyTransformations(ItemTransforms.TransformType perspective, PoseStack transforms) {
        transforms.translate(0.5F, 0.425F, 5.0F/16F);
        if(!perspective.firstPerson()) {
            transforms.translate(-WIDTH/32, T_TOTAL/32, 0.25F);
            transforms.mulPose(Vector3f.XN.rotationDegrees(-90));
        }
    }

    protected void drawOpenBack(ITessellator tessellator, PoseStack transforms, float openProgress, int light, int overlay) {
        tessellator.startDrawingQuads().setBrightness(light).setOverlay(overlay).pushMatrix();
        transforms.pushPose();

        transforms.translate(-openProgress/32.0F, 0, 0);
        tessellator.applyTransformation(transforms.last().pose());

        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, - T_TOTAL/2, -HEIGHT, 0.5F, T_TOTAL/2, 0);

        transforms.popPose();
        tessellator.popMatrix().draw();
    }

    protected void drawOpenRight(ITessellator tessellator, PoseStack transforms, ItemStack stack, IAgriJournalItem journal,
                                 float openProgress, float flipProgress, int light, int overlay) {
        tessellator.startDrawingQuads().setBrightness(light).setOverlay(overlay).pushMatrix();
        transforms.pushPose();

        transforms.translate(1.0F/32.0F, 0, 0);
        transforms.mulPose(Vector3f.ZP.rotationDegrees(-openProgress*OPEN_ANGLE));
        transforms.translate(-1.0F/32.0F, 0, 0);
        tessellator.applyTransformation(transforms.last().pose());

        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, -T_TOTAL/2, -HEIGHT, WIDTH, T_COVER - T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_PAGE)
                .drawScaledPrism(0.5F, T_COVER - T_TOTAL/2 , -HEIGHT + 0.5F, WIDTH - 0.5F, T_COVER + T_PAPER/2 - T_TOTAL/2, -0.5F);

        tessellator.popMatrix().draw();

        if(openProgress > 0) {
            transforms.pushPose();
            transforms.translate(1.0F / 32.0F, (T_COVER + T_PAPER / 2 - T_TOTAL / 2 + 0.00001F) / 16.0F, (-HEIGHT + 0.5F) / 16.0F);
            transforms.mulPose(ROTATION_RIGHT);

            if (flipProgress != 0) {
                JournalViewPointHandler.getInstance().renderFlippedPageRight(RENDERER_RIGHT, transforms, stack, journal);
            } else {
                JournalViewPointHandler.getInstance().renderViewedPageRight(RENDERER_RIGHT, transforms, stack, journal);
            }
            transforms.popPose();
        }

        transforms.popPose();
    }

    protected void drawOpenLeft(ITessellator tessellator, PoseStack transforms, ItemStack stack, IAgriJournalItem journal,
                                float openProgress, int light, int overlay) {
        tessellator.startDrawingQuads().setBrightness(light).setOverlay(overlay).pushMatrix();
        transforms.pushPose();

        transforms.translate(1.0F/32.0F, 0, 0);
        transforms.mulPose(Vector3f.ZP.rotationDegrees(openProgress*OPEN_ANGLE));
        transforms.translate(-1.0F/32.0F, 0, 0);
        tessellator.applyTransformation(transforms.last().pose());

        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, T_COVER + T_PAPER - T_TOTAL/2, -HEIGHT, WIDTH, T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_PAGE)
                .drawScaledPrism(0.5F, T_COVER + T_PAPER/2 - T_TOTAL/2, -HEIGHT + 0.5F, WIDTH - 0.5F, T_COVER + T_PAPER - T_TOTAL/2, -0.5F);

        tessellator.popMatrix().draw();

        if(openProgress > 0) {
            transforms.pushPose();
            transforms.translate((WIDTH - 0.5F) / 16.0F, (T_COVER + T_PAPER / 2 - T_TOTAL / 2 - 0.00001F) / 16.0F, (-HEIGHT + 0.5F) / 16.0F);
            transforms.mulPose(ROTATION_LEFT);

            JournalViewPointHandler.getInstance().renderViewedPageLeft(RENDERER_LEFT, transforms, stack, journal);

            transforms.popPose();
        }

        transforms.popPose();
    }

    protected void drawOpenFlipped(ITessellator tessellator, PoseStack transforms, ItemStack stack, IAgriJournalItem journal,
                                   float flipProgress, int light, int overlay) {
        if(flipProgress != 0) {
            tessellator.startDrawingQuads().setBrightness(light).setOverlay(overlay).pushMatrix();
            transforms.pushPose();

            transforms.translate(1.0F/32.0F, 0, 0);
            if(flipProgress < 0) {
                transforms.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(-flipProgress, -OPEN_ANGLE, OPEN_ANGLE)));
            } else {
                transforms.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(flipProgress, OPEN_ANGLE, -OPEN_ANGLE)));
            }
            transforms.translate(-1.0F/32.0F, 0, 0);
            tessellator.applyTransformation(transforms.last().pose());

            tessellator.setColorRGB(COLOR_PAGE)
                    .drawScaledFaceDouble(0.5F, -HEIGHT + 0.5F, WIDTH - 0.5F, -0.5F, Direction.UP, 0.0F);

            tessellator.popMatrix().draw();

            transforms.pushPose();
            transforms.translate(1.0F/32.0F, (T_COVER + T_PAPER/2 - T_TOTAL/2 + 0.001F)/16.0F, (-HEIGHT + 0.5F)/16.0F);
            transforms.mulPose(ROTATION_RIGHT);
            JournalViewPointHandler.getInstance().renderViewedPageRight(RENDERER_LEFT, transforms, stack, journal);
            transforms.popPose();

            transforms.pushPose();
            transforms.translate((WIDTH - 0.5F)/16.0F, (T_COVER + T_PAPER/2 - T_TOTAL/2 - 0.001F)/16.0F, (-HEIGHT + 0.5F)/16.0F);
            transforms.mulPose(ROTATION_LEFT);
            JournalViewPointHandler.getInstance().renderFlippedPageLeft(RENDERER_LEFT, transforms, stack, journal);
            transforms.popPose();

            transforms.popPose();
        }

    }

    protected boolean renderFull3D(ItemTransforms.TransformType perspective) {
        return perspective == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || perspective == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || perspective == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || perspective == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    protected ITessellator getTessellator(MultiBufferSource.BufferSource buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    protected RenderType getRenderType() {
        return PosColorRenderType.INSTANCE;
    }

    private static class PosColorRenderType extends RenderType {
        // Need to have a constructor...
        public PosColorRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean crumbling, boolean sorted, Runnable runnablePre, Runnable runnablePost) {
            super(name, format, mode, bufferSize, crumbling, sorted, runnablePre, runnablePost);
        }

        private static final RenderType INSTANCE = create("colored_quads",
                DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(BLOCK_SHADER)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false));
    }

    static {
        // Initialize colors
        COLOR_COVER = new Vector3f(0, 2.0F/255.0F, 165.0F/255.0F);
        COLOR_PAGE = new Vector3f(189.0F/255.0F, 194.0F/255.0F, 175.0F/255.0F);
        // Initialize rotations
        ROTATION_RIGHT = Vector3f.XP.rotationDegrees(90);
        ROTATION_LEFT = Vector3f.XN.rotationDegrees(90);
        ROTATION_LEFT.mul(Vector3f.ZP.rotationDegrees(180));
    }
}
