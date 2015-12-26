package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockBase;
import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.IIrrigationComponent;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel extends RenderBlockCustomWood<TileEntityChannel> {
    public static AtomicInteger renderCallCounter = new AtomicInteger(0);

    public RenderChannel() {
        this(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, new TileEntityChannel());
    }

    protected RenderChannel(Block block, TileEntityChannel channel) {
        super(block, channel, true);
    }

    @Override
    protected void renderInInventory(ItemStack item, Object... data) {
        TessellatorV2 tessellator = TessellatorV2.instance;
        tessellator.startDrawingQuads();
        TextureAtlasSprite texture = ItemBlockCustomWood.getTextureFromStack(item);
        this.renderBottom(teDummy, tessellator, texture);
        this.renderSide(teDummy, tessellator, ForgeDirection.NORTH, texture);
        this.renderSide(teDummy, tessellator, ForgeDirection.EAST, texture);
        this.renderSide(teDummy, tessellator, ForgeDirection.SOUTH, texture);
        this.renderSide(teDummy, tessellator, ForgeDirection.WEST, texture);
        tessellator.draw();
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, IBlockState state, BlockBase block, TileEntity tile, int modelId, float f) {
        //call correct drawing methods
        if (tile instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) tile;
            tessellator.startDrawingQuads();
            if (channel.getBlockMetadata() == 0) {
                if (channel.getDiscreteFluidLevel() > 0) {
                    renderCallCounter.incrementAndGet();
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    this.drawWater(channel, tessellator);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                }
                TextureAtlasSprite texture = channel.getTexture(state, null);
                this.renderWoodChannel(channel, tessellator, texture);
                if (channel.getDiscreteFluidLevel() > 0) {
                    this.drawWater(channel, tessellator);
                }

            } else if (channel.getBlockMetadata() == 1) {
                this.renderIronChannel(channel, tessellator);
            }
            tessellator.draw();
        }
        return true;
    }

    protected void renderWoodChannel(TileEntityChannel channel, TessellatorV2 tessellator, TextureAtlasSprite texture) {
        this.renderBottom(channel, tessellator, texture);
        this.renderSide(channel, tessellator, ForgeDirection.NORTH, texture);
        this.renderSide(channel, tessellator, ForgeDirection.EAST, texture);
        this.renderSide(channel, tessellator, ForgeDirection.SOUTH, texture);
        this.renderSide(channel, tessellator, ForgeDirection.WEST, texture);
    }

    protected void renderBottom(TileEntityChannel channel, TessellatorV2 tessellator, TextureAtlasSprite texture) {
        //the texture
        int cm = channel.colorMultiplier();
        //bottom
        drawScaledPrism(tessellator, 4, 4, 4, 12, 5, 12, cm, texture);
        //corners
        drawScaledPrism(tessellator, 4, 5, 4, 5, 12, 5, cm, texture);
        drawScaledPrism(tessellator, 11, 5, 4, 12, 12, 5, cm, texture);
        drawScaledPrism(tessellator, 4, 5, 11, 5, 12, 12, cm, texture);
        drawScaledPrism(tessellator, 11, 5, 11, 12, 12, 12, cm, texture);
    }

    //renders one of the four sides of a channel
	protected void renderSide(TileEntityChannel channel, TessellatorV2 tessellator, ForgeDirection dir, TextureAtlasSprite texture) {
		// the texture
		int cm = channel.colorMultiplier();
		if (channel.hasNeighbourCheck(dir)) {
			// extend bottom plane and side edges
			drawScaledPrism(tessellator, 4, 4, 0, 12, 5, 4, cm, dir, texture);
			drawScaledPrism(tessellator, 4, 5, 0, 5, 12, 5, cm, dir, texture);
			drawScaledPrism(tessellator, 11, 5, 0, 12, 12, 5, cm, dir, texture);
		} else {
			// draw an edge
			drawScaledPrism(tessellator, 4, 4, 4, 12, 12, 5, cm, dir, texture);
		}
	}

    private void renderIronChannel(TileEntityChannel channel, TessellatorV2 tessellator) {
    	// Do nothing ATM.
    }

    protected void drawWater(TileEntityChannel channel, TessellatorV2 tessellator) {
        float y = channel.getFluidHeight();
        //the texture
        //stolen from Vanilla code
        int l = Blocks.water.colorMultiplier(channel.getWorld(), channel.getPos());
        float f = (float)(l >> 16 & 255) / 255.0F;
        float f1 = (float)(l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;
        float f4 = 1.0F;
        tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(channel.getWorld(), channel.getPos()));
        tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

        //draw central water levels
        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        drawPlane(tessellator, 5, y-0.001f, 5, 11, y-0.001f, 11, ForgeDirection.NORTH, texture);
        //connect to edges
        this.connectWater(channel, tessellator, ForgeDirection.NORTH, y, texture);
        this.connectWater(channel, tessellator, ForgeDirection.EAST, y, texture);
        this.connectWater(channel, tessellator, ForgeDirection.SOUTH, y, texture);
        this.connectWater(channel, tessellator, ForgeDirection.WEST, y, texture);
    }

	protected void connectWater(TileEntityChannel channel, TessellatorV2 tessellator, ForgeDirection direction, float y, TextureAtlasSprite texture) {
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
			this.drawWaterEdge(tessellator, direction, y, y2, texture);
		}
	}

    protected void drawWaterEdge(TessellatorV2 tessellator, ForgeDirection direction, float lvl1, float lvl2, TextureAtlasSprite texture ) {
    	drawPlane(tessellator, 5, lvl1-0.001f, 0, 11, lvl2-0.001f, 5, direction, texture);
    }
}