package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase implements ISimpleBlockRenderingHandler {
    private final int id;
    private final Block block;

    protected RenderBlockBase(Block block, int id) {
        this.id = id;
        this.block = block;
    }

    @Override
    public final void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        doInventoryRender(block, metadata, modelId, renderer);
    }

    protected abstract void doInventoryRender(Block block, int meta, int model, RenderBlocks renderer);

    @Override
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Class<? extends TileEntity> clazz = getTileEnityClass();
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null) {
            if (clazz != null) {
                return false;
            }
        } else if (clazz != null && !clazz.isInstance(tileEntity)) {
            return false;
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.addTranslation(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityAgricraft) {
            rotateMatrix((TileEntityAgricraft) tileEntity, false);
        }
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        boolean result = doWorldRender(tessellator, world, x, y, z, block, modelId, renderer);

        if (tileEntity != null && tileEntity instanceof TileEntityAgricraft) {
            rotateMatrix((TileEntityAgricraft) tileEntity, true);
        }
        tessellator.addTranslation(-x, -y, -z);

        return result;
    }

    private void rotateMatrix(TileEntityAgricraft tileEntityAgricraft, boolean inverse) {
        //+x = EAST
        //+z = SOUTH
        //-x = WEST
        //-z = NORTH
        if(!tileEntityAgricraft.isRotatable()) {
            return;
        }
        float angle;
        switch(tileEntityAgricraft.getOrientation()) {
            case WEST: angle = 180; break;
            case SOUTH: angle = 90; break;
            case NORTH: angle = 270; break;
            default: return;
        }
        float dx = angle>90?-1:0;
        float dz = angle>180?0:-1;
        if(inverse) {
            GL11.glTranslatef(-dx, 0, -dz);
            GL11.glRotatef(-angle, 0, 1, 0);
        } else {
            GL11.glRotatef(angle, 0, 1, 0);
            GL11.glTranslatef(dx, 0, dz);
        }
    }

    protected abstract boolean doWorldRender(Tessellator tessellator, IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer);

    protected abstract Class<? extends TileEntity> getTileEnityClass();

    public final Block getBlock() {
        return this.block;
    }

    @Override
    public int getRenderId() {
        return id;
    }
}
