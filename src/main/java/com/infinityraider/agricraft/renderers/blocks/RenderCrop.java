package com.infinityraider.agricraft.renderers.blocks;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;

/*
 * Renders the TESR component of the Seed Analyzer.
 */
@SideOnly(Side.CLIENT)
public class RenderCrop extends TileEntitySpecialRenderer<TileEntityCrop> {
	
	@AgriConfigurable(key = "Render Seed In Crops", category = AgriConfigCategory.CLIENT, comment = "Set to true to see a floating model of the planted seed in the crop.")
	public static boolean RENDER_SEED = false;

	@Override
	public void renderTileEntityAt(TileEntityCrop crop, double x, double y, double z, float partialTicks, int destroyStage) {

		// Save Settings
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		// Translate to the location of our tile entity
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();

		// Render Plant
		if (crop != null && crop.hasPlant()) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			PlantRenderer.renderPlant(crop.getWorld(), BlockPos.ORIGIN, crop.getGrowthStage(), crop.getPlant());
			// Render Seed
			if (RENDER_SEED) {
				RenderUtil.renderItemStack(crop.getSeed().toStack(), 0.5, 0.5, 0.5, 0.75, true);
			}
		}

		// Restore Settings
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();

	}
	
	static {
		AgriCore.getConfig().addConfigurable(RenderCrop.class);
	}

}
