package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {
    public RenderTank() {
        super(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, new TileEntityTank(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(teDummy.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        //draw first plane front
        addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 0, 16, 0, 16);
        addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 0);
        //draw first plane back
        addScaledVertexWithUV(tessellator, 0, 16, 14, 0, 0);
        addScaledVertexWithUV(tessellator, 16, 16, 14, 16, 0);
        addScaledVertexWithUV(tessellator, 16, 0, 14, 16, 16);
        addScaledVertexWithUV(tessellator, 0, 0, 14, 0, 16);
        //draw first plane top
        addScaledVertexWithUV(tessellator, 0, 16, 14, 0, 14);
        addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 14, 16, 14);
        //draw second plane front
        addScaledVertexWithUV(tessellator, 16, 16, 16, 0, 0);
        addScaledVertexWithUV(tessellator, 16, 0, 16, 0, 16);
        addScaledVertexWithUV(tessellator, 16, 0, 0, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
        //draw second plane back
        addScaledVertexWithUV(tessellator, 14, 16, 16, 0, 0);
        addScaledVertexWithUV(tessellator, 14, 16, 0, 16, 0);
        addScaledVertexWithUV(tessellator, 14, 0, 0, 16, 16);
        addScaledVertexWithUV(tessellator, 14, 0, 16, 0, 16);
        //draw second plane top
        addScaledVertexWithUV(tessellator, 14, 16, 0, 14, 0);
        addScaledVertexWithUV(tessellator, 14, 16, 16, 14, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
        //draw third plane front
        addScaledVertexWithUV(tessellator, 16, 16, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16);
        addScaledVertexWithUV(tessellator, 0, 0, 0, 16, 16);
        addScaledVertexWithUV(tessellator, 0, 16, 0, 16, 0);
        //draw third plane back
        addScaledVertexWithUV(tessellator, 16, 16, 2, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 16, 2, 16, 0);
        addScaledVertexWithUV(tessellator, 0, 0, 2, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 0, 2, 0, 16);
        //draw third plane top
        addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 16, 2, 0, 2);
        addScaledVertexWithUV(tessellator, 16, 16, 2, 16, 2);
        addScaledVertexWithUV(tessellator, 16, 16, 0, 16, 0);
        //draw fourth plane front
        addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 16);
        addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 0, 16, 16, 16, 0);
        //draw fourth plane back
        addScaledVertexWithUV(tessellator, 2, 16, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 2, 16, 16, 16, 0);
        addScaledVertexWithUV(tessellator, 2, 0, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 2, 0, 0, 0, 16);
        //draw fourth plane top
        addScaledVertexWithUV(tessellator, 0, 16, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 16, 16, 0, 16);
        addScaledVertexWithUV(tessellator, 2, 16, 16, 2, 16);
        addScaledVertexWithUV(tessellator, 2, 16, 0, 2, 0);
        //draw bottom plane front
        addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16);
        addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 0);
        //draw bottom plane back
        addScaledVertexWithUV(tessellator, 0, 1, 0, 0, 0);
        addScaledVertexWithUV(tessellator, 0, 1, 16, 16, 0);
        addScaledVertexWithUV(tessellator, 16, 1, 16, 16, 16);
        addScaledVertexWithUV(tessellator, 16, 1, 0, 0, 16);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        //call correct drawing methods
        if (tile instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tile;
            if(tank.getBlockMetadata()==0) {
                this.drawWoodTank(tank, tessellator);
                //draw the waterTexture
                if(tank.getScaledDiscreteFluidLevel()>0) {
                    this.drawWater(tank, tessellator);
                }
            }
            else if(tank.getBlockMetadata()==1) {
                this.drawIronTank(tank, tessellator);
            }
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        return true;
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }

    private void drawWoodTank(TileEntityTank tank, Tessellator tessellator) {
        this.renderBottom(tank, tessellator);
        this.renderSide(tank, tessellator, 'x', -1);
        this.renderSide(tank, tessellator, 'x', 1);
        this.renderSide(tank, tessellator, 'z', -1);
        this.renderSide(tank, tessellator, 'z', 1);
    }

    private void renderBottom(TileEntityTank tank, Tessellator tessellator) {
        if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord, tank.yCoord-1, tank.zCoord))) {
            //the texture
            IIcon icon = tank.getIcon();
            //draw bottom plane front
            addScaledVertexWithUV(tessellator, 0, 0, 0, 0, 0, icon);
            addScaledVertexWithUV(tessellator, 16, 0, 0, 0, 16, icon);
            addScaledVertexWithUV(tessellator, 16, 0, 16, 16, 16, icon);
            addScaledVertexWithUV(tessellator, 0, 0, 16, 16, 0, icon);
            //draw bottom plane back
            addScaledVertexWithUV(tessellator, 0, 1, 0, 0, 0, icon);
            addScaledVertexWithUV(tessellator, 0, 1, 16, 16, 0, icon);
            addScaledVertexWithUV(tessellator, 16, 1, 16, 16, 16, icon);
            addScaledVertexWithUV(tessellator, 16, 1, 0, 0, 16, icon);
        }
    }

    private void renderSide(TileEntityTank tank, Tessellator tessellator, char axis, int direction) {
        //the texture
        IIcon icon = tank.getIcon();
        if ((axis == 'x' || axis == 'z') && (direction == 1 || direction == -1)) {
            boolean x = axis=='x';
            if(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))instanceof TileEntityChannel && ((TileEntityChannel) tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction))).isSameMaterial(tank)) {
                //draw plane front top
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 0 : (9 + 7 * direction), 0, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 11, x ? 0 : (9 + 7 * direction), 0, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 11, x ? 16 : (9 + 7 * direction), 16, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 16, x ? 16 : (9 + 7 * direction), 16, 0, icon);
                //draw plane front left
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 11, x ? 0 : (9 + 7 * direction), 0, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 5, x ? 0 : (9 + 7 * direction), 0, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 5, x ? 5 : (9 + 7 * direction), 5, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 11, x ? 5 : (9 + 7 * direction), 5, 5, icon);
                //draw plane front bottom
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 5, x ? 0 : (9 + 7 * direction), 0, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 0, x ? 0 : (9 + 7 * direction), 0, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 0, x ? 16 : (9 + 7 * direction), 16, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 5, x ? 16 : (9 + 7 * direction), 16, 11, icon);
                //draw plane front right
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 11, 11, x ? 11 : (9 + 7 * direction), 11, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 11, 5, x ? 11 : (9 + 7 * direction), 11, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 5, x ? 16 : (9 + 7 * direction), 16, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 11, x ? 16 : (9 + 7 * direction), 16, 5, icon);
                //draw plane back top
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 16, x ? 0 : (7 + 7 * direction), 0, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 16 : (7 + 7 * direction), 16, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 11, x ? 16 : (7 + 7 * direction), 16, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 11, x ? 0 : (7 + 7 * direction), 0, 5, icon);
                //draw plane back left
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 11, x ? 0 : (7 + 7 * direction), 0, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 5, 11, x ? 5 : (7 + 7 * direction), 5, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 5, 5, x ? 5 : (7 + 7 * direction), 5, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 5, x ? 0 : (7 + 7 * direction), 0, 11, icon);
                //draw plane back bottom
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 5, x ? 0 : (7 + 7 * direction), 0, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 5, x ? 16 : (7 + 7 * direction), 16, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 0, x ? 16 : (7 + 7 * direction), 16, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 0, x ? 0 : (7 + 7 * direction), 0, 16, icon);
                //draw plane back right
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 11, x ? 11 : (7 + 7 * direction), 11, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 11, x ? 16 : (7 + 7 * direction), 16, 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 5, x ? 16 : (7 + 7 * direction), 16, 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 5, x ? 11 : (7 + 7 * direction), 11, 11, icon);
                //draw hole bottom plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 5, x ? 5 : (7 + 7 * direction), x ? (7 + 7 * direction) : 5, x ? 5 : (7 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 5, x ? 11 : (9 + 7 * direction), x ? (7 + 7 * direction) : 5, x ? 11 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 5, x ? 11 : (9 + 7 * direction), x ? (9 + 7 * direction) : 11, x ? 11 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 5, x ? 5 : (7 + 7 * direction), x ? (9 + 7 * direction) : 11, x ? 5 : (7 + 7 * direction), icon);
                //draw hole right plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 11, 11, x ? 5 : (7 + 7 * direction), (7 + 7 * direction), 5, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 11, 5, x ? 5 : (7 + 7 * direction), (7 + 7 * direction), 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 5, x ? 5 : (9 + 7 * direction), (9 + 7 * direction), 11, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 11, x ? 5 : (9 + 7 * direction), (9 + 7 * direction), 5, icon);
                //draw hole top plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 11, x ? 5 : (7 + 7 * direction), x ? (7 + 7 * direction) : 5, x ? 5 : (7 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 11, x ? 5 : (7 + 7 * direction), x ? (9 + 7 * direction) : 11, x ? 5 : (7 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 11, 11, x ? 11 : (9 + 7 * direction), x ? (9 + 7 * direction) : 11, x ? 11 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 11, x ? 11 : (9 + 7 * direction), x ? (7 + 7 * direction) : 5, x ? 11 : (9 + 7 * direction), icon);
                //draw hole left plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 11, x ? 11 : (7 + 7 * direction), (7 + 7 * direction), 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 5, 11, x ? 11 : (9 + 7 * direction), (9 + 7 * direction), 5, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 5, 5, x ? 11 : (9 + 7 * direction), (9 + 7 * direction), 11, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 5, 5, x ? 11 : (7 + 7 * direction), (7 + 7 * direction), 11, icon);
                //draw plane top
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 0 : (7 + 7 * direction), x ? (7 + 7 * direction) : 0, x ? 0 : (7 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 16 : (9 + 7 * direction), x ? (7 + 7 * direction) : 0, x ? 16 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 16 : (9 + 7 * direction), x ? (9 + 7 * direction) : 16, x ? 16 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 0 : (7 + 7 * direction), x ? (9 + 7 * direction) : 16, x ? 0 : (7 + 7 * direction), icon);
            }
            else if(!tank.isMultiBlockPartner(tank.getWorldObj().getTileEntity(tank.xCoord+(x?direction:0), tank.yCoord, tank.zCoord+(x?0:direction)))) {
                //draw front plane
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 16, x ? 16 : (9 + 7 * direction), 0, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 0, 0, x ? 16 : (9 + 7 * direction), 0, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 0, x ? 0 : (9 + 7 * direction), 16, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 0 : (9 + 7 * direction), 16, 0, icon);
                //draw back plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 16 : (7 + 7 * direction), 0, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 16, x ? 0 : (7 + 7 * direction), 16, 0, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 16, 0, x ? 0 : (7 + 7 * direction), 16, 16, icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 0, x ? 16 : (7 + 7 * direction), 0, 16, icon);
                //draw top plane
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 0 : (7 + 7 * direction), x ? (7 + 7 * direction) : 0, x ? 0 : (7 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (7 + 7 * direction) : 0, 16, x ? 16 : (9 + 7 * direction), x ? (7 + 7 * direction) : 0, x ? 16 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 16 : (9 + 7 * direction), x ? (9 + 7 * direction) : 16, x ? 16 : (9 + 7 * direction), icon);
                addScaledVertexWithUV(tessellator, x ? (9 + 7 * direction) : 16, 16, x ? 0 : (7 + 7 * direction), x ? (9 + 7 * direction) : 16, x ? 0 : (7 + 7 * direction), icon);
            }
        }
    }

    private void drawIronTank(TileEntityTank tank, Tessellator tessellator) {
        IIcon icon = tank.getIcon();
    }

    private void drawWater(TileEntityTank tank, Tessellator tessellator) {
        //only render water on the bottom layer
        if(tank.getYPosition()==0) {
            float y = tank.getScaledDiscreteFluidY()-0.01F; //-0.0001F to avoid Z-fighting on maximum filled tanks
            //the texture
            IIcon icon = Blocks.water.getIcon(1,0);
            //stolen from Vanilla code
            int l = Blocks.water.colorMultiplier(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord);
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorldObj(), tank.xCoord, tank.yCoord, tank.zCoord));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            //draw surface
            addScaledVertexWithUV(tessellator, 0, y, 0, 0, 0, icon);
            addScaledVertexWithUV(tessellator, 0, y, 16, 0, 16, icon);
            addScaledVertexWithUV(tessellator, 16, y, 16, 16, 16, icon);
            addScaledVertexWithUV(tessellator, 16, y, 0, 16, 0, icon);
        }
    }
}