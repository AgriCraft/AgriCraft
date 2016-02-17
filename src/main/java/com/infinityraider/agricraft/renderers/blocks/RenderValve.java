package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityValve;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.icon.SafeIcon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
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

@SideOnly(Side.CLIENT)
public class RenderValve extends RenderChannel {
	
	private static final SafeIcon SEPARATOR_ICON = new SafeIcon(Blocks.iron_block);
	
    public RenderValve() {
        super(com.infinityraider.agricraft.init.AgriCraftBlocks.blockChannelValve, new TileEntityValve());
    }

    @Override
    protected void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
		
        TextureAtlasSprite icon = teDummy.getIcon();
        int cm = teDummy.colorMultiplier();
        //render the iron valves
        float f = Constants.UNIT;
		
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        
        //Render channel.
        drawScaledPrism(tessellator, 2, 4, 4, 14, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 2, 4, 11, 14, 12, 12, icon, cm);
        drawScaledPrism(tessellator, 2, 4, 5, 14, 5, 11, icon, cm);
        
        //Render separators.
        drawScaledPrism(tessellator, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, SEPARATOR_ICON.getIcon(), cm);
        drawScaledPrism(tessellator, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, SEPARATOR_ICON.getIcon(), cm);
        drawScaledPrism(tessellator, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, SEPARATOR_ICON.getIcon(), cm);
        drawScaledPrism(tessellator, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, SEPARATOR_ICON.getIcon(), cm);

        //render the wooden guide rails along z-axis
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.translate(0, 0, 6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.translate(14*f, 0, 0);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.translate(0, 0, -6*f);
        drawScaledPrism(tessellator, 0, 0, 3.999F, 2, 16, 5.999F, icon, cm);
        tessellator.translate(-14*f, 0, 0);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * TODO: Use rotation to eliminate duplicate code.
     */
    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
		
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

                final TextureAtlasSprite icon2 = valve.getIcon();
                int cm = valve.colorMultiplier();
                
				for (AgriForgeDirection dir : TileEntityChannel.VALID_DIRECTIONS) {
					if (valve.hasNeighbourCheck(dir)) {
						if (valve.isPowered()) {
							//Draw closed separator.
							drawScaledPrism(tessellator, 6, 5, 0, 10, 12, 2, SEPARATOR_ICON.getIcon(), cm, dir);
						} else {
							//Draw open separator.
							drawScaledPrism(tessellator, 6, 1, 0, 10, 5.001F, 2, SEPARATOR_ICON.getIcon(), cm, dir);
							drawScaledPrism(tessellator, 6, 12, 0, 10, 15, 2, SEPARATOR_ICON.getIcon(), cm, dir);
						}
						//Draw rails.
						drawScaledPrism(tessellator, 4, 0, 0, 6, 16, 2, icon2, cm, dir);
						drawScaledPrism(tessellator, 10, 0, 0, 12, 16, 2, icon2, cm, dir);
					}
				}
            }
        }
        return true;
    }

    @Override
	protected void renderSide(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection direction) {
		IBlockState neighbour;
		if (channel.getWorld() == null) {
			neighbour = null;
		} else {
			neighbour = channel.getWorld().getBlockState(channel.getPos().add(direction.offsetX, 0, direction.offsetZ));
		}
		if (neighbour != null) {
			int cm = channel.colorMultiplier();
			if (neighbour instanceof BlockLever && neighbour.getValue(BlockLever.FACING).getFacing() == direction.getEnumFacing()) {
				TextureAtlasSprite icon = channel.getIcon();
				drawScaledPrism(tessellator, 5, 4, 0, 11, 12, 4, icon, cm, direction);
			}
		}
		super.renderSide(channel, tessellator, direction);
	}

	@Override
	protected void connectWater(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection direction, float y, TextureAtlasSprite icon) {
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