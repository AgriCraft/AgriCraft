package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.handler.SeedAnalyzerViewPointHandler;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TileEntitySeedAnalyzerSeedRenderer implements ITileRenderer<TileEntitySeedAnalyzer>, IRenderUtilities {
    private static final Quaternion TEXT_ROTATION = new Quaternion(Vector3f.XP, 90, true);

    static {
        TEXT_ROTATION.mul(new Quaternion(Vector3f.ZP, 180, true));
        TEXT_ROTATION.mul(new Quaternion(Vector3f.XP, 22.5F, true));
    }

    public TileEntitySeedAnalyzerSeedRenderer() {}

    @Override
    public void render(TileEntitySeedAnalyzer tile, float partialTick, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        // Render the seed
        if(this.renderSeed(tile, partialTick, transforms, buffer, light, overlay)) {
            // Render the genome if a seed is rendered and if the tile is observed
            if (tile.canProvideGenesForObserver()) {
                this.renderGenome(tile, partialTick, transforms, buffer);
            }
        }
    }

    protected boolean renderSeed(TileEntitySeedAnalyzer tile, float partialTick, PoseStack transforms, MultiBufferSource buffer, int light, int overlay) {
        // Fetch the seed
        ItemStack seed = tile.getSeed();
        // If there is no seed, not much more to do here
        if(seed.isEmpty()) {
            return false;
        }

        // push a new matrix to the stack
        transforms.pushPose();

        // Apply animation
        SeedAnalyzerViewPointHandler.getInstance().applySeedAnimation(tile, partialTick, transforms);

        // draw the seed
        this.renderItem(seed, ItemTransforms.TransformType.GROUND, light, overlay, transforms, buffer);

        // pop the matrix off the stack
        transforms.popPose();

        // return true
        return true;
    }

    protected void renderGenome(TileEntitySeedAnalyzer tile, float partialTick, PoseStack transforms, MultiBufferSource buffer) {
        // fetch genes
        List<IAgriGenePair<?>> genes = tile.getGenesToRender();
        if(genes == null) {
            // should never be possible
            return;
        }

        // push a new matrix to the stack
        transforms.pushPose();

        // fetch helpers
        SeedAnalyzerViewPointHandler viewHandler = SeedAnalyzerViewPointHandler.getInstance();
        AgriGenomeRenderer renderer = AgriGenomeRenderer.getInstance();

        // fetch scroll position
        int index = viewHandler.getScrollIndex();
        float partial = viewHandler.getPartialScrollProgress(partialTick);

        // fetch orientation
        BlockState state = tile.getBlockState();
        Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);

        // helix dimensions
        float h = Constants.HALF;
        float r = h / 10;

        // transform to the desired position
        float dx = Constants.HALF + Constants.UNIT*dir.getStepX();
        float dy = 5 * Constants.UNIT;
        float dz = Constants.HALF + Constants.UNIT*dir.getStepZ();
        transforms.translate(dx, dy ,dz);
        transforms.mulPose(new Quaternion(Vector3f.YP, tile.getHorizontalAngle(), true));

        // render the helix
        renderer.renderDoubleHelix(genes, transforms, buffer, index, partial, r, h,1.0F, false);

        // render the text
        if(index >= 0 && index < genes.size()) {
            transforms.pushPose();
            transforms.translate(0, 0, -3*Constants.UNIT);
            transforms.mulPose(TEXT_ROTATION);
            float scale = 2.0F/Math.max(this.getScaledWindowWidth(), this.getScaledWindowHeight());
            transforms.scale(scale, scale, 1);
            renderer.renderTextOverlay(transforms, genes.get(index));
            transforms.popPose();
        }

        // pop the matrix off the stack
        transforms.popPose();
    }
}
