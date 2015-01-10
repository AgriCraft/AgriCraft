package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;


public class RenderValve implements ISimpleBlockRenderingHandler {

    private static final ForgeDirection[] DIRECTIONS = new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH,
        ForgeDirection.EAST, ForgeDirection.WEST};

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.addTranslation(x, y, z);
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        if (tileEntity instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) tileEntity;
            if (valve.getBlockMetadata() == 0) {
                renderWoodValve(valve, tessellator);
            } else if (valve.getBlockMetadata() == 1) {
                throw new UnsupportedOperationException("Iron valves not implemented yet!");
            }
        }

        renderer.clearOverrideBlockTexture();
        tessellator.addTranslation(-x, -y, -z);
        return true;
    }


    private void renderWoodValve(TileEntityValve valve, Tessellator tessellator) {
        // renderBottom(valve, tessellator);
        renderSides(valve, tessellator);
        // renderSide(valve, tessellator, 'x', -1);
        // renderSide(valve, tessellator, 'x', 1);
        // renderSide(valve, tessellator, 'z', -1);
        // renderSide(valve, tessellator, 'z', 1);
        // renderTop(valve, tessellator);
    }

    private void renderSides(TileEntityValve valve, Tessellator tessellator) {
        IIcon icon = valve.getIcon();

        for (ForgeDirection direction : DIRECTIONS) {
            boolean posZ = direction.offsetZ > 0;
            boolean posX = direction.offsetX > 0;

            if (direction.offsetX == 0) {
                RenderHelper.addScaledVertexWithUV(tessellator, posZ ? 4 : 12, posZ ? 12 : 4, posZ ? 12 : 4, posZ? 4 : 12, posZ? 4 : 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, posZ ? 12 : 4, posZ? 4 : 12, posZ ? 12 : 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, posZ ? 12 : 4, posZ ? 4 : 12, posZ ? 12 : 4, posZ ? 12 : 4, posZ ? 12 : 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, posZ ? 12 : 4, posZ ? 12 : 4, posZ? 4 : 12, icon);
            } else {
                RenderHelper.addScaledVertexWithUV(tessellator, posX ? 12 : 4, posX ? 4 : 12, posX ? 12 : 4, posX ? 4 : 12, posX ? 12 : 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, posX ? 12 : 4, 4, 4, 4, 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, posX ? 12 : 4, posX ? 12 : 4, posX ? 4 : 12, posX ? 12 : 4, posX ? 4 : 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, posX ? 12 : 4, 12, 12, 12, 12, icon);
            }
        }

    }

    private void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        //the texture
        IIcon icon = channel.getIcon();


        //draw first plane front
        //RenderHelper.addScaledVertexWithUV(tessellator, 0, 2, 0, 0, 0, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 16, 2, 0, 16, 0, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 16, 2, 16, 16, 16, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 0, 2, 16, 0, 16, icon);

        //draw first plane back
        //RenderHelper.addScaledVertexWithUV(tessellator, 2, 4, 2, 2, 2, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 2, 4, 14, 2, 14, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 14, 4, 14, 14, 14, icon);
        //RenderHelper.addScaledVertexWithUV(tessellator, 14, 4, 2, 14, 2, icon);
    }

    private void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            //checks if there is a neighbouring block that this block can connect to
            boolean neighbour = channel.hasNeighbour(axis, direction);
            boolean x = axis == 'x';
            //the texture
            IIcon icon = channel.getIcon();
            if(neighbour) {
                //extend bottom plane
                //draw bottom plane front
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 5, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 5, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction), icon);
                //draw bottom plane back
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?4:(6+6*direction), x?6*(direction+1):4, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(6+6*direction), x?(10.5F+direction*5.5F):12, x?4:(6+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?12:(10+6*direction), x?(10.5F+direction*5.5F):12, x?12:(10+6*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?6*(direction+1):4, 4, x?12:(10+6*direction), x?6*(direction+1):4, x?12:(10+6*direction), icon);
                //draw side edges
                //draw first edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(1+direction):4, 12, x?12:5.5F*(1+direction), 5.5F*(direction+1), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 4, x?12:5.5F*(1+direction), 5.5F*(direction+1), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 4, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4, icon);
                //draw first edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, x?12:4, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 4, x?11:5.5F*(1+direction), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, x?4:12, x?11:5.5F*(1+direction), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4, icon);
                //draw first edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):5, 12, x?11:(5.5F*(1+direction)), x?5.5F*(direction+1):5, x?11:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):4, 12, x?12:(5.5F*(1+direction)), x?5.5F*(direction+1):4, x?12:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):4, 12, x?12:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):4, x?12:(10.5F+5.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):5, 12, x?11:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):5, x?11:(10.5F+5.5F*direction), icon);
                //draw second edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 4, x?5:(5.5F*(1+direction)), 5.5F*(direction+1), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 4, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), (10.5F+direction*5.5F), 4, icon);
                //draw second edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(10.5F+5.5F*direction), x?5.5F*(direction+1):(16-(10.5F+5.5F*direction)), 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, x?12:4, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):(16-(10.5F+5.5F*direction)), x?4:12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 4, x?4:(5.5F*(1+direction)), x?(10.5F+direction*5.5F):(16-5.5F*(1+direction)), 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, x?4:12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):(16-5.5F*(1+direction)), x?12:4, icon);
                //draw second edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):12, 12, x?4:(5.5F*(1+direction)), x?5.5F*(direction+1):12, x?4:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?5.5F*(direction+1):11, 12, x?5:(5.5F*(1+direction)), x?5.5F*(direction+1):11, x?5:(5.5F*(1+direction)), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):11, 12, x?5:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):11, x?5:(10.5F+5.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(10.5F+direction*5.5F):12, 12, x?4:(10.5F+5.5F*direction), x?(10.5F+direction*5.5F):12, x?4:(10.5F+5.5F*direction), icon);
            }
            else {
                //draw an edge
                //draw edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9.5F+3.5F*direction):4, 12, x?12:(7.5F+3.5F*direction), 4, 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9.5F+3.5F*direction):4, 4, x?12:(7.5F+3.5F*direction), 4, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9.5F+3.5F*direction):12, 4, x?4:(7.5F+3.5F*direction), 12, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(9.5F+3.5F*direction):12, 12, x?4:(7.5F+3.5F*direction), 12, 4, icon);
                //draw edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), 4, 4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, x?4:12, x?4:(7.5F+3.5F*direction), x?4:12, x?12:4, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, 4, x?12:(7.5F+3.5F*direction), 12, 12, icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, x?12:4, x?12:(7.5F+3.5F*direction), x?12:4, x?4:12, icon);
                //draw edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?4:(7.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?12:(8.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?12:(8.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?12:(8.5F+3.5F*direction), icon);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(7.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?4:(7.5F+3.5F*direction), icon);
            }
        }
    }

    private void renderTop(TileEntityValve valve, Tessellator tessellator) {
        IIcon icon = valve.getIcon();
        //draw first plane front
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 4, 4, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 12, 4, 12, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 12, 12, 12, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 4, 12, 4, icon);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.valveId);
    }
}
