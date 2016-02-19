package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntitySprinkler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.icon.IconUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSprinkler extends RenderBlockBase {

	// Dimensions
	private static final float MIN_Y = 4.0F;
	private static final float MAX_Y = 8.0F;
	private static final float MIN_C = 7.0F;
	private static final float MAX_C = 9.0F;
	private static final float BLADE_W = 1.0F;
	private static final float BLADE_L = 5.0F;

	// Calculated
	private static final float BMX_Y = MIN_Y + BLADE_W;
	private static final float BMX_A = MIN_C - BLADE_L;
	private static final float BMX_B = MAX_C + BLADE_L;

	public RenderSprinkler() {
		super(AgriCraftBlocks.blockSprinkler, new TileEntitySprinkler(), true);
	}

	@Override
	protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {

		final TileEntitySprinkler sprinkler = (TileEntitySprinkler) tile;
		final TextureAtlasSprite icon = IconUtil.getIcon(Blocks.iron_block);

		// Prep. for render.
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		GL11.glDisable(GL11.GL_LIGHTING);

		// Rotate
		tessellator.pushMatrix();
		if (sprinkler != null) {
			tessellator.rotate(sprinkler.angle, 0, -1, 0);
		}

		// Render sprinkler head.
		if (callFromTESR) {
			tessellator.startDrawingQuads();
		}

		// Draw Core
		RenderUtil.drawScaledPrism(tessellator, MIN_C, MIN_Y, MIN_C, MAX_C, MAX_Y, MAX_C, icon);

		// Draw Blades
		RenderUtil.drawScaledPrism(tessellator, BMX_A, MIN_Y, MIN_C, BMX_B, BMX_Y, MAX_C, icon);
		RenderUtil.drawScaledPrism(tessellator, MIN_C, MIN_Y, BMX_A, MAX_C, BMX_Y, BMX_B, icon);

		// Undo Rotate.
		tessellator.popMatrix();

		// Draw the connector
		if (sprinkler != null) {
			RenderUtil.drawScaledPrism(tessellator, 4, 8, 4, 12, 20, 12, sprinkler.getChannelIcon());
		} else {
			RenderUtil.drawScaledPrism(tessellator, 4, 8, 4, 12, 16, 12, IconUtil.getIcon(IconUtil.OAK_PLANKS));
		}

		// Draw
		if (callFromTESR) {
			tessellator.draw();
		}
		
		// Clean Up
		GL11.glEnable(GL11.GL_LIGHTING);

		return true;
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
		this.doWorldRender(tessellator, null, 0, 0, 0, null, block, null, null, 0, 0, null, true);
	}

	@Override
	public boolean shouldBehaveAsTESR() {
		return true;
	}

	@Override
	public boolean shouldBehaveAsISBRH() {
		return true;
	}

}
