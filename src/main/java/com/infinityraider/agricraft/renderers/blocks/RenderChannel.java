package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.IIrrigationComponent;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.AgriForgeDirection;
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

import static com.InfinityRaider.AgriCraft.renderers.RenderUtil.*;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel extends RenderBlockCustomWood<TileEntityChannel> {
    public static AtomicInteger renderCallCounter = new AtomicInteger(0);

    public RenderChannel() {
        this(com.InfinityRaider.AgriCraft.init.AgriCraftBlocks.blockWaterChannel, new TileEntityChannel());
    }

    protected RenderChannel(Block block, TileEntityChannel channel) {
        super(block, channel, true);
    }

    @Override
    protected void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        tessellator.startDrawingQuads();
        this.renderBottom(teDummy, tessellator);
        this.renderSide(teDummy, tessellator, AgriForgeDirection.NORTH);
        this.renderSide(teDummy, tessellator, AgriForgeDirection.EAST);
        this.renderSide(teDummy, tessellator, AgriForgeDirection.SOUTH);
        this.renderSide(teDummy, tessellator, AgriForgeDirection.WEST);
        tessellator.draw();
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        //call correct drawing methods
        if (tile instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) tile;
            if (channel.getBlockMetadata() == 0) {
                if (callFromTESR) {
                    if(channel.getDiscreteFluidLevel()>0) {
                        renderCallCounter.incrementAndGet();
                        GL11.glPushMatrix();
                        GL11.glDisable(GL11.GL_LIGHTING);
                        tessellator.startDrawingQuads();
                        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                        this.drawWater(channel, tessellator);
                        tessellator.draw();
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glPopMatrix();
                    }
                } else {
                    this.renderWoodChannel(channel, tessellator);
                    if ((!this.shouldBehaveAsTESR()) && channel.getDiscreteFluidLevel() > 0) {
                        this.drawWater(channel, tessellator);
                    }
                }
            } else if (channel.getBlockMetadata() == 1) {
                this.renderIronChannel(channel, tessellator);
            }
        }
        //clear texture overrides
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

    protected void renderWoodChannel(TileEntityChannel channel, TessellatorV2 tessellator) {
        this.renderBottom(channel, tessellator);
        this.renderSide(channel, tessellator, AgriForgeDirection.NORTH);
        this.renderSide(channel, tessellator, AgriForgeDirection.EAST);
        this.renderSide(channel, tessellator, AgriForgeDirection.SOUTH);
        this.renderSide(channel, tessellator, AgriForgeDirection.WEST);
    }

    protected void renderBottom(TileEntityChannel channel, TessellatorV2 tessellator) {
        //the texture
        TextureAtlasSprite icon = channel.getIcon();
        int cm = channel.colorMultiplier();
        //bottom
        drawScaledPrism(tessellator, 4, 4, 4, 12, 5, 12, icon, cm);
        //corners
        drawScaledPrism(tessellator, 4, 5, 4, 5, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 11, 5, 4, 12, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 4, 5, 11, 5, 12, 12, icon, cm);
        drawScaledPrism(tessellator, 11, 5, 11, 12, 12, 12, icon, cm);
    }

    //renders one of the four sides of a channel
	protected void renderSide(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection dir) {
		// the texture
		TextureAtlasSprite icon = channel.getIcon();
		int cm = channel.colorMultiplier();
		if (channel.hasNeighbourCheck(dir)) {
			// extend bottom plane and side edges
			drawScaledPrism(tessellator, 4, 4, 0, 12, 5, 4, icon, cm, dir);
			drawScaledPrism(tessellator, 4, 5, 0, 5, 12, 5, icon, cm, dir);
			drawScaledPrism(tessellator, 11, 5, 0, 12, 12, 5, icon, cm, dir);
		} else {
			// draw an edge
			drawScaledPrism(tessellator, 4, 4, 4, 12, 12, 5, icon, cm, dir);
		}
	}

    private void renderIronChannel(TileEntityChannel channel, TessellatorV2 tessellator) {
    	// Do nothing ATM.
    }

    protected void drawWater(TileEntityChannel channel, TessellatorV2 tessellator) {
        float y = channel.getFluidHeight();
        //the texture
        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO: find water icon
        //stolen from Vanilla code
        int l = Blocks.water.colorMultiplier(channel.getWorld(), channel.getPos());
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;
        float f4 = 1.0F;
        tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(channel.getWorld(), channel.getPos()));
        tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

        //draw central water levels
        drawScaledFaceFrontXZ(tessellator, 5, 5, 11, 11, icon, y-0.001f, COLOR_MULTIPLIER_STANDARD);
        //connect to edges
        this.connectWater(channel, tessellator, AgriForgeDirection.NORTH, y, icon);
        this.connectWater(channel, tessellator, AgriForgeDirection.EAST, y, icon);
        this.connectWater(channel, tessellator, AgriForgeDirection.SOUTH, y, icon);
        this.connectWater(channel, tessellator, AgriForgeDirection.WEST, y, icon);
    }

	protected void connectWater(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection direction, float y, TextureAtlasSprite icon) {
		// checks if there is a neighboring block that this block can connect to
		if (channel.hasNeighbourCheck(direction)) {
			IIrrigationComponent te = channel.getNeighbor(direction);
			float y2;
			if (te instanceof TileEntityChannel) {
				if (te instanceof TileEntityValve && ((TileEntityValve) te).isPowered()) {
					y2 = y;
				} else {
					y2 = (y + te.getFluidHeight()) / 2;
				}
			} else {
				float lvl = (te.getFluidHeight() - 16 * ((TileEntityTank) te).getYPosition());
				y2 = lvl > 12 ? 12 : lvl < 5 ? (5 - 0.0001F) : lvl;
			}
			this.drawWaterEdge(tessellator, direction, y, y2, icon);
		}
	}

    protected void drawWaterEdge(TessellatorV2 tessellator, AgriForgeDirection direction, float lvl1, float lvl2, TextureAtlasSprite icon) {
    	drawScaledFaceFrontXZ(tessellator, 5, 0, 11, 5, icon, lvl2-0.001f, COLOR_MULTIPLIER_STANDARD);
    }
}