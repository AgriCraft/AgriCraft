package com.infinityraider.agricraft.render.plant;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@OnlyIn(Dist.CLIENT)
public class AgriGenomeRenderer implements IRenderUtilities {
    private static final AgriGenomeRenderer INSTANCE = new AgriGenomeRenderer();

    public static AgriGenomeRenderer getInstance() {
        return INSTANCE;
    }

    /** Pi, but as a float */
    private static final float PI = (float) Math.PI;

    /** Helix rotation per spoke rendering setting */
    public static final float RADIANS_PER_GENE = 30*PI/180;

    /** Helix points per distance rendering setting */
    public static final int POINTS_PER_GENE = 10;

    /** Helix line widths */
    public static final float THICKNESS_ACTIVE = 2.5F;
    public static final float THICKNESS_INACTIVE = 1.5F;

    /** Color of inactive genes */
    public static final Vector3f COLOR_INACTIVE = new Vector3f(0.15F, 0.15F, 0.15F);

    /** Gene text style */
    public static final Style STYLE_GENE = Style.EMPTY.withBold(true).setUnderlined(true);

    /** Dominant and recessive separator */
    public static final FormattedCharSequence TEXT_SEPARATOR = new TextComponent(" - ").withStyle(Style.EMPTY.withBold(true)).getVisualOrderText();

    private final Map<IAgriGene<?>, Tuple<Style, Style>> textFormats;

    private AgriGenomeRenderer() {
        this.textFormats = Maps.newIdentityHashMap();
    }

    /**
     * Renders an AgriCraft genome.
     *
     * Renders a right-hand, double helix with the dominant alleles on the left, and the recessive ones on the right,
     * The helix will be rotated so that the selected gene (denoted by index) is along the X-axis,
     * with a smooth transition towards the next gene.
     *
     * Notes:
     *  - The full double helix will be rendered, therefore it might appear squished or stretched based on the height
     *  - The selected gene will be colored with its color, the inactive ones will be greyed out
     *  - Not thread safe
     *
     * @param genePairs the genome for which to draw an overlay
     * @param transforms matrix stack for the transformation
     * @param index the index denoting the selected gene
     * @param transition a double denoting the transition progress to the next/previous gene (bounded by 1 and -1)
     * @param radius the radius of the double helix
     * @param height the height of the double helix
     * @param alpha the transparency of the helix
     * @param color if inactive genes should be colored or greyed
     */
    public void renderDoubleHelix(List<IAgriGenePair<?>> genePairs, PoseStack transforms,
                                  int index, float transition, float radius, float height, float alpha, boolean color) {
        MultiBufferSource.BufferSource buffer = this.getRenderTypeBuffer();
        this.renderDoubleHelix(genePairs, transforms, buffer, index, transition, radius, height, alpha, color);
        LineRenderType.finish(buffer);
    }

    /**
     * Renders an AgriCraft genome.
     *
     * Renders a right-hand, double helix with the dominant alleles on the left, and the recessive ones on the right,
     * The helix will be rotated so that the selected gene (denoted by index) is along the X-axis,
     * with a smooth transition towards the next gene.
     *
     * Notes:
     *  - The full double helix will be rendered, therefore it might appear squished or stretched based on the height
     *  - The selected gene will be colored with its color, the inactive ones will be greyed out
     *
     * @param genePairs the genome for which to draw an overlay
     * @param transforms matrix stack for the transformation
     * @param buffer the vertex buffer to draw with
     * @param index the index denoting the selected gene
     * @param transition a double denoting the transition progress to the next/previous gene (bounded by 1 and -1)
     * @param radius the radius of the double helix
     * @param height the height of the double helix
     * @param alpha the transparency of the helix
     * @param color if inactive genes should be colored or greyed
     */
    public void renderDoubleHelix(List<IAgriGenePair<?>> genePairs, PoseStack transforms, MultiBufferSource buffer,
                                  int index, float transition, float radius, float height, float alpha, boolean color) {

        // Define helix properties
        int count = genePairs.size();
        if(count == 0 || radius == 0 || height == 0) {
            // Should never happen
            return;
        }
        int points = POINTS_PER_GENE * count;
        float heightStep = (height + 0.0F)/points;
        float angleStep = -(RADIANS_PER_GENE + 0.0F)/POINTS_PER_GENE;
        float angleOffset = RADIANS_PER_GENE/2;
        float rotation = (index + transition)*RADIANS_PER_GENE;

        // Push transformation matrix
        transforms.pushPose();

        // Rotate according to the index
        transforms.mulPose(new Quaternion(Vector3f.YP, rotation, false));

        // Fetch transformation matrix
        Matrix4f matrix = transforms.last().pose();

        // First helix
        this.drawHelix(genePairs, index, radius, angleOffset, heightStep, angleStep, points, buffer, matrix, true, alpha, color);
        // Second helix
        this.drawHelix(genePairs, index, radius, PI + angleOffset, heightStep, angleStep, points, buffer, matrix, false, alpha, color);
        // Spokes
        this.drawSpokes(genePairs, index, radius, angleOffset, PI + angleOffset, heightStep, angleStep, buffer, matrix,alpha, color);

        // Pop transformation matrix from the stack
        transforms.popPose();
    }

    public void renderTextOverlay(PoseStack transforms, IAgriGenePair<?> genePair) {
        // Fetch font renderer
        Font fontRenderer = this.getFontRenderer();
        // Fetch styles
        Tuple<Style, Style> textFormats = this.getTextFormats(genePair.getGene());
        // Fetch colors
        int blackColor = STYLE_GENE.getColor() == null ? 0 : STYLE_GENE.getColor().getValue();
        int domColor = textFormats.getA().getColor() == null ? 0 : textFormats.getA().getColor().getValue();
        int recColor = textFormats.getB().getColor() == null ? 0 : textFormats.getB().getColor().getValue();
        // Fetch text components
        FormattedCharSequence geneText = genePair.getGene().getGeneDescription().withStyle(STYLE_GENE).getVisualOrderText();
        FormattedCharSequence domText = genePair.getDominant().getTooltip()/*.mergeStyle(textFormats.getA())*/.getVisualOrderText();
        FormattedCharSequence recText = genePair.getRecessive().getTooltip()/*.mergeStyle(textFormats.getB())*/.getVisualOrderText();
        // Calculate positions
        float y1 = 0;
        float x1 = (-fontRenderer.width(geneText) + 0.0F)/2;
        float y2 = y1 + 1.5F * fontRenderer.lineHeight;
        float x2 = (-fontRenderer.width(TEXT_SEPARATOR) + 0.0F)/2;
        float delta = 1.0F;
        float dx_d = delta + fontRenderer.width(domText);
        float dx_r = delta + fontRenderer.width(TEXT_SEPARATOR);
        // Render text
        this.getFontRenderer().draw(transforms, geneText, x1, y1, blackColor);
        this.getFontRenderer().draw(transforms, TEXT_SEPARATOR, x2, y2, blackColor);
        this.getFontRenderer().draw(transforms, domText, x2 - dx_d, y2, blackColor);
        this.getFontRenderer().draw(transforms, recText, x2 + dx_r, y2, blackColor);
    }

    protected void drawHelix(List<IAgriGenePair<?>> genePairs, int active, float radius, float phase, float dHeight, float dAngle,
                             int points, MultiBufferSource buffer, Matrix4f matrix, boolean dominant, float alpha, boolean color) {
        Vector3f prev = new Vector3f(radius * Mth.cos(-phase), dHeight*points, radius * Mth.sin(-phase));
        Vector3f next;
        for(int i = 0; i < points; i++) {
            // Determine color and width
            int index = i/POINTS_PER_GENE;
            int partial = i % POINTS_PER_GENE;
            IAgriGene<?> gene = genePairs.get(index).getGene();
            Vector3f colorVec = this.getColor(gene, index == active, dominant, color);
            float r = colorVec.x();
            float g = colorVec.y();
            float b = colorVec.z();
            float width = this.getLineWidth(index == active);
            if(partial < POINTS_PER_GENE/2) {
                int prevIndex = (index - 1) < 0 ? index : index - 1;
                IAgriGene<?> prevGene = genePairs.get(prevIndex).getGene();
                Vector3f prevColor = this.getColor(prevGene, prevIndex == active, dominant, color);
                float prevWidth = this.getLineWidth(prevIndex == active);
                float f = (partial + ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = Mth.lerp(f, prevColor.x(), r);
                g = Mth.lerp(f, prevColor.y(), g);
                b = Mth.lerp(f, prevColor.z(), b);
                width = Mth.lerp(f, prevWidth, width);
            } else if(partial > POINTS_PER_GENE/2) {
                int nextIndex = (index + 1) >= genePairs.size() ? index : index + 1;
                IAgriGene<?> nextGene = genePairs.get(nextIndex).getGene();
                Vector3f nextColor = this.getColor(nextGene, nextIndex == active, dominant, color);
                float nextWidth = this.getLineWidth(nextIndex == active);
                float f = (partial - ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = Mth.lerp(f, r, nextColor.x());
                g = Mth.lerp(f, g, nextColor.y());
                b = Mth.lerp(f, b, nextColor.z());
                width = Mth.lerp(f, width, nextWidth);
            }
            // Determine next coordinates
            next = new Vector3f( radius * Mth.cos(-((1 + i)*dAngle + phase)), dHeight*(points - i), radius * Mth.sin(-((1 + i)*dAngle + phase)));
            // Determine normal
            Vector3f normal = new Vector3f(next.x() - prev.x(), next.y() - prev.y(), next.z() - prev.z());
            normal.normalize();
            // Add vertices for line segment
            this.addVertex(buffer, matrix, prev, normal, r, g, b, alpha, width);
            this.addVertex(buffer, matrix, next, normal, r, g, b, alpha, width);
            // Update previous coordinates
            prev = next;
        }
    }

    protected void drawSpokes(List<IAgriGenePair<?>> genePairs, int active, float radius, float phase1, float phase2,
                              float dHeight, float dAngle, MultiBufferSource buffer, Matrix4f matrix, float alpha, boolean color) {
        for(int spoke = 0; spoke < genePairs.size(); spoke++) {
            // Find equivalent point index
            int i = spoke * POINTS_PER_GENE + POINTS_PER_GENE / 2;
            float angle = i * dAngle;
            // Find positions
            float x1 = radius * Mth.cos(-(angle + phase1));
            float x2 = radius * Mth.cos(-(angle + phase2));
            float y = dHeight * (POINTS_PER_GENE * genePairs.size() - i);
            float z1 = radius * Mth.sin(-(angle + phase1));
            float z2 = radius * Mth.sin(-(angle + phase2));
            // find colors
            IAgriGene<?> gene = genePairs.get(spoke).getGene();
            Vector3f dom = this.getColor(gene, active == spoke, true, color);
            Vector3f rec = this.getColor(gene, active == spoke, false, color);
            // find width
            float width = this.getLineWidth(active == spoke);
            // Positions and color
            Vector3f prevPos = new Vector3f(x1, y, z1);
            Vector3f nextPos;
            Vector3f prevCol = dom;
            Vector3f nextCol;
            for (float j = 1.0F; j <= POINTS_PER_GENE; j++) {
                // positions, color and normal
                nextPos = new Vector3f(
                        Mth.lerp(j / POINTS_PER_GENE, x1, x2),
                        y,
                        Mth.lerp(j / POINTS_PER_GENE, z1, z2)
                );
                nextCol = new Vector3f(
                        Mth.lerp(j / POINTS_PER_GENE, dom.x(), rec.x()),
                        Mth.lerp(j / POINTS_PER_GENE, dom.y(), rec.y()),
                        Mth.lerp(j / POINTS_PER_GENE, dom.z(), rec.z())
                );
                Vector3f normal = new Vector3f(nextPos.x() - prevPos.x(), nextPos.y() - prevPos.y(), nextPos.z() - prevPos.z());
                normal.normalize();
                // Add vertices
                this.addVertex(buffer, matrix, prevPos, normal, prevCol, alpha, width);
                this.addVertex(buffer, matrix, nextPos, normal, nextCol, alpha, width);
                // Add reverse vertices
                this.addVertex(buffer, matrix, nextPos, normal, nextCol, alpha, width);
                this.addVertex(buffer, matrix, prevPos, normal, prevCol, alpha, width);
                // refresh previous positions and color
                prevPos = nextPos;
                prevCol = nextCol;
            }
        }
    }
    protected void addVertex(MultiBufferSource buffer, Matrix4f matrix, Vector3f pos, Vector3f normal, Vector3f color, float a, float width) {
        this.addVertex(buffer, matrix, pos, normal, color.x(), color.y(), color.z(), a, width);
    }

    protected void addVertex(MultiBufferSource buffer, Matrix4f matrix, Vector3f pos, Vector3f normal, float r, float g, float b, float a, float width) {
        this.addVertex(buffer, matrix, pos.x(), pos.y(), pos.z(), normal.x(), normal.y(), normal.z(), r, g, b, a, width);
    }

    protected void addVertex(MultiBufferSource buffer, Matrix4f matrix, float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float a, float width) {
        VertexConsumer builder = this.getVertexBuilder(buffer, this.getRenderType(width));
        builder.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .normal(nx, ny, nz)
                .endVertex();
    }

    protected Vector3f getColor(IAgriGene<?> gene, boolean active, boolean dominant, boolean ignoreActive) {
        if(ignoreActive || active) {
            return dominant ? gene.getDominantColor() : gene.getRecessiveColor();
        } else {
            return COLOR_INACTIVE;
        }
    }

    protected float getLineWidth(boolean active) {
        return active ? THICKNESS_ACTIVE : THICKNESS_INACTIVE;
    }

    protected RenderType getRenderType(float width) {
        return LineRenderType.get(width);
    }

    protected Tuple<Style, Style> getTextFormats(IAgriGene<?> gene) {
        return this.textFormats.computeIfAbsent(gene, aGene ->
            new Tuple<>(
                    Style.EMPTY.withColor(this.convertColor(aGene.getDominantColor())),
                    Style.EMPTY.withColor(this.convertColor(aGene.getRecessiveColor()))
            ));
    }

    public static class LineRenderType extends RenderType {
        // We need to put the static instance inside a class, as to initialize it we need to access a Builder, which has protected access
        // Therefore we need a dummy constructor which will never be called ¯\_(ツ)_/¯
        private LineRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean delegate, boolean sorted, Runnable pre, Runnable post) {
            super(name, format, mode, bufferSize, delegate, sorted, pre, post);
        }

        private static final Map<Float, RenderType> CACHE = Maps.newConcurrentMap();

        private static final String RENDER_TYPE_KEY = AgriCraft.instance.getModId() + ":genome_lines";

        public static RenderType get(float width) {
            return CACHE.computeIfAbsent(width, aFloat ->
                    create(RENDER_TYPE_KEY + "_" + aFloat,
                            DefaultVertexFormat.POSITION_COLOR_NORMAL,
                            VertexFormat.Mode.LINES,
                            256,
                            false,
                            false,
                            RenderType.CompositeState.builder()
                                    .setLineState(new LineStateShard(OptionalDouble.of(aFloat)))
                                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                                    .setShaderState(RENDERTYPE_LINES_SHADER)
                                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                    .setOutputState(ITEM_ENTITY_TARGET)
                                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                                    .createCompositeState(false)));
        }

        public static void finish(MultiBufferSource.BufferSource buffer) {
            CACHE.values().forEach(buffer::endBatch);
        }
    }
}
