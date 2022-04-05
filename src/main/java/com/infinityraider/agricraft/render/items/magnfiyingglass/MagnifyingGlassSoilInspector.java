package com.infinityraider.agricraft.render.items.magnfiyingglass;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.requirement.NoSoil;
import com.infinityraider.agricraft.render.items.journal.JournalDataDrawerBase;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    public boolean canInspect(Level world, BlockPos pos, Player player) {
        return AgriApi.getSoil(world, pos).map(IAgriSoil::isSoil).orElse(false);
    }

    @Override
    public boolean canInspect(Level world, Entity entity, Player player) {
        return false;
    }

    @Override
    public void onInspectionStart(Level world, BlockPos pos, Player player) {
        this.cachedSoil = AgriApi.getSoil(world, pos).orElse(NoSoil.getInstance());
    }

    @Override
    public void onInspectionStart(Level world, Entity entity, Player player) {
        // Do nothing
    }

    @Override
    public boolean onInspectionTick(Level world, BlockPos pos, Player player) {
        return this.cachedSoil != null && this.cachedSoil.isSoil();
    }

    @Override
    public boolean onInspectionTick(Level world, Entity entity, Player player) {
        return false;
    }

    @Override
    public void onInspectionEnd(Level world, BlockPos pos, Player player) {
        this.cachedSoil = null;
    }

    @Override
    public void onInspectionEnd(Level world, @Nullable Entity entity, Player player) {
        // Do nothing
    }

    @Override
    public void onMouseScroll(int delta) {
        // Do nothing
    }

    @Override
    public void doInspectionRender(PoseStack transforms, float partialTick, @Nullable Entity entity) {
        // Check if soil is still valid
        IAgriSoil soil = this.cachedSoil;
        if(soil == null || !soil.isSoil()) {
            return;
        }

        // Push matrix for render, flip y, and apply y offset
        transforms.pushPose();
        transforms.mulPose(FLIP_Y);
        transforms.translate(0, DELTA_Y, 0);

        // Draw the soil property icons
        this.drawSoilPropertyIcons(transforms, soil);

        // Draw the text
        this.drawSoilPropertyText(transforms, soil);

        // Pop the matrix
        transforms.popPose();
    }

    protected void drawSoilPropertyIcons(PoseStack transforms, IAgriSoil soil) {
        // Push the matrix
        transforms.pushPose();

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
        transforms.popPose();
    }

    protected void drawSoilPropertyText(PoseStack transforms, IAgriSoil soil) {
        // Push the matrix
        transforms.pushPose();

        // Scale down
        float width = this.getScaledWindowWidth();
        float height = this.getScaledWindowHeight();
        float scale = TEXT_SCALE/Math.max(width, height);
        transforms.scale(scale, scale, scale);

        // Humidity
        float dy = DELTA_Y/scale;
        transforms.translate(0, -2.5*dy, 0);
        this.renderText(transforms, soil.getHumidity().getDescription().getVisualOrderText());

        // Acidity
        transforms.translate(0, dy, 0);
        this.renderText(transforms, soil.getAcidity().getDescription().getVisualOrderText());

        // Nutrients
        transforms.translate(0, dy, 0);
        this.renderText(transforms, soil.getNutrients().getDescription().getVisualOrderText());

        // Pop the matrix
        transforms.popPose();
    }

    protected <T extends IAgriSoil.SoilProperty> void drawSoilProperty(
            PoseStack transforms, T property, T[] properties, ResourceLocation filledTexture, ResourceLocation emptyTexture, int[] textureOffsets) {
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
    protected void drawSoilPropertyTexture(PoseStack transforms, ResourceLocation texture, float x, float w, float u1, float u2) {
        this.bindTexture(texture);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        Matrix4f matrix = transforms.last().pose();
        float x1 = x* ICON_SCALE;
        float y1 = 0;
        float x2 = x1 + w* ICON_SCALE;
        float y2 = y1 + 12* ICON_SCALE;
        float v1 = 0.0F;
        float v2 = 1.0F;
        bufferbuilder.vertex(matrix, x1, y2, 0).uv(u1, v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, 0).uv(u2, v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x2, y1, 0).uv(u2, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, x1, y1, 0).uv(u1, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

    public void renderText(PoseStack transforms, FormattedCharSequence text) {
        // Fetch font renderer
        Font fontRenderer = this.getFontRenderer();
        // Calculate positions
        float x = (-fontRenderer.width(text) + 0.0F)/2;
        // Render text
        int color = 0;
        this.getFontRenderer().draw(transforms, text, x, 0, color);
    }
}
