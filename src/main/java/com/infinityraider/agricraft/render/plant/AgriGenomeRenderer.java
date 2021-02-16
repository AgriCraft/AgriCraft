package com.infinityraider.agricraft.render.plant;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

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
    public static final Style STYLE_GENE = Style.EMPTY.setBold(true).setUnderlined(true);

    /** Dominant and recessive separator */
    public static final IReorderingProcessor TEXT_SEPARATOR = new StringTextComponent(" - ").mergeStyle(Style.EMPTY.setBold(true)).func_241878_f();

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
    public void renderDoubleHelix(List<IAgriGenePair<?>> genePairs, MatrixStack transforms, IRenderTypeBuffer buffer,
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
        transforms.push();

        // Rotate according to the index
        transforms.rotate(new Quaternion(Vector3f.YP, rotation, false));

        // Fetch transformation matrix
        Matrix4f matrix = transforms.getLast().getMatrix();

        // First helix
        this.drawHelix(genePairs, index, radius, angleOffset, heightStep, angleStep, points, buffer, matrix, true, alpha, color);
        // Second helix
        this.drawHelix(genePairs, index, radius, PI + angleOffset, heightStep, angleStep, points, buffer, matrix, false, alpha, color);
        // Spokes
        this.drawSpokes(genePairs, index, radius, angleOffset, PI + angleOffset, heightStep, angleStep, buffer, matrix,alpha, color);

        // Pop transformation matrix from the stack
        transforms.pop();
    }

    public void renderTextOverlay(MatrixStack transforms, IAgriGenePair<?> genePair) {
        // Fetch font renderer
        FontRenderer fontRenderer = this.getFontRenderer();
        // Fetch styles
        Tuple<Style, Style> textFormats = this.getTextFormats(genePair.getGene());
        // Fetch colors
        int blackColor = STYLE_GENE.getColor() == null ? 0 : STYLE_GENE.getColor().getColor();
        int domColor = textFormats.getA().getColor() == null ? 0 : textFormats.getA().getColor().getColor();
        int recColor = textFormats.getB().getColor() == null ? 0 : textFormats.getB().getColor().getColor();
        // Fetch text components
        IReorderingProcessor geneText = genePair.getGene().getDescription().mergeStyle(STYLE_GENE).func_241878_f();
        IReorderingProcessor domText = genePair.getDominant().getTooltip()/*.mergeStyle(textFormats.getA())*/.func_241878_f();
        IReorderingProcessor recText = genePair.getRecessive().getTooltip()/*.mergeStyle(textFormats.getB())*/.func_241878_f();
        // Calculate positions
        float y1 = 0;
        float x1 = (-fontRenderer.func_243245_a(geneText) + 0.0F)/2;
        float y2 = y1 + 1.5F * fontRenderer.FONT_HEIGHT;
        float x2 = (-fontRenderer.func_243245_a(TEXT_SEPARATOR) + 0.0F)/2;
        float delta = 1.0F;
        float dx_d = delta + fontRenderer.func_243245_a(domText);
        float dx_r = delta + fontRenderer.func_243245_a(TEXT_SEPARATOR);
        // Render text
        this.getFontRenderer().func_238422_b_(transforms, geneText, x1, y1, blackColor);
        this.getFontRenderer().func_238422_b_(transforms, TEXT_SEPARATOR, x2, y2, blackColor);
        this.getFontRenderer().func_238422_b_(transforms, domText, x2 - dx_d, y2, blackColor);
        this.getFontRenderer().func_238422_b_(transforms, recText, x2 + dx_r, y2, blackColor);
    }

    protected void drawHelix(List<IAgriGenePair<?>> genePairs, int active, float radius, float phase, float dHeight, float dAngle,
                             int points, IRenderTypeBuffer buffer, Matrix4f matrix, boolean dominant, float alpha, boolean color) {
        float x_1 = radius * MathHelper.cos(-phase);
        float y_1 = dHeight*points;
        float z_1 = radius * MathHelper.sin(-phase);
        for(int i = 0; i < points; i++) {
            // Determine color and width
            int index = i/POINTS_PER_GENE;
            int partial = i % POINTS_PER_GENE;
            IAgriGene<?> gene = genePairs.get(index).getGene();
            Vector3f colorVec = this.getColor(gene, index == active, dominant, color);
            float r = colorVec.getX();
            float g = colorVec.getY();
            float b = colorVec.getZ();
            float w = this.getLineWidth(index == active);
            if(partial < POINTS_PER_GENE/2) {
                int prevIndex = (index - 1) < 0 ? index : index - 1;
                IAgriGene<?> prevGene = genePairs.get(prevIndex).getGene();
                Vector3f prevColor = this.getColor(prevGene, prevIndex == active, dominant, color);
                float prevWidth = this.getLineWidth(prevIndex == active);
                float f = (partial + ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = MathHelper.lerp(f, prevColor.getX(), r);
                g = MathHelper.lerp(f, prevColor.getY(), g);
                b = MathHelper.lerp(f, prevColor.getZ(), b);
                w = MathHelper.lerp(f, prevWidth, w);
            } else if(partial > POINTS_PER_GENE/2) {
                int nextIndex = (index + 1) >= genePairs.size() ? index : index + 1;
                IAgriGene<?> nextGene = genePairs.get(nextIndex).getGene();
                Vector3f nextColor = this.getColor(nextGene, nextIndex == active, dominant, color);
                float nextWidth = this.getLineWidth(nextIndex == active);
                float f = (partial - ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = MathHelper.lerp(f, r, nextColor.getX());
                g = MathHelper.lerp(f, g, nextColor.getY());
                b = MathHelper.lerp(f, b, nextColor.getZ());
                w = MathHelper.lerp(f, w, nextWidth);
            }
            // Determine coordinates
            float x_2 = radius * MathHelper.cos(-((1 + i)*dAngle + phase));
            float y_2 = dHeight*(points - i);
            float z_2 = radius * MathHelper.sin(-((1 + i)*dAngle + phase));
            // Add vertices for line segment
            this.addVertex(buffer, matrix, x_1, y_1, z_1, r, g, b, alpha, w);
            this.addVertex(buffer, matrix, x_2, y_2, z_2, r, g, b, alpha, w);
            // Update previous coordinates
            x_1 = x_2;
            y_1 = y_2;
            z_1 = z_2;
        }
    }

    protected void drawSpokes(List<IAgriGenePair<?>> genePairs, int active, float radius, float phase1, float phase2,
                              float dHeight, float dAngle, IRenderTypeBuffer buffer, Matrix4f matrix, float alpha, boolean color) {
        for(int spoke = 0; spoke < genePairs.size(); spoke++) {
            // Find equivalent point index
            int i = spoke*POINTS_PER_GENE + POINTS_PER_GENE/2;
            float angle = i*dAngle;
            // Find positions
            float x1 = radius*MathHelper.cos(-(angle + phase1));
            float x2 = radius*MathHelper.cos(-(angle + phase2));
            float y = dHeight * (POINTS_PER_GENE * genePairs.size() - i);
            float z1 = radius*MathHelper.sin(-(angle + phase1));
            float z2 = radius*MathHelper.sin(-(angle + phase2));
            // find colors
            IAgriGene<?> gene = genePairs.get(spoke).getGene();
            Vector3f dom = this.getColor(gene, active == spoke, true, color);
            Vector3f rec = this.getColor(gene, active == spoke, false, color);
            // find width
            float w = this.getLineWidth(active == spoke);
            // First vertex of the first segment
            this.addVertex(buffer, matrix, x1, y, z1, dom.getX(), dom.getY(), dom.getZ(), alpha, w);
            for(int j = 1; j < POINTS_PER_GENE; j++) {
                float x = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, x1, x2);
                float z = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, z1, z2);
                float r = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, dom.getX(), rec.getX());
                float g = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, dom.getY(), rec.getY());
                float b = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, dom.getZ(), rec.getZ());
                // Second vertex of the previous segment
                this.addVertex(buffer, matrix, x, y, z, r, g, b, alpha, w);
                // First vertex of the next segment
                this.addVertex(buffer, matrix, x, y, z, r, g, b, alpha, w);
            }
            // Second vertex of the last segment
            this.addVertex(buffer, matrix, x2, y, z2, rec.getX(), rec.getY(), rec.getZ(), alpha, w);
        }
    }

    protected void addVertex(IRenderTypeBuffer buffer, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a, float w) {
        IVertexBuilder builder = this.getVertexBuilder(buffer, this.getRenderType(w));
        builder.pos(matrix, x, y, z)
                .color(r, g, b, a)
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
                    Style.EMPTY.setColor(this.convertColor(aGene.getDominantColor())),
                    Style.EMPTY.setColor(this.convertColor(aGene.getRecessiveColor()))
            ));
    }

    public static class LineRenderType extends RenderType {
        // We need to put the static instance inside a class, as to initialize it we need to access a Builder, which has protected access
        // Therefore we need a dummy constructor which will never be called ¯\_(ツ)_/¯
        private LineRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        private static final Map<Float, RenderType> CACHE = Maps.newConcurrentMap();

        private static final String RENDER_TYPE_KEY = AgriCraft.instance.getModId() + ":genome_lines";

        public static RenderType get(float width) {
            return CACHE.computeIfAbsent(width, aFloat ->
                    makeType(RENDER_TYPE_KEY + "_" + aFloat,
                            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES,
                            256,
                            State.getBuilder()
                                    .line(new LineState(OptionalDouble.of(aFloat)))
                                    .layer(field_239235_M_)
                                    .transparency(TRANSLUCENT_TRANSPARENCY)
                                    .target(field_241712_U_)
                                    .writeMask(COLOR_DEPTH_WRITE)
                                    .build(false)));
        }
    }
}
