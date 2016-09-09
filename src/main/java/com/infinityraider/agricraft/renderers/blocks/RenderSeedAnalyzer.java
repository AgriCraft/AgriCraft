package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.renderers.AgriObjModelLoader;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;

@SideOnly(Side.CLIENT)
public class RenderSeedAnalyzer extends RenderBlockWithTileBase<BlockSeedAnalyzer, TileEntitySeedAnalyzer> {

    private static final String seedAnalyzerObj = "agricraft:models/block/seed_analyzer.obj";
    private static final String seedBookObj = "agricraft:models/block/seed_analyzer_book.obj";

    public RenderSeedAnalyzer(BlockSeedAnalyzer block) {
        super(block, new TileEntitySeedAnalyzer(), true, true, true);
    }

    private void renderModel(ITessellator tessellator, EnumFacing direction, boolean journal) {
        tessellator.pushMatrix();
        rotateBlock(tessellator, direction);
        AgriObjModelLoader.getBasicObjQuads(seedAnalyzerObj, tessellator.getVertexFormat(), tessellator::getIcon).ifPresent(tessellator::addQuads);
        if (journal) {
            AgriObjModelLoader.getBasicObjQuads(seedBookObj, tessellator.getVertexFormat(), tessellator::getIcon).ifPresent(tessellator::addQuads);
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
        AgriObjModelLoader.getObjModel(seedAnalyzerObj).map(m -> m.getTextures()).ifPresent(textures::addAll);
        AgriObjModelLoader.getObjModel(seedBookObj).map(m -> m.getTextures()).ifPresent(textures::addAll);
        return textures;
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, BlockSeedAnalyzer block,
            TileEntitySeedAnalyzer tile, boolean dynamicRender, float partialTick, int destroyStage) {
        if (dynamicRender) {
            final VertexFormat form = tessellator.getVertexFormat();
            tessellator.draw();
            this.renderSeed(tile, 0, 0, 0);
            tessellator.startDrawingQuads(form);
        } else {
            this.renderModel(tessellator, tile.getOrientation(), tile.hasJournal());
        }

    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockSeedAnalyzer block, TileEntitySeedAnalyzer tile,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        renderModel(tessellator, EnumFacing.NORTH, true);
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
