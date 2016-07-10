package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.tiles.TileEntitySeedAnalyzer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/*
 * Renders the TESR component of the Seed Analyzer.
 */
@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends TileEntitySpecialRenderer<TileEntitySeedAnalyzer> {

	@Override
	public void renderTileEntityAt(TileEntitySeedAnalyzer te, double x, double y, double z, float partialTicks, int destroyStage) {

		// Save Settings
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		// Translate to the location of our tile entity
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		// Render Seed
		if (te != null && te.hasSpecimen()) {
			// Draw Item
			RenderUtil.renderItemStack(te.getSpecimen(), 0.5, 0.5, 0.5, 0.75, true);
		}

		// Restore Settings
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();

	}

}
