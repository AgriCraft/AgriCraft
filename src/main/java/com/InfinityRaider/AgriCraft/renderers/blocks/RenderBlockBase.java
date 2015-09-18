package com.InfinityRaider.AgriCraft.renderers.blocks;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {
    private static HashMap<Block, Integer> renderIds = new HashMap<Block, Integer>();
    public static final int COLOR_MULTIPLIER_STANDARD = 16777215;

    private final Block block;

    protected RenderBlockBase(Block block, boolean inventory) {
        this(block, null, inventory);
    }

    protected RenderBlockBase(Block block, TileEntity te, boolean inventory) {
        this.block = block;
        if(!renderIds.containsKey(block)) {
            this.registerRenderer(block, te);
        }
        if(inventory) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), this);
        }
    }

    private void registerRenderer(Block block, TileEntity te) {
        if(te!=null && this.shouldBehaveAsTESR()) {
            ClientRegistry.bindTileEntitySpecialRenderer(te.getClass(), this);
            renderIds.put(block, -1);
        }
        if(this.shouldBehaveAsISBRH()) {
            int id = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(id, this);
            renderIds.put(block, id);
        }
    }

    //WORLD
    //-----
    private boolean renderBlock(Tessellator tessellator, IBlockAccess world, double x, double y, double z, Block block, TileEntity tile, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if (callFromTESR) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
        } else {
            if(tessellator instanceof TessellatorV2) {
                ((TessellatorV2) tessellator).setRotation(0, 0, 0, 0);
            }
            tessellator.addTranslation((float) x, (float) y, (float) z);
        }
        if (tile != null && tile instanceof TileEntityAgricraft) {
            rotateMatrix((TileEntityAgricraft) tile, tessellator, false);
        }

        tessellator.setColorRGBA_F(1, 1, 1, 1);

        boolean result = doWorldRender(tessellator, world, x, y, z, tile, block, f, modelId, renderer, callFromTESR);

        if (tile != null && tile instanceof TileEntityAgricraft) {
            rotateMatrix((TileEntityAgricraft) tile, tessellator, true);
        }
        if (callFromTESR) {
            GL11.glTranslated(-x, -y, -z);
            GL11.glPopMatrix();
        } else {
            if(tessellator instanceof TessellatorV2) {
                ((TessellatorV2) tessellator).setRotation(0, 0, 0, 0);
            }
            tessellator.addTranslation((float) -x, (float) -y, (float) -z);
        }

        return result;
    }

    @Override
    public final void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        renderBlock(Tessellator.instance, tileEntity.getWorldObj(), x, y, z, tileEntity.getBlockType(), tileEntity, f, 0, RenderBlocks.getInstance(), true);
    }

    @Override
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return renderBlock(TessellatorV2.instance, world, x, y, z, block, world.getTileEntity(x, y, z), 0, modelId, renderer, false);
    }

    protected abstract boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR);


    //INVENTORY
    //---------
    @Override
    public final void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public final boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public final boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public final boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public final void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        doInventoryRender(type, item, data);
        GL11.glPopMatrix();
    }

    protected abstract void doInventoryRender(ItemRenderType type, ItemStack item, Object... data);


    //HELPER METHODS
    //--------------
    public final Block getBlock() {
        return this.block;
    }

    public abstract boolean shouldBehaveAsTESR();

    public abstract boolean shouldBehaveAsISBRH();

    @Override
    public final int getRenderId() {
        return AgriCraft.proxy.getRenderId(this.getBlock());
    }

    public static int getRenderId(Block block) {
        return renderIds.containsKey(block)?renderIds.get(block):-1;
    }


    //UTILITY METHODS
    //---------------
    protected void rotateMatrix(TileEntityAgricraft tileEntityAgricraft, Tessellator tessellator, boolean inverse) {
        //+x = EAST
        //+z = SOUTH
        //-x = WEST
        //-z = NORTH
        if(!tileEntityAgricraft.isRotatable()) {
            return;
        }
        rotateMatrix(tileEntityAgricraft.getOrientation(), tessellator, inverse);
    }
    
	protected void rotateMatrix(ForgeDirection dir, Tessellator tessellator, boolean inverse) {
		float angle;
		switch (dir) {
			default:
			case NORTH:
				angle = 0;
				break;
			case WEST:
				angle = 90;
				break;
			case SOUTH:
				angle = 180;
				break;
			case EAST:
				angle = 270;
				break;
		}
		float dx = angle % 270 == 0 ? 0 : -1;
		float dz = angle > 90 ? -1 : 0;
		if (tessellator instanceof TessellatorV2) {
			TessellatorV2 tessellatorV2 = (TessellatorV2) tessellator;
			if (inverse) {
				tessellatorV2.addTranslation(-dx, 0, -dz);
				tessellatorV2.addRotation(-angle, 0, 1, 0);
			} else {
				tessellatorV2.addRotation(angle, 0, 1, 0);
				tessellatorV2.addTranslation(dx, 0, dz);
			}
		} else {
			if (inverse) {
				GL11.glTranslatef(-dx, 0, -dz);
				GL11.glRotatef(-angle, 0, 1, 0);
			} else {
				GL11.glRotatef(angle, 0, 1, 0);
				GL11.glTranslatef(dx, 0, dz);
			}
		}
	}

    //adds a vertex to the tessellator scaled with 1/16th of a block
    protected void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    //same as above method, but does not require the correct texture to be bound
    protected void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x * unit, y * unit, z * unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    protected void drawScaledFaceDoubleXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z) {
        z = z*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
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

    protected void drawScaledFaceDoubleXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y) {
        y = y*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
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


    protected void drawScaledFaceDoubleYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x) {
        x = x*16.0F;
        float minU = 0;
        float maxU = icon.getIconWidth();
        float minV = 0;
        float maxV = icon.getIconHeight();
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

    //draws a rectangular prism
    protected void drawScaledPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon, int colorMultiplier) {
        //bottom
        drawScaledFaceBackXZ(tessellator, minX, minZ, maxX, maxZ, icon, minY/16.0F, colorMultiplier);
        //top
        drawScaledFaceFrontXZ(tessellator, minX, minZ, maxX, maxZ, icon, maxY/16.0F, colorMultiplier);
        //back
        drawScaledFaceBackXY(tessellator, minX, minY, maxX, maxY, icon, minZ/16.0F, colorMultiplier);
        //front
        drawScaledFaceFrontXY(tessellator, minX, minY, maxX, maxY, icon, maxZ/16.0F, colorMultiplier);
        //left
        drawScaledFaceBackYZ(tessellator, minY, minZ, maxY, maxZ, icon, minX/16.0F, colorMultiplier);
        //right
        drawScaledFaceFrontYZ(tessellator, minY, minZ, maxY, maxZ, icon, maxX/16.0F, colorMultiplier);

    }
    
    /**
     * Draws a prism.
     * <p>
     * The prism coordinates range from 0 to {@link Constants#WHOLE}.
     * </p><p>
     * The prism is relative to the bottom left corner of the block.
     * </p>
     * @param tessellator
     * @param minX
     * @param minY
     * @param minZ
     * @param maxX
     * @param maxY
     * @param maxZ
     * @param icon
     * @param colorMultiplier
     * @param direction
     */
    protected void drawScaledPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon, int colorMultiplier, ForgeDirection direction) {
        float adj[] = RenderHelper.rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawScaledPrism(tessellator, adj[0], adj[1], adj[2], adj[3], adj[4], adj[5], icon, colorMultiplier);
    }

    protected void drawScaledFaceFrontXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z, int colorMultiplier) {
        //z positive
        z = z*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        //front
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
    }

    protected void drawScaledFaceFrontXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y, int colorMultiplier) {
        //y positive
        y = y*16.0F;
        //front
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
    }

    protected void drawScaledFaceFrontYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x, int colorMultiplier) {
        //x positive
        x = x*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        //front
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
    }

    protected void drawScaledFaceBackXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z, int colorMultiplier) {
        //z negative
        z = z*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        //back
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV, icon);
    }

    protected void drawScaledFaceBackXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y, int colorMultiplier) {
        //y negative
        y = y*16.0F;
        //back
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ, icon);
    }

    protected void drawScaledFaceBackYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x, int colorMultiplier) {
        //x negative
        x = x*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        //back
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV, icon);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV, icon);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV, icon);
    }
    
    protected void drawPlane(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon, ForgeDirection direction) {
    	float[] rot = RenderHelper.rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawPlane(tessellator, rot[0], rot[1], rot[2], rot[3], rot[4], rot[5], icon);
    }
    
    private void drawPlane(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
    	addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ, icon);
    }
}
