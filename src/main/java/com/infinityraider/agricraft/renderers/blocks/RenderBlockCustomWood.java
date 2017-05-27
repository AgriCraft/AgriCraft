package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<B extends BlockCustomWood<T>, T extends TileEntityCustomWood> extends RenderBlockWithTileBase<B, T> {

    protected RenderBlockCustomWood(B block, T te, boolean inventory, boolean staticRender, boolean dynRender) {
        super(block, te, inventory, staticRender, dynRender);
    }

    @Override
    public void renderWorldBlockDynamic(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, B block,
                                        T tile, float partialTick, int destroyStage) {
        this.renderWorldBlockWoodDynamic(tessellator, world, pos, block, tile, getIcon(tile));
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, B block, EnumFacing side) {
        if(state instanceof IExtendedBlockState) {
            CustomWoodType type = ((IExtendedBlockState) state).getValue(AgriProperties.CUSTOM_WOOD_TYPE);
            this.renderWorldBlockWoodStatic(tessellator, (IExtendedBlockState) state, block, side, type.getIcon());
        }
    }

    @Override
    public final void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, B block, T tile,
                                           ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        tile.setMaterial(stack);
        this.renderInventoryBlockWood(tessellator, world, state, block, tile, stack, entity, type, getIcon(tile));
    }

    protected abstract void renderWorldBlockWoodDynamic(ITessellator tess, World world, BlockPos pos, B block,
                                                        T tile, TextureAtlasSprite icon);

    protected abstract void renderWorldBlockWoodStatic(ITessellator tess, IExtendedBlockState state, B block, EnumFacing side, TextureAtlasSprite icon);

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