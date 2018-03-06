package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelValve;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelValve;
import com.infinityraider.agricraft.utility.BaseIcons;
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
public class RenderChannelValve extends RenderChannel<BlockWaterChannelValve, TileEntityChannelValve> {

    public RenderChannelValve(BlockWaterChannelValve block) {
        super(block, block.createNewTileEntity(null, 0));
    }

    @Override
    public void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, BlockWaterChannelValve block, TileEntityChannelValve tile,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {

        final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

        //Render channel.
        tessellator.drawScaledPrism(2, 4, 4, 14, 12, 5, icon);
        tessellator.drawScaledPrism(2, 4, 11, 14, 12, 12, icon);
        tessellator.drawScaledPrism(2, 4, 5, 14, 5, 11, icon);

        //Render separators.
        tessellator.drawScaledPrism(0.001f, 11.5f, 5, 1.999f, 15.001f, 11, sepIcon);
        tessellator.drawScaledPrism(0.001f, 0.999f, 5, 1.999f, 5.5f, 11, sepIcon);
        tessellator.drawScaledPrism(14.001f, 11.5f, 5, 15.999f, 15.001f, 11, sepIcon);
        tessellator.drawScaledPrism(14.001f, 0.999f, 5, 15.999f, 5.5f, 11, sepIcon);

        //render the wooden guide rails along z-axis
        tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.translate(0, 0, 6 * Constants.UNIT);
        tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.translate(14 * Constants.UNIT, 0, 0);
        tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.translate(0, 0, -6 * Constants.UNIT);
        tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
        tessellator.translate(-14 * Constants.UNIT, 0, 0);
    }

    @Override
    protected void renderSide(ITessellator tessellator, IBlockState state, EnumFacing dir, byte connection, TextureAtlasSprite matIcon) {
        super.renderSide(tessellator, state, dir, connection, matIcon);
        if (connection == 1) {
            renderSeparator(tessellator, state, dir, matIcon, BaseIcons.IRON_BLOCK.getIcon());
        } else if (connection == -1) {
            renderConnector(tessellator, dir, matIcon);
        }
    }

    private void renderConnector(ITessellator tessellator, EnumFacing dir, TextureAtlasSprite matIcon) {
        switch (dir) {
            case EAST:
                //positive x
                tessellator.drawScaledPrism(12, 4, 5, 16, 12, 11, matIcon);
                break;
            case WEST:
                //negative x
                tessellator.drawScaledPrism(0, 4, 5, 4, 12, 11, matIcon);
                break;
            case NORTH:
                //negative z
                tessellator.drawScaledPrism(5, 4, 0, 11, 12, 4, matIcon);
                break;
            case SOUTH:
                //positive z
                tessellator.drawScaledPrism(5, 4, 12, 11, 12, 16, matIcon);
                break;
        }
    }

    private void renderSeparator(ITessellator tessellator, IBlockState state, EnumFacing dir, TextureAtlasSprite matIcon, TextureAtlasSprite sepIcon) {
        boolean powered = AgriProperties.POWERED.getValue(state);
        switch (dir) {
            case EAST:
                //positive x
                tessellator.drawScaledPrism(14, 0, 3, 16, 16, 6, matIcon);
                tessellator.drawScaledPrism(14, 0, 10, 16, 16, 13, matIcon);
                if (powered) {
                    tessellator.drawScaledPrism(14, 5, 6, 16, 12, 10, sepIcon);
                } else {
                    tessellator.drawScaledPrism(14, 1, 6, 16, 5.001F, 10, sepIcon);
                    tessellator.drawScaledPrism(14, 12, 6, 16, 15, 10, sepIcon);
                }
                break;
            case WEST:
                //negative x
                tessellator.drawScaledPrism(0, 0, 3, 2, 16, 6, matIcon);
                tessellator.drawScaledPrism(0, 0, 10, 2, 16, 13, matIcon);
                if (powered) {
                    tessellator.drawScaledPrism(0, 5, 6, 2, 12, 10, sepIcon);
                } else {
                    tessellator.drawScaledPrism(0, 1, 6, 2, 5.001F, 10, sepIcon);
                    tessellator.drawScaledPrism(0, 12, 6, 2, 15, 10, sepIcon);
                }
                break;
            case NORTH:
                //negative z
                tessellator.drawScaledPrism(3, 0, 0, 6, 16, 2, matIcon);
                tessellator.drawScaledPrism(10, 0, 0, 13, 16, 2, matIcon);
                if (powered) {
                    tessellator.drawScaledPrism(6, 5, 0, 10, 12, 2, sepIcon);
                } else {
                    tessellator.drawScaledPrism(6, 1, 0, 10, 5.001F, 2, sepIcon);
                    tessellator.drawScaledPrism(6, 12, 0, 10, 15, 2, sepIcon);
                }
                break;
            case SOUTH:
                //positive z
                tessellator.drawScaledPrism(3, 0, 14, 6, 16, 16, matIcon);
                tessellator.drawScaledPrism(10, 0, 14, 13, 16, 16, matIcon);
                if (powered) {
                    tessellator.drawScaledPrism(6, 5, 14, 10, 12, 16, sepIcon);
                } else {
                    tessellator.drawScaledPrism(6, 1, 14, 10, 5.001F, 16, sepIcon);
                    tessellator.drawScaledPrism(6, 12, 14, 10, 15, 16, sepIcon);
                }
                break;
        }
    }

}
