package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzerBook;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.reference.Constants;
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
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockBase<BlockSeedAnalyzer, TileEntitySeedAnalyzer> {
	private static ModelTechne<ModelSeedAnalyzer> MODEL_ANALYZER = new ModelTechne<>(new ModelSeedAnalyzer());
	private static ModelTechne<ModelSeedAnalyzerBook> MODEL_BOOK = new ModelTechne<>(new ModelSeedAnalyzerBook());

	private List<BakedQuad> analyzerQuads;
	private List<BakedQuad> bookQuads;

	public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
		super(block, new TileEntitySeedAnalyzer(), true, true, true);
	}

	private void renderModel(ITessellator tessellator, EnumFacing direction, boolean journal) {
		tessellator.pushMatrix();
		int angle = (90 * direction.getHorizontalIndex() + 180) % 360;
		if(angle != 0) {
			tessellator.translate(0.5, 0, 0.5);
			tessellator.rotate(angle, 0, 1, 0);
			tessellator.translate(-0.5, 0, -0.5);
		}
		if (analyzerQuads == null) {
            analyzerQuads = MODEL_ANALYZER.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER),1);
		}
        tessellator.addQuads(analyzerQuads);
		if(journal) {
            if (bookQuads == null) {
                bookQuads = MODEL_BOOK.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(BlockSeedAnalyzer.TEXTURE_ANALYZER), Constants.UNIT);
            }
            tessellator.addQuads(bookQuads);
        }
        tessellator.setApplyDiffuseLighting(false);
		tessellator.popMatrix();
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
			this.renderModel(tessellator, tile.getOrientation(), tile.hasJournal());
		}

	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockSeedAnalyzer block, @Nullable TileEntitySeedAnalyzer tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        renderModel(tessellator, EnumFacing.NORTH, false);
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
