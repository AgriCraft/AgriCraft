package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityTank;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.IconUtil;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {
	
	private static final TextureAtlasSprite WATER_ICON = IconUtil.getIcon(Blocks.waterlily);
	
    public RenderTank() {
        super(com.infinityraider.agricraft.init.AgriCraftBlocks.blockWaterTank, new TileEntityTank(), true);
    }

    @Override
    protected void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        this.renderTank(teDummy, tessellator, item.getItemDamage());
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        //call correct drawing methods
        boolean success = false;
        if (tile instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) tile;
            if(callFromTESR) {
                if(tank.getFluidHeight()>0) {
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                    drawWater(tank, tessellator);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                    success = true;
                }
            } else {
                success = renderTank(tank, tessellator, tank.getBlockMetadata());
            }
        }
        return success;
    }

	private boolean renderTank(TileEntityTank tank, TessellatorV2 tessellator, int meta) {
		this.drawWoodTank(tank, tessellator);
		// draw the waterTexture
		if ((!shouldBehaveAsTESR()) && (tank.getFluidHeight() > 0)) {
			this.drawWater(tank, tessellator);
		}
		return true;
	}

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }

    private void drawWoodTank(TileEntityTank tank, TessellatorV2 tessellator) {
        this.renderBottom(tank, tessellator);
        this.renderSide(tank, tessellator, AgriForgeDirection.NORTH);
        this.renderSide(tank, tessellator, AgriForgeDirection.EAST);
        this.renderSide(tank, tessellator, AgriForgeDirection.SOUTH);
        this.renderSide(tank, tessellator, AgriForgeDirection.WEST);
    }

    private void renderBottom(TileEntityTank tank, TessellatorV2 tessellator) {
        //the texture
        TextureAtlasSprite icon = tank.getIcon();
        int cm = tank.colorMultiplier();
        //bottom
        boolean bottom = !tank.hasNeighbour(AgriForgeDirection.DOWN);
        if (bottom) {
            drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon, cm);
        }
        //corners
        int yMin = bottom?1:0;
        if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
            drawScaledPrism(tessellator, 0, yMin, 0, 2, 16, 2, icon, cm);
        }
        if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
            drawScaledPrism(tessellator, 14, yMin, 0, 16, 16, 2, icon, cm);
        }
        if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
            drawScaledPrism(tessellator, 0, yMin, 14, 2, 16, 16, icon, cm);
        }
        if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
            drawScaledPrism(tessellator, 14, yMin, 14, 16, 16, 16, icon, cm);
        }
    }

    private void renderSide(TileEntityTank tank, TessellatorV2 tessellator, AgriForgeDirection dir) {
        //the texture
        TextureAtlasSprite icon = tank.getIcon();
        int cm = tank.colorMultiplier();
        int yMin = tank.hasNeighbour(AgriForgeDirection.DOWN)?0:1;
        if ((dir != null) && (dir != AgriForgeDirection.UNKNOWN)) {
            //connected to a channel
            if(tank.isConnectedToChannel(dir)) {
                drawScaledPrism(tessellator, 2, yMin, 0, 14, 5, 2, icon, cm, dir);
                drawScaledPrism(tessellator, 2, 5, 0, 5, 12, 2, icon, cm, dir);
                drawScaledPrism(tessellator, 11, 5, 0, 14, 12, 2, icon, cm, dir);
                drawScaledPrism(tessellator, 2, 12, 0, 14, 16, 2, icon, cm, dir);
            }
            //not connected to anything
            else if(!tank.hasNeighbour(dir)) {
                drawScaledPrism(tessellator, 2, yMin, 0, 14, 16, 2, icon, cm, dir);
            }
        }
    }

    private void drawWater(TileEntityTank tank, TessellatorV2 tessellator) {
        //only render water on the bottom layer
        if(tank.getYPosition()==0) {
            float y = tank.getFluidHeight()-0.01F; //-0.0001F to avoid Z-fighting on maximum filled tanks
            //the texture
            //stolen from Vanilla code
            int l = Blocks.water.colorMultiplier(tank.getWorld(), tank.getPos());
            float f = (float)(l >> 16 & 255) / 255.0F;
            float f1 = (float)(l >> 8 & 255) / 255.0F;
            float f2 = (float)(l & 255) / 255.0F;
            float f4 = 1.0F;
            tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorld(), tank.getPos()));
            tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
            //draw surface
            addScaledVertexWithUV(tessellator, 0, y, 0, 0, 0, WATER_ICON);
            addScaledVertexWithUV(tessellator, 0, y, 16, 0, 16, WATER_ICON);
            addScaledVertexWithUV(tessellator, 16, y, 16, 16, 16, WATER_ICON);
            addScaledVertexWithUV(tessellator, 16, y, 0, 16, 0, WATER_ICON);
        }
    }
}