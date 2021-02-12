package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.handler.SeedAnalyzerViewPointHandler;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
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

    private static final Quaternion ROTATION_TILT = new Quaternion(Vector3f.XP, 45.0F, true);

    public TileEntitySeedAnalyzerSeedRenderer() {}

    @Override
    public void render(TileEntitySeedAnalyzer tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        // Fetch the seed
        ItemStack seed = tile.getSeed();
        // If there is no seed, not much more to do here
        if(seed.isEmpty()) {
            return;
        }
        // Render the seed
        this.renderSeed(seed, transforms, buffer, light, overlay);
        // Render the genome if observed
        if(tile.canProvideGenesForObserver()) {
            this.renderGenome(tile, transforms);
        }
    }

    protected void renderSeed(ItemStack seed, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        // push a new matrix to the stack
        transforms.push();

        // translate to center of block
        transforms.translate(0.5, 0.5, 0.5);

        // define rotation angle in function of system time
        float angle = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        transforms.rotate(new Quaternion(Vector3f.YP, angle, true));

        // scale down the seed
        transforms.scale(0.5F, 0.5F, 0.5F);

        // draw the seed
        this.renderItem(seed, ItemCameraTransforms.TransformType.GROUND, light, overlay, transforms, buffer);

        // pop the matrix off the stack
        transforms.pop();
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
        float h = 1.500F;
        float r = 0.125F;

        // transform to the desired position
        transforms.translate(0.5, 0, 0.5);
        transforms.rotate(new Quaternion(Vector3f.YP, dir.getHorizontalAngle(), true));
        //transforms.translate(0.25, 0, 0.25);
        transforms.rotate(ROTATION_TILT);
        transforms.scale(1, -1, 1);
        //transforms.translate(0, h*((index + partial) - count)/count, 0);

        // render the helix
        renderer.renderDoubleHelix(genes, transforms, index, partial, r, h,1.0F);

        // pop the matrix off the stack
        transforms.pop();
    }
}
