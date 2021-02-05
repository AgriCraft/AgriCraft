package com.infinityraider.agricraft.render.blocks;

import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.tile.ITileRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySeedAnalyzerSeedRenderer implements ITileRenderer<TileEntitySeedAnalyzer>, IRenderUtilities {
    public TileEntitySeedAnalyzerSeedRenderer() {}

    @Override
    public void render(TileEntitySeedAnalyzer tile, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light, int overlay) {
        ItemStack seed = tile.getSeed();
        if(seed.isEmpty()) {
            return;
        }

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
}
