package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzerBook;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.render.block.RenderBlockBase;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockBase<BlockSeedAnalyzer, TileEntitySeedAnalyzer> {
	private static ModelTechne<ModelSeedAnalyzer> MODEL_ANALYZER = new ModelTechne<>(new ModelSeedAnalyzer());
	private static ModelTechne<ModelSeedAnalyzerBook> MODEL_BOOK = new ModelTechne<>(new ModelSeedAnalyzerBook());

	private Map<EnumFacing, List<BakedQuad>> analyzerQuads;
	private Map<EnumFacing, List<BakedQuad>> bookQuads;

	public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
		super(block, new TileEntitySeedAnalyzer(), true, true, true);
		this.analyzerQuads = new HashMap<>();
		this.bookQuads = new HashMap<>();
	}

	private void renderModel(ITessellator tessellator, TileEntitySeedAnalyzer tile) {
		EnumFacing dir = tile.getOrientation();
		if(tile.hasJournal()) {
			if (!bookQuads.containsKey(dir)) {
				tessellator.rotate(dir.getHorizontalAngle(), 0, 1, 0);
				tessellator.addQuads(MODEL_ANALYZER.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER), 1));
				tessellator.addQuads(MODEL_BOOK.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER), 1));
				bookQuads.put(dir, tessellator.getQuads());
			} else {
				tessellator.addQuads(bookQuads.get(dir));
			}
		} else {
			if (!analyzerQuads.containsKey(dir)) {
				tessellator.rotate(dir.getHorizontalAngle(), 0, 1, 0);
				tessellator.addQuads(MODEL_ANALYZER.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER), 1));
				analyzerQuads.put(dir, tessellator.getQuads());
			} else {
				tessellator.addQuads(analyzerQuads.get(dir));
			}
		}
	}

	private void renderSeed(TileEntitySeedAnalyzer te, double x, double y, double z) {
		// Save Settings
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		// Translate to the location of our tile entity
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		// Render Seed
		if (te != null && te.hasSpecimen()) {
			// Draw Item
			this.renderItemStack(te.getSpecimen(), 0.5, 0.5, 0.5, 0.75, true);
		}

		// Restore Settings
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, BlockSeedAnalyzer block,
								 @Nullable TileEntitySeedAnalyzer tile, boolean dynamicRender, float partialTick, int destroyStage) {
		if(dynamicRender) {
			this.renderSeed(tile, x, y, z);
		} else {
			this.renderModel(tessellator, tile);
		}

	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockSeedAnalyzer block, @Nullable TileEntitySeedAnalyzer tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

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
