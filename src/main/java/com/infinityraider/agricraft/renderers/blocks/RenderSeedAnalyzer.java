package com.infinityraider.agricraft.renderers.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzer;
import com.infinityraider.agricraft.renderers.models.ModelSeedAnalyzerBook;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockWithTileBase<BlockSeedAnalyzer, TileEntitySeedAnalyzer> {
    public static ResourceLocation TEXTURE_ANALYZER = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":blocks/seedAnalyzer");

    private static ModelTechne<ModelSeedAnalyzer> MODEL_ANALYZER = new ModelTechne<>(new ModelSeedAnalyzer());
    private static ModelTechne<ModelSeedAnalyzerBook> MODEL_BOOK = new ModelTechne<>(new ModelSeedAnalyzerBook());

    private List<BakedQuad> analyzerQuads;
    private List<BakedQuad> bookQuads;

    public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
        super(block, new TileEntitySeedAnalyzer(), true, true, true);
    }

    private void renderModel(ITessellator tessellator, EnumFacing direction, boolean journal) {
        tessellator.pushMatrix();
        rotateBlock(tessellator, direction);
        if (analyzerQuads == null) {
            analyzerQuads = MODEL_ANALYZER.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(TEXTURE_ANALYZER),1);
        }
        tessellator.addQuads(analyzerQuads);
        if(journal) {
            if (bookQuads == null) {
                bookQuads = MODEL_BOOK.getBakedQuads(tessellator.getVertexFormat(), this.getIcon(TEXTURE_ANALYZER), Constants.UNIT);
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
    public List<ResourceLocation> getAllTextures() {
        List<ResourceLocation> textures = new ArrayList<>();
        textures.add(TEXTURE_ANALYZER);
        return textures;
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, BlockSeedAnalyzer block,
                                 TileEntitySeedAnalyzer tile, boolean dynamicRender, float partialTick, int destroyStage) {
        if(dynamicRender) {
            tessellator.draw();
            this.renderSeed(tile, 0, 0, 0);
            tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        } else {
            this.renderModel(tessellator, tile.getOrientation(), tile.hasJournal());
        }

    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockSeedAnalyzer block, TileEntitySeedAnalyzer tile,
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
