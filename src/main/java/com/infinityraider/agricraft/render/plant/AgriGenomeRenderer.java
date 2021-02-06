package com.infinityraider.agricraft.render.plant;

import com.infinityraider.agricraft.AgriCraft;
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
import net.minecraft.util.math.vector.Vector4f;
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

    /** Helix points per distance rendering setting */
    public static final float RADIANS_PER_GENE = 30*PI/180;
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
     * @param dominant the color for the dominant helix
     * @param recessive the color for the recessive helix
     */
    public void renderGenome(List<IAgriGenePair<?>> genePairs, MatrixStack transforms, int index, float transition,
                             float radius, float height, Vector4f dominant, Vector4f recessive) {

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
        this.drawHelix(radius, PI - angleOffset, heightStep, angleStep, points, builder, matrix,
                dominant.getX(), dominant.getY(), dominant.getZ(), dominant.getW());
        // Second helix
        this.drawHelix(radius, - angleOffset, heightStep, angleStep, points, builder, matrix,
                recessive.getX(), recessive.getY(), recessive.getZ(), recessive.getW());
        // Spokes
        this.drawSpokes(radius, PI - angleOffset, - angleOffset, heightStep, angleStep, points, builder, matrix,
                dominant.getX(), dominant.getY(), dominant.getZ(), dominant.getW(),
                recessive.getX(), recessive.getY(), recessive.getZ(), recessive.getW());

        // Pop transformation matrix from the stack
        transforms.pop();

        // Finalize rendering
        buffer.finish(LineRenderType.INSTANCE);
    }

    protected void drawHelix(float radius, float phase, float dHeight, float dAngle, int points,
                             IVertexBuilder builder, Matrix4f matrix, float r, float g, float b, float a) {
        float x_1 = radius * MathHelper.cos(phase);
        float y_1 = 0;
        float z_1 = radius * MathHelper.sin(phase);
        for(int i = 0; i < points; i++) {
            float x_2 = radius * MathHelper.cos((1 + i)*dAngle + phase);
            float y_2 = (1 + i) * dHeight;
            float z_2 = radius * MathHelper.sin((1 + i)*dAngle + phase);
            this.addVertex(builder, matrix, x_1, y_1, z_1, r, g, b, a);
            this.addVertex(builder, matrix, x_2, y_2, z_2, r, g, b, a);
            x_1 = x_2;
            y_1 = y_2;
            z_1 = z_2;
        }
    }

    protected void drawSpokes(float radius, float phase1, float phase2, float dHeight, float dAngle, int points,
                              IVertexBuilder builder, Matrix4f matrix, float r1, float g1, float b1, float a1,
                              float r2, float g2, float b2, float a2) {
        for(int spoke = 0; spoke < points; spoke++) {
            // Find equivalent point index
            int i = spoke*POINTS_PER_GENE + POINTS_PER_GENE/2;
            float angle = i*dAngle;
            // Find positions
            float x1 = radius*MathHelper.cos(angle + phase1);
            float x2 = radius*MathHelper.cos(angle + phase2);
            float y = i * dHeight;
            float z1 = radius*MathHelper.sin(angle + phase1);
            float z2 = radius*MathHelper.sin(angle + phase2);
            // First vertex of the first segment
            this.addVertex(builder, matrix, x1, y, z1, r1, g1, b1, a1);
            for(int j = 1; j < POINTS_PER_GENE; j++) {
                float x = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, x1, x2);
                float z = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, z1, z2);
                float r = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, r1, r2);
                float g = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, g1, g2);
                float b = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, b1, b2);
                float a = MathHelper.lerp((j + 0.0F)/POINTS_PER_GENE, a1, a2);
                // Second vertex of the previous segment
                this.addVertex(builder, matrix, x, y, z, r, g, b, a);
                // First vertex of the next segment
                this.addVertex(builder, matrix, x, y, z, r, g, b, a);
            }
            // Second vertex of the last segment
            this.addVertex(builder, matrix, x2, y, z2, r2, g2, b2, a2);
        }
    }

    protected void addVertex(IVertexBuilder builder, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {
        builder.pos(matrix, x, y, z)
                .color(r, g, b, a)
                .endVertex();

    }

    public static class LineRenderType extends RenderType {
        private LineRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
        }

        public static final String RENDER_TYPE_KEY = AgriCraft.instance.getModId() + ":genome_lines";
        public static final Double LINE_THICKNESS = 10.0;

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
