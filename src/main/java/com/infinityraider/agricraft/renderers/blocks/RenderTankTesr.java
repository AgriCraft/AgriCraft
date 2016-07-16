package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorVertexBuffer;
import com.infinityraider.agricraft.tiles.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.utility.BaseIcons;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

/*
 * Renders the TESR component of the Seed Analyzer.
 */
@SideOnly(Side.CLIENT)
public class RenderTankTesr extends TileEntitySpecialRenderer<TileEntityTank> {

	@Override
	public void renderTileEntityAt(TileEntityTank te, double x, double y, double z, float partialTicks, int destroyStage) {

		// Save Settings
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		// Translate to the location of our tile entity
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		// Render Water
		if (te != null && te.getFluidAmount(0) > 0) {
			// Get Tess.
			final ITessellator tess = TessellatorVertexBuffer.getInstance();
			// Test
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			// Get Icon
			final TextureAtlasSprite water = BaseIcons.WATER_STILL.getIcon();
			// Get Height
			final float height = te.getFluidAmount(0) / (float)te.getCapacity();
			// Start
			tess.startDrawingQuads(DefaultVertexFormats.BLOCK);
			// Render
			tess.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.UP, water, height);
			// End
			tess.draw();
		}

		// Restore Settings
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();

	}

}
