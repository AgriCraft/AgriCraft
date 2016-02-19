package com.infinityraider.agricraft.renderers;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.reference.Constants.*;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
	
    public static final int COLOR_MULTIPLIER_STANDARD = 16777215;
	
	private static final float MIN_UV = 0;
	private static final float MAX_UV = 16;

    private RenderUtil() {}
	
	private static float inboundUV(float m) {
		return (m < MIN_UV) ? MIN_UV : (m > MAX_UV) ? MAX_UV : m;
	}

    /** Tessellates a vertex at the given position using the passed tessellator and texture u and v position */
    public static void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v) {
		u = inboundUV(u);
		v = inboundUV(v);
        tessellator.addVertexWithUV(x*UNIT, y*UNIT, z*UNIT, u * UNIT, v * UNIT);
    }

    /** Tessellates a vertex at the given position using the passed tessellator, texture u and v position are interpolated using the passed icon */
    public static void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v, TextureAtlasSprite icon) {
		u = icon.getInterpolatedU(inboundUV(u));
		v = icon.getInterpolatedV(inboundUV(v));
        tessellator.addVertexWithUV(x * UNIT, y * UNIT, z * UNIT, u, v);
    }

    /** Draws both sides of a face parallel to the XY plane*/
    public static void drawScaledFaceDoubleXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z) {
        z = z*16.0F;
        //front
        addScaledVertexWithUV(tessellator, maxX, maxY, z, MAX_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, MAX_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, MIN_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, MIN_UV, MIN_UV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, maxY, z, MAX_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, MIN_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, MIN_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, MAX_UV, MAX_UV, icon);
    }

    /** Draws both sides of a face parallel to the XZ plane*/
    public static void drawScaledFaceDoubleXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y) {
        y = y*16.0F;
        //front
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, MAX_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, MAX_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, MIN_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, MIN_UV, MAX_UV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, MAX_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, MIN_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, MIN_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, MAX_UV, MIN_UV, icon);
    }

    /** Draws both sides of a face parallel to the YZ plane*/
    public static void drawScaledFaceDoubleYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x) {
        x = x*16.0F;
        //front
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, MAX_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, MAX_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, MIN_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, MIN_UV, MIN_UV, icon);
        //back
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, MAX_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, MIN_UV, MIN_UV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, MIN_UV, MAX_UV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, MAX_UV, MAX_UV, icon);
    }

    /** Draws a prism which is rotated along the given direction */
    public static void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, int colorMultiplier, AgriForgeDirection direction) {
        float adj[] = rotatePlane(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawScaledPrism(tessellator, adj[0], adj[1], adj[2], adj[3], adj[4], adj[5], icon, colorMultiplier);
    }

    public static void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        drawScaledPrism(tessellator, minX, minY ,minZ, maxX, maxY, maxZ, icon, COLOR_MULTIPLIER_STANDARD);
    }

    /** Draws a prism */
    public static void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, int colorMultiplier) {
        //bottom
        drawScaledFaceBackXZ(tessellator, minX, minZ, maxX, maxZ, icon, minY / 16.0F, colorMultiplier);
        //top
        drawScaledFaceFrontXZ(tessellator, minX, minZ, maxX, maxZ, icon, maxY / 16.0F, colorMultiplier);
        //back
        drawScaledFaceBackXY(tessellator, minX, minY, maxX, maxY, icon, minZ / 16.0F, colorMultiplier);
        //front
        drawScaledFaceFrontXY(tessellator, minX, minY, maxX, maxY, icon, maxZ / 16.0F, colorMultiplier);
        //left
        drawScaledFaceBackYZ(tessellator, minY, minZ, maxY, maxZ, icon, minX / 16.0F, colorMultiplier);
        //right
        drawScaledFaceFrontYZ(tessellator, minY, minZ, maxY, maxZ, icon, maxX / 16.0F, colorMultiplier);
    }

    /** Draws the front side of a face parallel to the XY plane */
    public static void drawScaledFaceFrontXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = MAX_UV-maxY;
        float maxV = MAX_UV-minY;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.SOUTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
    }

    /** Draws the front side of a face parallel to the XZ plane */
    public static void drawScaledFaceFrontXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.UP);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
    }

    /** Draws the front side of a face parallel to the YZ plane */
    public static void drawScaledFaceFrontYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = MAX_UV-maxY;
        float maxV = MAX_UV-minY;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.EAST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
    }

    /** Draws the back side of a face parallel to the XY plane */
    public static void drawScaledFaceBackXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = MAX_UV - maxY;
        float maxV = MAX_UV - minY;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.NORTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
    }

    /** Draws the back side of a face parallel to the XZ plane */
    public static void drawScaledFaceBackXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.DOWN);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
    }

    /** Draws the back side of a face parallel to the YZ plane */
    public static void drawScaledFaceBackYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = MAX_UV - maxY;
        float maxV = MAX_UV - minY;
        applyColorMultiplier(tessellator, colorMultiplier, AgriForgeDirection.WEST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
    }

    /** Applies a color multiplier to the tessellator for a given side, the side is transformed according to the rotation of the tessellator */
    public static void applyColorMultiplier(TessellatorV2 tessellator, int colorMultiplier, AgriForgeDirection side) {
        float preMultiplier = getMultiplier(transformSide(tessellator, side));
        float r = preMultiplier * ((float) (colorMultiplier >> 16 & 255) / 255.0F);
        float g = preMultiplier * ((float) (colorMultiplier >> 8 & 255) / 255.0F);
        float b = preMultiplier * ((float) (colorMultiplier & 255) / 255.0F);
        tessellator.setColorOpaque_F(r, g, b);
    }

    /** Transforms a direction according to the rotation of the tessellator */
    public static AgriForgeDirection transformSide(TessellatorV2 tessellator, AgriForgeDirection dir) {
        if(dir==AgriForgeDirection.UNKNOWN) {
            return dir;
        }
        double[] coords = tessellator.getTransformationMatrix().transform(dir.offsetX, dir.offsetY, dir.offsetZ);
        double[] translation = tessellator.getTransformationMatrix().getTranslation();
        coords[0] = coords[0] - translation[0];
        coords[1] = coords[1] - translation[1];
        coords[2] = coords[2] - translation[2];
        double x = Math.abs(coords[0]);
        double y = Math.abs(coords[1]);
        double z = Math.abs(coords[2]);
        if(x > z) {
            if(x > y) {
                return coords[0] > 0 ? AgriForgeDirection.EAST : AgriForgeDirection.WEST;
            }
        } else {
            if(z > y) {
                return coords[2] > 0 ? AgriForgeDirection.SOUTH : AgriForgeDirection.NORTH;
            }
        }
        return coords[1] > 0 ? AgriForgeDirection.UP : AgriForgeDirection.DOWN;
    }

    /** Gets a color multiplier factor for the given side (same values used by vanilla) */
    public static float getMultiplier(AgriForgeDirection side) {
        switch(side) {
            case DOWN: return 0.5F;
            case NORTH:
            case SOUTH: return 0.8F;
            case EAST:
            case WEST: return 0.6F;
            default: return 1;
        }
    }

    /**
     * Rotates a Plane. This is impressively useful, but may not be impressively efficient.
     * Always returns a 6-element array. Defaults to the north direction (the base direction).
     * <p>
     * This is for use up to the point that a way to rotate lower down is found. (IE. OpenGL).
     * </p>
     *
     * TODO: Test up/down rotations more thoroughly.
     */
    public static float[] rotatePlane(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, AgriForgeDirection direction) {
        float adj[] = new float[6];

        switch (direction) {
            default:
            case NORTH:
                adj[0] = minX; //-x
                adj[1] = minY; //-y
                adj[2] = minZ; //-z
                adj[3] = maxX; //+x
                adj[4] = maxY; //+y
                adj[5] = maxZ; //+z
                break;
            case EAST:
                adj[0] = Constants.WHOLE - maxZ; //-x
                adj[1] = minY; //-y
                adj[2] = minX; //-z
                adj[3] = Constants.WHOLE - minZ; //+x
                adj[4] = maxY; //+y
                adj[5] = maxX; //+z
                break;
            case SOUTH:
                adj[0] = minX; //-x
                adj[1] = minY; //-y
                adj[2] = Constants.WHOLE - maxZ; //-z
                adj[3] = maxX; //+x
                adj[4] = maxY; //+y
                adj[5] = Constants.WHOLE - minZ; //+z
                break;
            case WEST:
                adj[0] = minZ; //-x
                adj[1] = minY; //-y
                adj[2] = minX; //-z
                adj[3] = maxZ; //+x
                adj[4] = maxY; //+y
                adj[5] = maxX; //+z
                break;
            case UP:
                adj[0] = minX; //-x
                adj[1] = Constants.WHOLE - maxZ; //-y
                adj[2] = minY; //-z
                adj[3] = maxX; //+x
                adj[4] = Constants.WHOLE - minZ; //+y
                adj[5] = maxY; //+z
            case DOWN:
                adj[0] = minX; //-x
                adj[1] = minZ; //-y
                adj[2] = minY; //-z
                adj[3] = maxX; //+x
                adj[4] = maxZ; //+y
                adj[5] = maxY; //+z
        }
        return adj;
    }

    /**
     * utility method used for debugging rendering
     */
    @SuppressWarnings("unused")
    public static void drawAxisSystem(TessellatorV2 tessellator, boolean startDrawing) {
        if(startDrawing) {
            tessellator.startDrawingQuads();
        }

        tessellator.addVertexWithUV(-0.005F, 2, 0, 1, 0);
        tessellator.addVertexWithUV(0.005F, 2, 0, 0, 0);
        tessellator.addVertexWithUV(0.005F, -1, 0, 0, 1);
        tessellator.addVertexWithUV(-0.005F, -1, 0, 1, 1);

        tessellator.addVertexWithUV(2, -0.005F, 0, 1, 0);
        tessellator.addVertexWithUV(2, 0.005F, 0, 0, 0);
        tessellator.addVertexWithUV(-1, 0.005F, 0, 0, 1);
        tessellator.addVertexWithUV(-1, -0.005F, 0, 1, 1);

        tessellator.addVertexWithUV(0, -0.005F, 2, 1, 0);
        tessellator.addVertexWithUV(0, 0.005F, 2, 0, 0);
        tessellator.addVertexWithUV(0, 0.005F, -1, 0, 1);
        tessellator.addVertexWithUV(0, -0.005F, -1, 1, 1);

        if(startDrawing) {
            tessellator.draw();
        }
    }
}
