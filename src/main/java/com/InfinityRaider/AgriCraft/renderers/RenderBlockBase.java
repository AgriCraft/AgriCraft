package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {
    private static HashMap<Block, Integer> renderIds = new HashMap<Block, Integer>();

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
            if (tile != null && tile instanceof TileEntityAgricraft) {
                rotateMatrix((TileEntityAgricraft) tile, false);
            }
        } else {
            tessellator.addTranslation((float) x, (float) y, (float) z);
        }

        tessellator.setColorRGBA_F(1, 1, 1, 1);
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, (int) x, (int) y, (int) z));

        boolean result = doWorldRender(tessellator, world, x, y, z, tile, block, 0, modelId, renderer, callFromTESR);

        if (callFromTESR) {
            if (tile != null && tile instanceof TileEntityAgricraft) {
                rotateMatrix((TileEntityAgricraft) tile, true);
            }
            GL11.glTranslated(-x, -y, -z);
            GL11.glPopMatrix();
        } else {
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
        return renderBlock(Tessellator.instance, world, x, y, z, block, world.getTileEntity(x, y, z), 0, modelId, renderer, false);
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
        doInventoryRender(type, item, data);
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
    private void rotateMatrix(TileEntityAgricraft tileEntityAgricraft, boolean inverse) {
        //+x = EAST
        //+z = SOUTH
        //-x = WEST
        //-z = NORTH
        if(!tileEntityAgricraft.isRotatable()) {
            return;
        }
        float angle=0;
        switch(tileEntityAgricraft.getOrientation()) {
            case SOUTH: angle = 180; break;
            case WEST: angle = 90; break;
            case NORTH: angle = 0; break;
            case EAST: angle = 270; break;
            default: return;
        }
        float dx = angle%270==0?0:-1;
        float dz = angle>90?-1:0;
        if(inverse) {
            GL11.glTranslatef(-dx, 0, -dz);
            GL11.glRotatef(-angle, 0, 1, 0);
        } else {
            GL11.glRotatef(angle, 0, 1, 0);
            GL11.glTranslatef(dx, 0, dz);
        }
    }

    //adds a vertex to the tessellator scaled with 1/16th of a block
    protected void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    //same as above method, but does not require the correct texture to be bound
    protected void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x * unit, y * unit, z * unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    protected void drawScaledFaceXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z) {
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

    protected void drawFaceXY(Tessellator tessellator, float minX, float minY, float maxX, float maxY, IIcon icon, float z) {
        drawScaledFaceXY(tessellator, minX * 16, minY * 16, maxX * 16, maxY * 16, icon, z);
    }

    protected void drawScaledFaceXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y) {
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

    protected void drawFaceXZ(Tessellator tessellator, float minX, float minZ, float maxX, float maxZ, IIcon icon, float y) {
        drawScaledFaceXY(tessellator, minX * 16, minZ * 16, maxX * 16, maxZ * 16, icon, y);
    }

    protected void drawScaledFaceYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x) {
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

    protected void drawFaceYZ(Tessellator tessellator, float minY, float minZ, float maxY, float maxZ, IIcon icon, float x) {
        drawScaledFaceYZ(tessellator, minY * 16, minZ * 16, maxY * 16, maxZ * 16, icon, x);
    }

    //draws a rectangular prism
    protected void drawScaledPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        //front plane
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, 16 - maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, 16-maxY, icon);
        //back plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, 16-minY, icon);
        //right plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, minZ, 16-maxY, icon);
        //left plane
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, maxZ, 16-minY, icon);
        //top plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, maxZ, icon);
        //bottom plane
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, minZ, icon);
    }

    //draws a rectangular prism
    protected void drawPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        drawScaledPrism(tessellator, minX*16, minY*16, minZ*16, maxX*16, maxY*16, maxZ*16, icon);
    }
}
