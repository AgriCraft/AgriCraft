package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderValve extends RenderChannel {
    public RenderValve() {
        super(com.InfinityRaider.AgriCraft.init.Blocks.blockChannelValve, new TileEntityValve());
    }

    @Override
    protected void renderInInventory(ItemStack item, Object... data) {
        int cm = teDummy.colorMultiplier();
        TessellatorV2 tessellator = TessellatorV2.instance;
        //render the iron valves
        float f = Constants.UNIT;
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        
        //Render channel.
        drawScaledPrism(tessellator, 2, 4, 4, 14, 12, 5, cm);
        drawScaledPrism(tessellator, 2, 4, 11, 14, 12, 12, cm);
        drawScaledPrism(tessellator, 2, 4, 5, 14, 5, 11, cm);
        
        //Render separators. (USE IRON BLOCK TEXTURE HERE)
        drawScaledPrism(tessellator, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, cm);
        drawScaledPrism(tessellator, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, cm);
        drawScaledPrism(tessellator, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, cm);
        drawScaledPrism(tessellator, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, cm);

        //render the wooden guide rails along z-axis
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, cm);
        tessellator.addTranslation(0, 0, 6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, cm);
        tessellator.addTranslation(14*f, 0, 0);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, cm);
        tessellator.addTranslation(0, 0, -6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, cm);
        tessellator.addTranslation(-14*f, 0, 0);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * TODO: Use rotation to eliminate duplicate code.
     */
    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, boolean callFromTESR) {
        TileEntityValve valve = (TileEntityValve) tile;
        if (valve != null) {
            if (callFromTESR) {
                if(valve.getDiscreteFluidLevel() > 0) {
                    renderCallCounter.incrementAndGet();
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    tessellator.startDrawingQuads();
                    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                    this.drawWater(valve, tessellator);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                }
            } else {
                this.renderWoodChannel(valve, tessellator);
                if ((!this.shouldBehaveAsTESR()) && (valve.getDiscreteFluidLevel() > 0)) {
                    this.drawWater(valve, tessellator);
                }
                int cm = valve.colorMultiplier();
				for (ForgeDirection dir : TileEntityChannel.validDirections) {
					if (valve.hasNeighbourCheck(dir)) {
						if (valve.isPowered()) {
							//Draw closed separator.
							drawScaledPrism(tessellator, 6, 5, 0, 10, 12, 2, cm, dir);
						} else {
							//Draw open separator.
							drawScaledPrism(tessellator, 6, 1, 0, 10, 5.001F, 2, cm, dir);
							drawScaledPrism(tessellator, 6, 12, 0, 10, 15, 2, cm, dir);
						}
						//Draw rails.
						drawScaledPrism(tessellator, 4, 0, 0, 6, 16, 2, cm, dir);
						drawScaledPrism(tessellator, 10, 0, 0, 12, 16, 2, cm, dir);
					}
				}
            }
        }
        return true;
    }

    @Override
	protected void renderSide(TileEntityChannel channel, TessellatorV2 tessellator, ForgeDirection direction) {
		Block neighbour;
        IBlockState state;
		if (channel.getWorld() == null) {
			neighbour = null;
            state = null;
		} else {
            state = channel.getWorld().getBlockState(channel.getPos().add(direction.offsetX, 0, direction.offsetZ));
			neighbour = state.getBlock();
		}
		if (neighbour != null) {
			int cm = channel.colorMultiplier();
			if (neighbour instanceof BlockLever && state.getValue(BlockLever.FACING).getFacing() == direction.getEnumFacing()) {
				drawScaledPrism(tessellator, 5, 4, 0, 11, 12, 4, cm, direction);
			}
            /*
            else if (Loader.isModLoaded(Names.Mods.mcMultipart) && ForgeMultiPartHelper.isMultiPart(neighbour)) {
                if(ForgeMultiPartHelper.isLeverFacingThis(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord, direction)) {
                    IIcon icon = channel.getIcon();
                    drawScaledPrism(tessellator, 5, 4, 0, 11, 12, 4, cm, direction);
                }
			}
			*/
		}
		super.renderSide(channel, tessellator, direction);
	}

	@Override
	protected void connectWater(TileEntityChannel channel, TessellatorV2 tessellator, ForgeDirection direction, float y) {
		TileEntityValve valve = (TileEntityValve) channel;
		// checks if there is a neighboring block that this block can connect to
		if (channel.hasNeighbourCheck(direction)) {
			if (valve.isPowered()) {
				float y2 = valve.getFluidHeight();
				this.drawWaterEdge(tessellator, direction, y2, y2);
			} else {
				super.connectWater(channel, tessellator, direction, y);
			}
		}

	}
}