package com.infinityraider.agricraft.render.items.magnfiyingglass;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.requirement.NoSoil;
import com.infinityraider.agricraft.render.items.journal.JournalDataDrawerBase;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagnifyingGlassSoilInspector implements IMagnifyingGlassInspector, IRenderUtilities {
    public static void init() {
        IMagnifyingGlassInspector.registerInspector(new MagnifyingGlassSoilInspector());
    }

    private static final Quaternion FLIP_Y = Vector3f.ZP.rotationDegrees(180);
    private static final float DELTA_Y = 0.020F;
    private static final float ICON_SCALE = 0.175F*Constants.UNIT*Constants.UNIT;
    private static final float TEXT_SCALE = 0.65F;

    private IAgriSoil cachedSoil;

    protected MagnifyingGlassSoilInspector() {}

    @Override
    public boolean canInspect(World world, BlockPos pos, PlayerEntity player) {
        return AgriApi.getSoil(world, pos).map(IAgriSoil::isSoil).orElse(false);
    }

    @Override
    public boolean canInspect(World world, Entity entity, PlayerEntity player) {
        return false;
    }

    @Override
    public void onInspectionStart(World world, BlockPos pos, PlayerEntity player) {
        this.cachedSoil = AgriApi.getSoil(world, pos).orElse(NoSoil.getInstance());
    }

    @Override
    public void onInspectionStart(World world, Entity entity, PlayerEntity player) {
        // Do nothing
    }

    @Override
    public boolean onInspectionTick(World world, BlockPos pos, PlayerEntity player) {
        return this.cachedSoil != null && this.cachedSoil.isSoil();
    }

    @Override
    public boolean onInspectionTick(World world, Entity entity, PlayerEntity player) {
        return false;
    }

    @Override
    public void onInspectionEnd(World world, BlockPos pos, PlayerEntity player) {
        this.cachedSoil = null;
    }

    @Override
    public void onInspectionEnd(World world, @Nullable Entity entity, PlayerEntity player) {
        // Do nothing
    }

    @Override
    public void onMouseScroll(int delta) {
        // Do nothing
    }

    @Override
    public void doInspectionRender(MatrixStack transforms, float partialTick, @Nullable Entity entity) {
        // Check if soil is still valid
        IAgriSoil soil = this.cachedSoil;
        if(soil == null || !soil.isSoil()) {
            return;
        }

        // Push matrix for render, flip y, and apply y offset
        transforms.push();
        transforms.rotate(FLIP_Y);
        transforms.translate(0, DELTA_Y, 0);

        // Draw the soil property icons
        this.drawSoilPropertyIcons(transforms, soil);

        // Draw the text
        this.drawSoilPropertyText(transforms, soil);

        // Pop the matrix
        transforms.pop();
    }

    protected void drawSoilPropertyIcons(MatrixStack transforms, IAgriSoil soil) {
        // Push the matrix
        transforms.push();

        // Humidity
        transforms.translate(0, -3*DELTA_Y, 0);
        this.drawSoilProperty(transforms, soil.getHumidity(), IAgriSoil.Humidity.values(),
                JournalDataDrawerBase.Textures.HUMIDITY_FILLED, JournalDataDrawerBase.Textures.HUMIDITY_EMPTY, JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS);

        // Acidity
        transforms.translate(0, DELTA_Y, 0);
        this.drawSoilProperty(transforms, soil.getAcidity(), IAgriSoil.Acidity.values(),
                JournalDataDrawerBase.Textures.ACIDITY_FILLED, JournalDataDrawerBase.Textures.ACIDITY_EMPTY, JournalDataDrawerBase.Textures.ACIDITY_OFFSETS);

        // Nutrients
        transforms.translate(0, DELTA_Y, 0);
        this.drawSoilProperty(transforms, soil.getNutrients(), IAgriSoil.Nutrients.values(),
                JournalDataDrawerBase.Textures.NUTRIENTS_FILLED, JournalDataDrawerBase.Textures.NUTRIENTS_EMPTY, JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS);

        // Pop the matrix
        transforms.pop();
    }

    protected void drawSoilPropertyText(MatrixStack transforms, IAgriSoil soil) {
        // Push the matrix
        transforms.push();

        // Scale down
        float width = this.getScaledWindowWidth();
        float height = this.getScaledWindowHeight();
        float scale = TEXT_SCALE/Math.max(width, height);
        transforms.scale(scale, scale, scale);

        // Humidity
        float dy = DELTA_Y/scale;
        transforms.translate(0, -2.5*dy, 0);
        this.renderText(transforms, soil.getHumidity().getDescription().func_241878_f());

        // Acidity
        transforms.translate(0, dy, 0);
        this.renderText(transforms, soil.getAcidity().getDescription().func_241878_f());

        // Nutrients
        transforms.translate(0, dy, 0);
        this.renderText(transforms, soil.getNutrients().getDescription().func_241878_f());

        // Pop the matrix
        transforms.pop();
    }

    protected <T extends IAgriSoil.SoilProperty> void drawSoilProperty(
            MatrixStack transforms, T property, T[] properties, ResourceLocation filledTexture, ResourceLocation emptyTexture, int[] textureOffsets) {
        for(int i = 0; i < properties.length - 1; i++) {
            int dx = textureOffsets[i];
            int w = textureOffsets[i + 1] - textureOffsets[i];
            float u1 = (dx + 0.0F) / 53.0F;
            float u2 = (dx + w + 0.0F) / 53.0F;
            if (property == properties[i]) {
                this.drawSoilPropertyTexture(transforms, filledTexture, dx - 53.0F/2.0F,  w, u1, u2);
            } else {
                this.drawSoilPropertyTexture(transforms, emptyTexture, dx - 53.0F/2.0F, w, u1, u2);
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected void drawSoilPropertyTexture(MatrixStack transforms, ResourceLocation texture, float x, float w, float u1, float u2) {
        this.bindTexture(texture);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        Matrix4f matrix = transforms.getLast().getMatrix();
        float x1 = x* ICON_SCALE;
        float y1 = 0;
        float x2 = x1 + w* ICON_SCALE;
        float y2 = y1 + 12* ICON_SCALE;
        float v1 = 0.0F;
        float v2 = 1.0F;
        bufferbuilder.pos(matrix, x1, y2, 0).tex(u1, v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(matrix, x2, y2, 0).tex(u2, v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(matrix, x2, y1, 0).tex(u2, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(matrix, x1, y1, 0).tex(u1, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    public void renderText(MatrixStack transforms, IReorderingProcessor text) {
        // Fetch font renderer
        FontRenderer fontRenderer = this.getFontRenderer();
        // Calculate positions
        float x = (-fontRenderer.func_243245_a(text) + 0.0F)/2;
        // Render text
        int color = 0;
        this.getFontRenderer().func_238422_b_(transforms, text, x, 0, color);
    }
}
