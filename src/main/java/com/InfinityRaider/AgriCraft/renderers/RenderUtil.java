package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
    private static final RenderUtil INSTANCE = new RenderUtil();
    public static final int COLOR_MULTIPLIER_STANDARD = 16777215;

    private RenderUtil() {}

    public static RenderUtil getInstance() {
        return INSTANCE;
    }

    /** Tessellates a vertex at the given position using the passed tessellator and texture u and v position */
    public void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    /** Tessellates a vertex at the given position using the passed tessellator, texture u and v position are interpolated using the passed icon */
    public void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v, TextureAtlasSprite icon) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x * unit, y * unit, z * unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    /** Draws both sides of a face parallel to the XY plane*/
    public void drawScaledFaceDoubleXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z) {
        z = z*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV, icon);
    }

    /** Draws both sides of a face parallel to the XZ plane*/
    public void drawScaledFaceDoubleXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y) {
        y = y*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV, icon);
        //back
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV, icon);
    }

    /** Draws both sides of a face parallel to the YZ plane*/
    public void drawScaledFaceDoubleYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x) {
        x = x*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV, icon);
        //back
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV, icon);
    }

    /** Draws a prism which is rotated along the given direction */
    public void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, int colorMultiplier, ForgeDirection direction) {
        float adj[] = rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawScaledPrism(tessellator, adj[0], adj[1], adj[2], adj[3], adj[4], adj[5], icon, colorMultiplier);
    }

    public void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        drawScaledPrism(tessellator, minX, minY ,minZ, maxX, maxY, maxZ, icon, COLOR_MULTIPLIER_STANDARD);
    }

    /** Draws a prism */
    public void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, int colorMultiplier) {
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
    public void drawScaledFaceFrontXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.SOUTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
    }

    /** Draws the front side of a face parallel to the XZ plane */
    public void drawScaledFaceFrontXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.UP);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
    }

    /** Draws the front side of a face parallel to the YZ plane */
    public void drawScaledFaceFrontYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.EAST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
    }

    /** Draws the back side of a face parallel to the XY plane */
    public void drawScaledFaceBackXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, TextureAtlasSprite icon, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.NORTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
    }

    /** Draws the back side of a face parallel to the XZ plane */
    public void drawScaledFaceBackXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, TextureAtlasSprite icon, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.DOWN);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
    }

    /** Draws the back side of a face parallel to the YZ plane */
    public void drawScaledFaceBackYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, TextureAtlasSprite icon, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.WEST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
    }

    /** Applies a color multiplier to the tessellator for a given side, the side is transformed according to the rotation of the tessellator */
    public void applyColorMultiplier(TessellatorV2 tessellator, int colorMultiplier, ForgeDirection side) {
        float preMultiplier = getMultiplier(transformSide(tessellator, side));
        float r = preMultiplier * ((float) (colorMultiplier >> 16 & 255) / 255.0F);
        float g = preMultiplier * ((float) (colorMultiplier >> 8 & 255) / 255.0F);
        float b = preMultiplier * ((float) (colorMultiplier & 255) / 255.0F);
        tessellator.setColorOpaque_F(r, g, b);
    }

    /** Transforms a direction according to the rotation of the tessellator */
    public ForgeDirection transformSide(TessellatorV2 tessellator, ForgeDirection dir) {
        if(dir==ForgeDirection.UNKNOWN) {
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
                return coords[0] > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
            }
        } else {
            if(z > y) {
                return coords[2] > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
            }
        }
        return coords[1] > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
    }

    /** Gets a color multiplier factor for the given side (same values used by vanilla) */
    public float getMultiplier(ForgeDirection side) {
        switch(side) {
            case DOWN: return 0.5F;
            case NORTH:
            case SOUTH: return 0.8F;
            case EAST:
            case WEST: return 0.6F;
            default: return 1;
        }
    }

    /** Draws a plane and rotates it according to the given direction */
    public void drawPlane(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon, ForgeDirection direction) {
        float[] rot = rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawPlane(tessellator, rot[0], rot[1], rot[2], rot[3], rot[4], rot[5], icon);
    }

    /** Helper method for the above method */
    private void drawPlane(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ, icon);
    }

    /**
     * Rotates a plane. This is impressively useful, but may not be impressively efficient.
     * Always returns a 6-element array. Defaults to the north direction (the base direction).
     * <p>
     * This is for use up to the point that a way to rotate lower down is found. (IE. OpenGL).
     * </p>
     *
     * TODO: Test up/down rotations more thoroughly.
     */
    public float[] rotatePrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, ForgeDirection direction) {
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
    public void drawAxisSystem(boolean startDrawing) {
        TessellatorV2 tessellator = TessellatorV2.instance;

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
