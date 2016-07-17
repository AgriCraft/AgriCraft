package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorVertexBuffer;
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
public class RenderWaterTank extends TileEntitySpecialRenderer<TileEntityTank> {

	@Override
	public void renderTileEntityAt(TileEntityTank te, double x, double y, double z, float partialTicks, int destroyStage) {

		// Save Settings
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		// Translate to the location of our tile entity
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		// Render Water
		if (te != null && te.getYPosition() == 0 && te.getFluidHeight() > 0) {
			// Get Tess.
			final ITessellator tess = TessellatorVertexBuffer.getInstance();
			// Fix Settings
			tess.setColorRGBA(255, 255, 255, 255);
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			// Start
			tess.startDrawingQuads(DefaultVertexFormats.BLOCK);
			// Render
			final float waterLvl = te.getFluidHeight() - 0.01F;
			final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
			tess.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, waterIcon, waterLvl);
			// End
			tess.draw();
		}

		// Restore Settings
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();

	}

}
