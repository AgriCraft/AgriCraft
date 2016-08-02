package com.infinityraider.agricraft.renderers.blocks;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.block.RenderBlockTile;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockTile<BlockCrop, TileEntityCrop> {

	public RenderCrop(BlockCrop block) {
		super(block, new TileEntityCrop(), false, true, true);
	}

	static {
		AgriCore.getConfig().addConfigurable(RenderCrop.class);
	}

	@Override
	protected void renderStaticTile(ITessellator tess, TileEntityCrop tile) {
		TextureAtlasSprite sprite = RenderUtilBase.getIcon(BlockCrop.TEXTURE);
		tess.translate(0, -3 * Constants.UNIT, 0);
		tess.drawScaledPrism(2, 0, 2, 3, 16, 3, sprite);
		tess.drawScaledPrism(13, 0, 2, 14, 16, 3, sprite);
		tess.drawScaledPrism(13, 0, 13, 14, 16, 14, sprite);
		tess.drawScaledPrism(2, 0, 13, 3, 16, 14, sprite);
		tess.translate(0, 3 * Constants.UNIT, 0);
		if (tile.isCrossCrop()) {
			tess.drawScaledPrism(0, 10, 2, 16, 11, 3, sprite);
			tess.drawScaledPrism(0, 10, 13, 16, 11, 14, sprite);
			tess.drawScaledPrism(2, 10, 0, 3, 11, 16, sprite);
			tess.drawScaledPrism(13, 10, 0, 14, 11, 16, sprite);
		}
	}

	@Override
	public void renderDynamicTile(ITessellator tess, TileEntityCrop crop, float partialTicks, int destroyStage) {
		if (crop.hasPlant()) {
			PlantRenderer.renderPlant(tess, crop.getPlant(), crop.getBlockMetadata());
		}
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return RenderUtilBase.getIcon(BlockCrop.TEXTURE);
	}

	@Override
	public boolean applyAmbientOcclusion() {
		return true;
	}

}
