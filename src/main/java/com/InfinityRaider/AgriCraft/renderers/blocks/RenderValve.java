package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.compatibility.forgemultipart.ForgeMultiPartHelper;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.LeverHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderValve extends RenderChannel {
    public RenderValve() {
        super(com.InfinityRaider.AgriCraft.init.Blocks.blockChannelValve, new TileEntityValve());
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        IIcon icon = teDummy.getIcon();
        int cm = teDummy.colorMultiplier();
        Tessellator tessellator = Tessellator.instance;
        //render the iron valves
        float f = Constants.UNIT;
        IIcon ironIcon = Blocks.iron_block.getIcon(0, 0);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        
        //Render channel.
        drawScaledPrism(tessellator, 2, 4, 4, 14, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 2, 4, 11, 14, 12, 12, icon, cm);
        drawScaledPrism(tessellator, 2, 4, 5, 14, 5, 11, icon, cm);
        
        //Render separators.
        drawScaledPrism(tessellator, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, ironIcon, cm);
        drawScaledPrism(tessellator, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, ironIcon, cm);

        //render the wooden guide rails along z-axis
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(0, 0, 6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(14*f, 0, 0);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(0, 0, -6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.addTranslation(-14*f, 0, 0);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * TODO: Use rotation to eliminate duplicate code.
     */
    @Override
    protected boolean doWorldRender(Tessellator tessellator2, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        Tessellator tessellator1 = Tessellator.instance;
        TileEntityValve valve = (TileEntityValve) tile;
        if (valve != null) {
            if (callFromTESR) {
                if(valve.getDiscreteFluidLevel() > 0) {
                    renderCallCounter.incrementAndGet();
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_LIGHTING);
                    tessellator1.startDrawingQuads();
                    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                    this.drawWater(valve, tessellator1);
                    tessellator1.draw();
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                }
            } else {
                this.renderWoodChannel(valve, tessellator2);
                if ((!this.shouldBehaveAsTESR()) && (valve.getDiscreteFluidLevel() > 0)) {
                    this.drawWater(valve, tessellator2);
                }

                //render the iron valves
                IIcon icon = Blocks.iron_block.getIcon(0, 0);
                IIcon icon2 = valve.getIcon();
                int cm = valve.colorMultiplier();
                
				for (ForgeDirection dir : TileEntityChannel.validDirections) {
					if (valve.hasNeighbourCheck(dir)) {
						if (valve.isPowered()) {
							//Draw closed separator.
							drawScaledPrism(tessellator2, 6, 5, 0, 10, 12, 2, icon, cm, dir);
						} else {
							//Draw open separator.
							drawScaledPrism(tessellator2, 6, 1, 0, 10, 5.001F, 2, icon, cm, dir);
							drawScaledPrism(tessellator2, 6, 12, 0, 10, 15, 2, icon, cm, dir);
						}
						//Draw rails.
						drawScaledPrism(tessellator2, 4, 0, 0, 6, 16, 2, icon2, cm, dir);
						drawScaledPrism(tessellator2, 10, 0, 0, 12, 16, 2, icon2, cm, dir);
					}
				}
            }
        }
        return true;
    }

    @Override
	protected void renderSide(TileEntityChannel channel, Tessellator tessellator, ForgeDirection direction) {
		Block neighbour;
		if (channel.getWorldObj() == null) {
			neighbour = null;
		} else {
			neighbour = channel.getWorldObj().getBlock(channel.xCoord + direction.offsetX, channel.yCoord, channel.zCoord + direction.offsetZ);
		}
		if (neighbour != null) {
			int cm = channel.colorMultiplier();
			if (neighbour instanceof BlockLever && LeverHelper.isLeverFacingBlock(channel.getWorldObj().getBlockMetadata(channel.xCoord + direction.offsetX, channel.yCoord, channel.zCoord + direction.offsetZ), direction)) {
				IIcon icon = channel.getIcon();
				drawScaledPrism(tessellator, 5, 4, 0, 11, 12, 4, icon, cm, direction);
			} else if (Loader.isModLoaded(Names.Mods.mcMultipart) && ForgeMultiPartHelper.isMultiPart(neighbour)) {
                if(ForgeMultiPartHelper.isLeverFacingThis(channel.getWorldObj(), channel.xCoord, channel.yCoord, channel.zCoord, direction)) {
                    IIcon icon = channel.getIcon();
                    drawScaledPrism(tessellator, 5, 4, 0, 11, 12, 4, icon, cm, direction);
                }
			}
		}
		super.renderSide(channel, tessellator, direction);
	}

	@Override
	protected void connectWater(TileEntityChannel channel, Tessellator tessellator, ForgeDirection direction, float y, IIcon icon) {
		TileEntityValve valve = (TileEntityValve) channel;
		// checks if there is a neighboring block that this block can connect to
		if (channel.hasNeighbourCheck(direction)) {
			if (valve.isPowered()) {
				float y2 = valve.getFluidHeight();
				this.drawWaterEdge(tessellator, direction, y2, y2, icon);
			} else {
				super.connectWater(channel, tessellator, direction, y, icon);
			}
		}

	}
}