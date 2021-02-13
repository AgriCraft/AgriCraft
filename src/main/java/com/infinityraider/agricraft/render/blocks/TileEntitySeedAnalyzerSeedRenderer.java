package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.handler.SeedAnalyzerViewPointHandler;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TileEntitySeedAnalyzerSeedRenderer implements ITileRenderer<TileEntitySeedAnalyzer>, IRenderUtilities {
    public TileEntitySeedAnalyzerSeedRenderer() {}

    @Override
    public void render(TileEntitySeedAnalyzer tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        // Render the seed
        if(this.renderSeed(tile, partialTicks, transforms, buffer, light, overlay)) {
            // Render the genome if a seed is rendered and if the tile is observed
            if (tile.canProvideGenesForObserver()) {
                this.renderGenome(tile, transforms);
            }
        }
    }

    protected boolean renderSeed(TileEntitySeedAnalyzer tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        // Fetch the seed
        ItemStack seed = tile.getSeed();
        // If there is no seed, not much more to do here
        if(seed.isEmpty()) {
            return false;
        }

        // push a new matrix to the stack
        transforms.push();

        // Apply animation
        SeedAnalyzerViewPointHandler.getInstance().applySeedAnimation(tile, partialTicks, transforms);

        // draw the seed
        this.renderItem(seed, ItemCameraTransforms.TransformType.GROUND, light, overlay, transforms, buffer);

        // pop the matrix off the stack
        transforms.pop();

        // return true
        return true;
    }

    protected void renderGenome(TileEntitySeedAnalyzer tile, MatrixStack transforms) {
        // fetch genes
        List<IAgriGenePair<?>> genes = tile.getGenesToRender();
        if(genes == null) {
            // should never be possible
            return;
        }

        // push a new matrix to the stack
        transforms.push();

        // fetch helpers
        SeedAnalyzerViewPointHandler viewHandler = SeedAnalyzerViewPointHandler.getInstance();
        AgriGenomeRenderer renderer = AgriGenomeRenderer.getInstance();

        // fetch scroll position
        int index = viewHandler.getScrollIndex();
        float partial = viewHandler.getScrollProgress();

        // fetch orientation
        BlockState state = tile.getBlockState();
        Direction dir = BlockSeedAnalyzer.ORIENTATION.fetch(state);

        // helix dimensions
        float h = 0.50F;
        float r = 0.05F;

        // transform to the desired position
        transforms.translate(0.5, 5* Constants.UNIT, 0.5);
        transforms.rotate(new Quaternion(Vector3f.YP, dir.getHorizontalAngle(), true));

        // render the helix
        renderer.renderDoubleHelix(genes, transforms, index, partial, r, h,1.0F);

        // pop the matrix off the stack
        transforms.pop();
    }
}
