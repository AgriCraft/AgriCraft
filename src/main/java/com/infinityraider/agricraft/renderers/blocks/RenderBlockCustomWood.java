package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.BaseIcons;

import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<B extends BlockCustomWood<T>, T extends TileEntityCustomWood> extends RenderBlockWithTileBase<B, T> {

    protected RenderBlockCustomWood(B block, T te, boolean inventory, boolean staticRender, boolean dynRender) {
        super(block, te, inventory, staticRender, dynRender);
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, B block,
                                 T tile, boolean dynamicRender, float partialTick, int destroyStage) {
        this.renderWorldBlockWood(tessellator, world, pos, state, block, tile, getIcon(tile), dynamicRender);
    }

    @Override
    public final void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, B block, T tile,
                                           ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        tile.setMaterial(stack);
        this.renderInventoryBlockWood(tessellator, world, state, block, tile, stack, entity, type, getIcon(tile));
    }

    protected abstract void renderWorldBlockWood(ITessellator tess, World world, BlockPos pos, IBlockState state, B block,
                                                 T tile, TextureAtlasSprite icon, boolean dynamic);

    protected abstract void renderInventoryBlockWood(ITessellator tess, World world, IBlockState state, B block, T tile,
                                                     ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon);

    @Override
    public List<ResourceLocation> getAllTextures() {
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return getIcon(getTileEntity());
    }

    @Nonnull
    public TextureAtlasSprite getIcon(TileEntityCustomWood tile) {
        if (tile == null) {
            return BaseIcons.OAK_PLANKS.getIcon();
        }
        return tile.getIcon();
    }

}