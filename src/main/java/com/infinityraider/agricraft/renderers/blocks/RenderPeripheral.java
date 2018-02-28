package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockPeripheral;
import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.renderers.models.ModelPeripheralProbe;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.IconHelper;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
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

public class RenderPeripheral extends RenderBlockWithTileBase<BlockPeripheral, TileEntityPeripheral> {

    public static final ResourceLocation TEXTURE_TOP = new ResourceLocation("agricraft:blocks/peripheral_top");
    public static final ResourceLocation TEXTURE_SIDE = new ResourceLocation("agricraft:blocks/peripheral_side");
    public static final ResourceLocation TEXTURE_BOTTOM = new ResourceLocation("agricraft:blocks/peripheral_bottom");
    public static final ResourceLocation TEXTURE_INNER = new ResourceLocation("agricraft:blocks/peripheral_inner");
    public static final ResourceLocation TEXTURE_PROBE = new ResourceLocation("agricraft:blocks/peripheral_probe");

    private static final ModelTechne<ModelPeripheralProbe> MODEL_PERIPHERAL = new ModelTechne<>(new ModelPeripheralProbe()).setDiffuseLighting(false);

    private List<BakedQuad> probeQuads;

    public RenderPeripheral(BlockPeripheral block) {
        super(block, new TileEntityPeripheral(), true, true, true);
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        final List<ResourceLocation> list = new ArrayList<>();
        list.add(TEXTURE_TOP);
        list.add(TEXTURE_SIDE);
        list.add(TEXTURE_BOTTOM);
        list.add(TEXTURE_INNER);
        list.add(TEXTURE_PROBE);
        return list;
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return IconHelper.getIcon(TEXTURE_PROBE.toString());
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, BlockPeripheral block, EnumFacing side) {
        this.renderChasis(tessellator);
        this.renderProbe(tessellator);
    }

    @Override
    public void renderWorldBlockDynamic(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z,
            BlockPeripheral block, TileEntityPeripheral tile, float partialTick, int destroyStage, float alpha) {
        tessellator.draw();
        this.renderSeed(tile, 0, 0, 0);
        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockPeripheral block, TileEntityPeripheral tile,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        renderChasis(tessellator);
        renderProbe(tessellator);
    }

    private void renderChasis(ITessellator tessellator) {
        // Fetch Icons
        final TextureAtlasSprite iconTop = getIcon(TEXTURE_TOP);
        final TextureAtlasSprite iconSide = getIcon(TEXTURE_SIDE);
        final TextureAtlasSprite iconBottom = getIcon(TEXTURE_BOTTOM);
        final TextureAtlasSprite iconInside = getIcon(TEXTURE_INNER);

        // Render Top
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, iconTop, 16);
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.DOWN, iconTop, 14);

        // Render Bottom
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, iconInside, 2);
        tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.DOWN, iconBottom, 0);

        // Render Sides - Don't Ask Why This Works...
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            // Push Matrix
            tessellator.pushMatrix();
            // Rotate The Block
            rotateBlock(tessellator, side);
            // Render Outer Face
            tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.NORTH, iconSide, 0);
            // Render Inner Face
            tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, iconInside, 4);
            // Pop Matrix
            tessellator.popMatrix();
        }
    }

    private void renderProbe(ITessellator tessellator) {
        // Create Quads, If Needed
        if (probeQuads == null) {
            probeQuads = MODEL_PERIPHERAL.getBakedQuads(tessellator.getVertexFormat(), getIcon(TEXTURE_PROBE));
        }
        
        // Add Probe Quads
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            // Push Matrix
            tessellator.pushMatrix();
            // Rotate The Block
            rotateBlock(tessellator, side);
            // Add probe for the side.
            tessellator.addQuads(probeQuads);
            // Pop Matrix
            tessellator.popMatrix();
        }
    }

    private void renderSeed(TileEntitySeedAnalyzer te, double x, double y, double z) {
        if (te != null && te.hasSpecimen()) {
            // Save Settings
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();

            // Translate to the location of our tile entity
            GlStateManager.translate(x, y, z);
            GlStateManager.disableRescaleNormal();

            // Render Seed Item
            renderItemStack(te.getSpecimen(), 0.5, 0.5, 0.5, 0.75, true);

            // Restore Settings
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }

}
