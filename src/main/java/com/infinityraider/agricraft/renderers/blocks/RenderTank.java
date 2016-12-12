package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import net.minecraft.init.Blocks;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<BlockWaterTank, TileEntityTank> {

    // Values to stop z-fighting.
    final static float A = 00.001f;
    final static float B = Constants.WHOLE - A;

    public RenderTank(BlockWaterTank block) {
        super(block, new TileEntityTank(), true, true, true);
    }

    private void renderBottom(ITessellator tessellator, TextureAtlasSprite icon) {
        tessellator.drawScaledFace(A, A, B, B, EnumFacing.DOWN, icon, 0);
        tessellator.drawScaledFace(A, A, B, B, EnumFacing.UP, icon, 1);
    }

    private void renderSide(ITessellator tessellator, EnumFacing dir, TileEntityTank.Connection connection, TextureAtlasSprite icon) {
        if (connection == TileEntityTank.Connection.TANK) {
            return;
        }
        //data about side to render
        boolean xAxis = dir.getAxis() == EnumFacing.Axis.X;
        int index = xAxis ? dir.getFrontOffsetX() : dir.getFrontOffsetZ();
        int min = index < 0 ? 0 : 14;
        int max = index < 0 ? 2 : 16;

        //render upper face
        tessellator.drawScaledFace(xAxis ? min : 0, xAxis ? 0 : min, xAxis ? max : 16, xAxis ? 16 : max, EnumFacing.UP, icon, 16);

        //render side
        if (connection == TileEntityTank.Connection.NONE) {
            tessellator.drawScaledFace(0, 0, 16, 16, dir, icon, index > 0 ? 16 : 0);
            tessellator.drawScaledFace(0, 0, 16, 16, dir.getOpposite(), icon, index > 0 ? 14 : 2);
        } else {
            //vertical faces

            //lower part, under the channel
            tessellator.drawScaledFace(0, 0, 16, 5, dir, icon, index > 0 ? 16 : 0);
            tessellator.drawScaledFace(0, 0, 16, 5, dir.getOpposite(), icon, index > 0 ? 14 : 2);
            //left center part, same height as the channel
            tessellator.drawScaledFace(0, 5, 5, 12, dir, icon, index > 0 ? 16 : 0);
            tessellator.drawScaledFace(0, 5, 5, 12, dir.getOpposite(), icon, index > 0 ? 14 : 2);
            //right center part, same height as the channel
            tessellator.drawScaledFace(11, 5, 16, 12, dir, icon, index > 0 ? 16 : 0);
            tessellator.drawScaledFace(11, 5, 16, 12, dir.getOpposite(), icon, index > 0 ? 14 : 2);
            //upper part, above the channel
            tessellator.drawScaledFace(0, 12, 16, 16, dir, icon, index > 0 ? 16 : 0);
            tessellator.drawScaledFace(0, 12, 16, 16, dir.getOpposite(), icon, index > 0 ? 14 : 2);

            //inside of the gap
            tessellator.drawScaledFace(xAxis ? min : 5, xAxis ? 5 : min, xAxis ? max : 11, xAxis ? 11 : max, EnumFacing.UP, icon, 5);
            tessellator.drawScaledFace(xAxis ? min : 5, xAxis ? 5 : min, xAxis ? max : 11, xAxis ? 11 : max, EnumFacing.DOWN, icon, 12);

            EnumFacing left = xAxis ? EnumFacing.NORTH : EnumFacing.WEST;
            EnumFacing right = left.getOpposite();

            tessellator.drawScaledFace(min, 5, max, 12, left, icon, 11);
            tessellator.drawScaledFace(min, 5, max, 12, right, icon, 5);
        }
    }

    @Override
    protected void renderWorldBlockWoodDynamic(ITessellator tessellator, World world, BlockPos pos, BlockWaterTank block,
                                               TileEntityTank tank, TextureAtlasSprite sprite) {
        //only render water on the bottom layer
        if (tank.getYPosition() == 0) {

            // -0.0001F to avoid Z-fighting on maximum filled tanks
            float y = tank.getFluidHeight() - A;

            // Calculate water brightness.
            final int l = RenderUtilBase.getMixedBrightness(tank.getWorld(), tank.getPos(), Blocks.WATER);
            tessellator.setBrightness(l);
            tessellator.setColorRGBA(tessellator.getRedValueInt(), tessellator.getRedValueInt(), tessellator.getBlueValueInt(), 100);

            //draw surface
            final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
            tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, waterIcon, y);
        }
    }

    @Override
    protected void renderWorldBlockWoodStatic(ITessellator tess, IExtendedBlockState state, BlockWaterTank block, EnumFacing side, TextureAtlasSprite sprite) {
        TileEntityTank.Connection north = AgriProperties.TANK_NORTH.getValue(state);
        TileEntityTank.Connection east = AgriProperties.TANK_EAST.getValue(state);
        TileEntityTank.Connection south = AgriProperties.TANK_SOUTH.getValue(state);
        TileEntityTank.Connection west = AgriProperties.TANK_WEST.getValue(state);
        TileEntityTank.Connection down = AgriProperties.TANK_DOWN.getValue(state);
        renderSide(tess, EnumFacing.NORTH, north, sprite);
        renderSide(tess, EnumFacing.EAST, east, sprite);
        renderSide(tess, EnumFacing.SOUTH, south, sprite);
        renderSide(tess, EnumFacing.WEST, west, sprite);
        if (down != TileEntityTank.Connection.TANK) {
            renderBottom(tess, sprite);
        }
    }

    @Override
    protected void renderInventoryBlockWood(ITessellator tess, World world, IBlockState state, BlockWaterTank block, TileEntityTank tile,
                                            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite sprite) {
        renderSide(tess, EnumFacing.NORTH, TileEntityTank.Connection.NONE, sprite);
        renderSide(tess, EnumFacing.EAST, TileEntityTank.Connection.NONE, sprite);
        renderSide(tess, EnumFacing.SOUTH, TileEntityTank.Connection.NONE, sprite);
        renderSide(tess, EnumFacing.WEST, TileEntityTank.Connection.NONE, sprite);
        renderBottom(tess, sprite);
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }

}
