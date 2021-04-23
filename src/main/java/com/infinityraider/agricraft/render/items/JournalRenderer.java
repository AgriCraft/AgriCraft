package com.infinityraider.agricraft.render.items;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityJournalReader;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class JournalRenderer implements InfItemRenderer, IRenderUtilities {
    private static final JournalRenderer INSTANCE = new JournalRenderer();

    private static final Vector3f COLOR_COVER = new Vector3f(0, 2.0F/255.0F, 165.0F/255.0F);
    private static final Vector3f COLOR_PAGE = new Vector3f(189.0F/255.0F, 194.0F/255.0F, 175.0F/255.0F);

    private static final float HEIGHT = 7.0F;
    private static final float WIDTH = 5.0F;
    private static final float T_COVER = 0.5F;
    private static final float T_PAPER = 1.0F;
    private static final float T_TOTAL = 2*T_COVER + T_PAPER;

    public static JournalRenderer getInstance() {
        return INSTANCE;
    }

    private final Map<IRenderTypeBuffer.Impl, ThreadLocal<ITessellator>> tessellators;

    private JournalRenderer() {
        this.tessellators = Maps.newConcurrentMap();
    }

    @Override
    public void render(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                       IRenderTypeBuffer buffer, int light, int overlay) {
        PlayerEntity player = AgriCraft.instance.getClientPlayer();
        if(this.renderFull3D(perspective)) {
            if(buffer instanceof IRenderTypeBuffer.Impl) {
                if (CapabilityJournalReader.getInstance().isReading(stack, player)) {
                    this.renderJournalOpen(stack, perspective, transforms, (IRenderTypeBuffer.Impl) buffer, light, overlay);
                } else {
                    this.renderJournalClosed(perspective, transforms, (IRenderTypeBuffer.Impl) buffer, light, overlay);
                }
            } else {
                this.renderFlat(stack, perspective, transforms, buffer, light, overlay);
            }
        } else {
            this.renderFlat(stack, perspective, transforms, buffer, light, overlay);
        }
    }

    protected void renderFlat(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                              IRenderTypeBuffer buffer, int light, int overlay) {
        this.renderItem(stack, perspective, light, overlay, transforms, buffer);
    }

    protected void renderJournalClosed(ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                                       IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        // Apply transformations to the stack
        transforms.push();
        this.applyTransformations(perspective, transforms);

        this.renderCoordinateSystem(transforms, buffer);

        // Fetch tessellator and start drawing
        ITessellator tessellator = this.getTessellator(buffer);
        tessellator.startDrawingQuads();

        // Configure tessellator
        tessellator.pushMatrix();
        tessellator.applyTransformation(transforms.getLast().getMatrix());
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
        transforms.pop();
    }

    protected void renderJournalOpen(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                                     IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        // Apply transformations to the stack
        transforms.push();
        this.applyTransformations(perspective, transforms);
        float f = JournalViewPointHandler.getInstance().getOpeningProgress(this.getPartialTick());

        // Fetch tessellator and start drawing
        ITessellator tessellator = this.getTessellator(buffer);
        tessellator.startDrawingQuads();

        // Configure tessellator
        tessellator.applyTransformation(transforms.getLast().getMatrix());
        tessellator.setBrightness(light).setOverlay(overlay);

        // Draw book back
        transforms.push();
        tessellator.pushMatrix();
        transforms.translate(-1.0F*f/32.0F, 0, 0);

        tessellator.applyTransformation(transforms.getLast().getMatrix())
                .setColorRGB(COLOR_COVER).
                drawScaledPrism(0, - T_TOTAL/2, -HEIGHT, 0.5F, T_TOTAL/2, 0);

        tessellator.popMatrix();
        transforms.pop();

        // Draw left side
        transforms.push();
        tessellator.pushMatrix();

        transforms.translate(1.0F/32.0F, 0, 0);
        transforms.rotate(Vector3f.ZP.rotationDegrees(-60*f));
        transforms.translate(-1.0F/32.0F, 0, 0);

        this.renderCoordinateSystem(transforms, buffer);

        tessellator.applyTransformation(transforms.getLast().getMatrix());
        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, -T_TOTAL/2, -HEIGHT, WIDTH, T_COVER - T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_PAGE)
                .drawScaledPrism(0.5F, T_COVER - T_TOTAL/2 , -HEIGHT + 0.5F, WIDTH - 0.5F, T_COVER + T_PAPER/2 - T_TOTAL/2, -0.5F);

        tessellator.popMatrix();
        transforms.pop();

        //Draw right side
        transforms.push();
        tessellator.pushMatrix();

        transforms.translate(1.0F/32.0F, 0, 0);
        transforms.rotate(Vector3f.ZP.rotationDegrees(60*f));
        transforms.translate(-1.0F/32.0F, 0, 0);

        tessellator.applyTransformation(transforms.getLast().getMatrix());
        tessellator.setColorRGB(COLOR_COVER)
                .drawScaledPrism(0, T_COVER + T_PAPER - T_TOTAL/2, -HEIGHT, WIDTH, T_TOTAL/2, 0);
        tessellator.setColorRGB(COLOR_PAGE)
                .drawScaledPrism(0.5F, T_COVER + T_PAPER/2 - T_TOTAL/2, -HEIGHT + 0.5F, WIDTH - 0.5F, T_COVER + T_PAPER - T_TOTAL/2, -0.5F);

        tessellator.popMatrix();
        transforms.pop();

        // Finalize drawing
        tessellator.draw();
        transforms.pop();
    }

    protected void applyTransformations(ItemCameraTransforms.TransformType perspective, MatrixStack transforms) {
        transforms.translate(0.5, 0.5, 7.0F/16F);
        if(!perspective.isFirstPerson()) {
            transforms.translate(-WIDTH/32, T_TOTAL/32, 0);
            transforms.rotate(Vector3f.XN.rotationDegrees(-90));
        }
    }

    protected boolean renderFull3D(ItemCameraTransforms.TransformType perspective) {
        return perspective == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || perspective == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || perspective == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || perspective == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    protected ITessellator getTessellator(IRenderTypeBuffer.Impl buffer) {
        return this.tessellators.computeIfAbsent(buffer, aBuffer -> ThreadLocal.withInitial(
                () -> this.getVertexBufferTessellator(aBuffer, this.getRenderType()))).get();
    }

    protected RenderType getRenderType() {
        return PosColorRenderType.INSTANCE;
    }

    private static class PosColorRenderType extends RenderType {
        // Need to have a constructor...
        public PosColorRenderType(String name, VertexFormat format, int i1, int i2, boolean b1, boolean b2, Runnable runnablePre, Runnable runnablePost) {
            super(name, format, i1, i2, b1, b2, runnablePre, runnablePost);
        }

        private static final RenderType INSTANCE = makeType("colored_quads",
                DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 256,
                RenderType.State.getBuilder()
                        .shadeModel(SHADE_ENABLED)
                        .lightmap(LIGHTMAP_ENABLED)
                        .build(false));
    }
}
