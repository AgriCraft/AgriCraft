package com.infinityraider.agricraft.renderers.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.infinityraider.agricraft.blocks.decoration.BlockGrate;
import com.infinityraider.agricraft.blocks.tiles.decoration.TileEntityGrate;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;

public class RenderBlockGrate extends RenderBlockCustomWood<BlockGrate, TileEntityGrate> {
    public RenderBlockGrate(BlockGrate block) {
        super(block, new TileEntityGrate(), true, true, false);
    }

    @Override
    public void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, BlockGrate block, TileEntityGrate tile,
                                         ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
        tessellator.drawScaledPrism(7, 0, 1, 9, 16, 3, icon);
        tessellator.drawScaledPrism(7, 0, 5, 9, 16, 7, icon);
        tessellator.drawScaledPrism(7, 0, 9, 9, 16, 11, icon);
        tessellator.drawScaledPrism(7, 0, 13, 9, 16, 15, icon);
        tessellator.drawScaledPrism(7, 1, 0, 9, 3, 16, icon);
        tessellator.drawScaledPrism(7, 5, 0, 9, 7, 16, icon);
        tessellator.drawScaledPrism(7, 9, 0, 9, 11, 16, icon);
        tessellator.drawScaledPrism(7, 13, 0, 9, 15, 16, icon);
    }

    @Override
    protected void renderWorldBlockWood(ITessellator tess, World world, BlockPos pos, IBlockState state, BlockGrate block,
                                        TileEntityGrate grate, TextureAtlasSprite sprite, boolean dynamic) {
        // Setup
        final float offset = ((float) grate.getOffset() * 7) / 16.0F;

        // Offset
        tess.translate(0, 0, offset);

        // Draw Grate
        tess.drawScaledPrism(1, 0, 0, 3, 16, 2, sprite);
        tess.drawScaledPrism(5, 0, 0, 7, 16, 2, sprite);
        tess.drawScaledPrism(9, 0, 0, 11, 16, 2, sprite);
        tess.drawScaledPrism(13, 0, 0, 15, 16, 2, sprite);
        tess.drawScaledPrism(0, 1, 0, 16, 3, 2, sprite);
        tess.drawScaledPrism(0, 5, 0, 16, 7, 2, sprite);
        tess.drawScaledPrism(0, 9, 0, 16, 11, 2, sprite);
        tess.drawScaledPrism(0, 13, 0, 16, 15, 2, sprite);

        //vines
        final TextureAtlasSprite vinesIcon = BaseIcons.VINE.getIcon();
        int l = this.getMixedBrightness(grate.getWorld(), grate.getPos(), Blocks.VINE.getDefaultState());
        float f0 = (float) (l >> 16 & 255) / 255.0F;
        float f1 = (float) (l >> 8 & 255) / 255.0F;
        float f2 = (float) (l & 255) / 255.0F;
        tess.setColorRGB_F(f0, f1, f2);

        if (grate.hasVines(true)) {
            tess.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, vinesIcon, 0.001f);
        }
        if (grate.hasVines(false)) {
            tess.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, vinesIcon, 1.999f);
        }
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }
}
