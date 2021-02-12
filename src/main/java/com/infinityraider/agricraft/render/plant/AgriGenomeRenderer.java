package com.infinityraider.agricraft.render.plant;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.OptionalDouble;

@OnlyIn(Dist.CLIENT)
public class AgriGenomeRenderer implements IRenderUtilities {
    private static final AgriGenomeRenderer INSTANCE = new AgriGenomeRenderer();

    public static AgriGenomeRenderer getInstance() {
        return INSTANCE;
    }

    private static final float PI = (float) Math.PI;

    /** Helix rotation per spoke rendering setting */
    public static final float RADIANS_PER_GENE = 30*PI/180;

    /** Helix points per distance rendering setting */
    public static final int POINTS_PER_GENE = 10;

    private AgriGenomeRenderer() {}

    /**
     * Renders an overlay of a genome on the screen.
     *
     * Renders a double helix with the dominant alleles on the left, and the recessive ones on the right,
     * The helix will be translated and rotated down to the selected gene (denoted by index),
     * with a smooth transition towards the next gene.
     *
     * @param genePairs the genome for which to draw an overlay
     * @param transforms matrix stack for the transformation
     * @param index the index denoting the selected gene
     * @param transition a double denoting the transition progress to the next/previous gene (bounded by 1 and -1)
     * @param radius the radius of the double helix
     * @param height the height of the double helix
     * @param alpha the transparency of the helix
     */
    public void renderDoubleHelix(List<IAgriGenePair<?>> genePairs, MatrixStack transforms, int index, float transition,
                                  float radius, float height, float alpha) {

        // Define helix properties
        int count = genePairs.size();
        if(count == 0 || radius == 0 || height == 0) {
            // Should never happen
            return;
        }
        int points = POINTS_PER_GENE * (count + 1);
        float heightStep = (height + 0.0F)/points;
        float angleStep = -(RADIANS_PER_GENE + 0.0F)/POINTS_PER_GENE;
        float angleOffset = RADIANS_PER_GENE/2;
        float rotation = (index + transition)*RADIANS_PER_GENE;

        // Push transformation matrix
        transforms.push();
        // Rotate according to the index
        transforms.rotate(new Quaternion(Vector3f.YP, rotation, false));

        // Fetch vertex buffer, builder and transformation matrix
        IRenderTypeBuffer.Impl buffer = this.getRenderTypeBuffer();
        IVertexBuilder builder = this.getVertexBuilder(buffer, LineRenderType.INSTANCE);
        Matrix4f matrix = transforms.getLast().getMatrix();

        // First helix
        this.drawHelix(genePairs, radius, PI - angleOffset, heightStep, angleStep, points, builder, matrix, true, alpha);
        // Second helix
        this.drawHelix(genePairs, radius, - angleOffset, heightStep, angleStep, points, builder, matrix, false, alpha);
        // Spokes
        this.drawSpokes(genePairs, radius, PI - angleOffset, - angleOffset, heightStep, angleStep, builder, matrix,alpha);

        // Pop transformation matrix from the stack
        transforms.pop();

        // Finalize rendering
        buffer.finish(LineRenderType.INSTANCE);
    }

    protected void drawHelix(List<IAgriGenePair<?>> genePairs, float radius, float phase, float dHeight, float dAngle, int points,
                             IVertexBuilder builder, Matrix4f matrix, boolean dominant, float alpha) {
        float x_1 = radius * MathHelper.cos(phase);
        float y_1 = 0;
        float z_1 = radius * MathHelper.sin(phase);
        for(int i = 0; i < points; i++) {
            // Determine color
            int index = i/POINTS_PER_GENE;
            index = index >= genePairs.size() ? genePairs.size() - 1 : index;
            int partial = i % POINTS_PER_GENE;
            IAgriGene<?> gene = genePairs.get(index).getGene();
            float r = dominant ? gene.getDominantColor().getX() : gene.getRecessiveColor().getX();
            float g = dominant ? gene.getDominantColor().getY() : gene.getRecessiveColor().getY();
            float b = dominant ? gene.getDominantColor().getZ() : gene.getRecessiveColor().getZ();
            if(partial < POINTS_PER_GENE/2) {
                IAgriGene<?> prevGene = (index - 1) < 0 ? gene :genePairs.get(index - 1).getGene();
                float f = (partial + ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = MathHelper.lerp(f, dominant ? prevGene.getDominantColor().getX() : prevGene.getRecessiveColor().getX(), r);
                g = MathHelper.lerp(f, dominant ? prevGene.getDominantColor().getY() : prevGene.getRecessiveColor().getY(), g);
                b = MathHelper.lerp(f, dominant ? prevGene.getDominantColor().getZ() : prevGene.getRecessiveColor().getZ(), b);
            } else if(partial > POINTS_PER_GENE/2){
                IAgriGene<?> nextGene = (index + 1) >= genePairs.size() ? gene :genePairs.get(index + 1).getGene();
                float f = (partial - ((POINTS_PER_GENE + 0.0F)/2)) / POINTS_PER_GENE;
                r = MathHelper.lerp(f, r, dominant ? nextGene.getDominantColor().getX() : nextGene.getRecessiveColor().getX());
                g = MathHelper.lerp(f, g, dominant ? nextGene.getDominantColor().getY() : nextGene.getRecessiveColor().getY());
                b = MathHelper.lerp(f, b, dominant ? nextGene.getDominantColor().getZ() : nextGene.getRecessiveColor().getZ());
            }
            // Determine coordinates
            float x_2 = radius * MathHelper.cos((1 + i)*dAngle + phase);
            float y_2 = (1 + i) * dHeight;
            float z_2 = radius * MathHelper.sin((1 + i)*dAngle + phase);
            // Add vertices for line segment
            this.addVertex(builder, matrix, x_1, y_1, z_1, r, g, b, alpha);
            this.addVertex(builder, matrix, x_2, y_2, z_2, r, g, b, alpha);
            // Update previous coordinates
            x_1 = x_2;
            y_1 = y_2;
            z_1 = z_2;
        }
    }

    protected void drawSpokes(List<IAgriGenePair<?>> genePairs, float radius, float phase1, float phase2,
                              float dHeight, float dAngle, IVertexBuilder builder, Matrix4f matrix, float alpha) {
        for(int spoke = 0; spoke < genePairs.size(); spoke++) {
            // Find equivalent point index
            int i = spoke*POINTS_PER_GENE + POINTS_PER_GENE/2;
            float angle = i*dAngle;
            // Find positions
            float x1 = radius*MathHelper.cos(angle + phase1);
            float x2 = radius*MathHelper.cos(angle + phase2);
            float y = i * dHeight;
            float z1 = radius*MathHelper.sin(angle + phase1);
            float z2 = radius*MathHelper.sin(angle + phase2);
            // find colors
            IAgriGene<?> gene = genePairs.get(spoke).getGene();
            float r1 = gene.getDominantColor().getX();
            float g1 = gene.getDominantColor().getY();
            float b1 = gene.getDominantColor().getZ();
            float r2 = gene.getRecessiveColor().getX();
            float g2 = gene.getRecessiveColor().getY();
            float b2 = gene.getRecessiveColor().getZ();
            // First vertex of the first segment
            this.addVertex(builder, matrix, x1, y, z1, r1, g1, b1, alpha);
            for(int j = 1; j < POINTS_PER_GENE; j++) {
                float x = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, x1, x2);
                float z = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, z1, z2);
                float r = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, r1, r2);
                float g = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, g1, g2);
                float b = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, b1, b2);
                // Second vertex of the previous segment
                this.addVertex(builder, matrix, x, y, z, r, g, b, alpha);
                // First vertex of the next segment
                this.addVertex(builder, matrix, x, y, z, r, g, b, alpha);
            }
            // Second vertex of the last segment
            this.addVertex(builder, matrix, x2, y, z2, r2, g2, b2, alpha);
        }
    }

    protected void addVertex(IVertexBuilder builder, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {
        builder.pos(matrix, x, y, z)
                .color(r, g, b, a)
                .endVertex();

    }

    public static class LineRenderType extends RenderType {
        // We need to put the static instance inside a class, as to initialize it we need to access a Builder,
        // which has protected access
        // Therefore we need a dummy constructor which will never be called ¯\_(ツ)_/¯
        private LineRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final String RENDER_TYPE_KEY = AgriCraft.instance.getModId() + ":genome_lines";
        public static final Double LINE_THICKNESS = 5.0;

        public static final RenderType INSTANCE = makeType(RENDER_TYPE_KEY,
                DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
                LineRenderType.State.getBuilder().line(new RenderState.LineState(OptionalDouble.of(LINE_THICKNESS)))
                        .layer(LineRenderType.field_239235_M_)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .texture(NO_TEXTURE)
                        .depthTest(DEPTH_ALWAYS)
                        .cull(CULL_DISABLED)
                        .lightmap(LIGHTMAP_DISABLED)
                        .writeMask(COLOR_WRITE)
                        .build(true));
    }
}
