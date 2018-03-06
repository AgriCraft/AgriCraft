package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel<BlockWaterChannelFull, TileEntityChannelFull> {

    public RenderChannelFull(BlockWaterChannelFull block) {
        super(block, block.createNewTileEntity(null, 0));
    }

    @Override
    protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
        //draw bottom
        tessellator.drawScaledPrism(0, 0, 0, 16, 5, 16, matIcon);
        //draw top
        tessellator.drawScaledPrism(0, 12, 0, 16, 16, 16, matIcon);
        //draw four corners
        tessellator.drawScaledPrism(0, 5, 0, 5, 12, 5, matIcon);
        tessellator.drawScaledPrism(11, 5, 0, 16, 12, 5, matIcon);
        tessellator.drawScaledPrism(11, 5, 11, 16, 12, 16, matIcon);
        tessellator.drawScaledPrism(0, 5, 11, 5, 12, 16, matIcon);

    }

    @Override
    protected void renderSide(ITessellator tessellator, IBlockState state, EnumFacing dir, byte connection, TextureAtlasSprite matIcon) {
        if (connection > 0) {
            return;
        }
        switch (dir) {
            case EAST:
                //positive x
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 16);
                break;
            case WEST:
                //negative x
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 0);
                break;
            case NORTH:
                //negative z
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 0);
                break;
            case SOUTH:
                //positive z
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 16);
                break;
        }
    }

    @Override
    protected void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, BlockWaterChannelFull block, TileEntityChannelFull channel,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
        this.renderBottom(tessellator, icon);
        this.renderSide(tessellator, state, EnumFacing.NORTH, (byte) 1, icon);
        this.renderSide(tessellator, state, EnumFacing.EAST, (byte) 1, icon);
        this.renderSide(tessellator, state, EnumFacing.SOUTH, (byte) 1, icon);
        this.renderSide(tessellator, state, EnumFacing.WEST, (byte) 1, icon);
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }
}
