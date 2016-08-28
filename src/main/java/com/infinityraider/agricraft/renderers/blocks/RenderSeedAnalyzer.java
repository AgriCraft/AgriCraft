package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzerBook;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.block.RenderBlockTile;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockTile<BlockSeedAnalyzer, TileEntitySeedAnalyzer> {
	private static final ModelTechne<ModelSeedAnalyzer> MODEL_ANALYZER = new ModelTechne<>(new ModelSeedAnalyzer());
	private static final ModelTechne<ModelSeedAnalyzerBook> MODEL_BOOK = new ModelTechne<>(new ModelSeedAnalyzerBook());

	private List<BakedQuad> analyzerQuads;
	private List<BakedQuad> bookQuads;

	public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
		super(block, new TileEntitySeedAnalyzer(), true, true, true);
	}

	private void renderModel(ITessellator tessellator, EnumFacing direction, boolean journal) {
		tessellator.pushMatrix();
		direction = (direction == null) ? EnumFacing.NORTH : direction;
		int angle = (90 * direction.getHorizontalIndex() + 180) % 360;
		if(angle != 0) {
			tessellator.translate(0.5, 0, 0.5);
			tessellator.rotate(angle, 0, 1, 0);
			tessellator.translate(-0.5, 0, -0.5);
		}
		if (analyzerQuads == null) {
            analyzerQuads = MODEL_ANALYZER.getBakedQuads(tessellator.getVertexFormat(), RenderUtilBase.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER),1);
		}
        tessellator.addQuads(analyzerQuads);
		if(journal) {
            if (bookQuads == null) {
                bookQuads = MODEL_BOOK.getBakedQuads(tessellator.getVertexFormat(), RenderUtilBase.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER), Constants.UNIT);
            }
            tessellator.addQuads(bookQuads);
        }
        tessellator.setApplyDiffuseLighting(false);
		tessellator.popMatrix();
	}

	@Override
	public void renderDynamicTile(ITessellator tess, TileEntitySeedAnalyzer tile, float partialTicks, int destroyStage) {
		// Render Seed
		if (tile.hasSpecimen()) {
			// Correct Draw Mode
			tess.draw();
			// Draw Item
			RenderUtilBase.renderItemStack(tile.getSpecimen(), 0.5, 0.5, 0.5, 0.75, true);
			// Correct Draw Mode
			tess.startDrawingQuads(DefaultVertexFormats.BLOCK);
		}
	}

	@Override
	protected void renderStaticTile(ITessellator tess, TileEntitySeedAnalyzer tile) {
		this.renderModel(tess, tile.getOrientation(), tile.hasJournal());
	}

	@Override
	public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
		renderModel(tessellator, EnumFacing.SOUTH, true);
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return null;
	}

	@Override
	public boolean applyAmbientOcclusion() {
		return false;
	}
}
