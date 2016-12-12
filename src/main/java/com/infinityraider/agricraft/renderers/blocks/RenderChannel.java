package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.AbstractBlockWaterChannel;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.RenderUtilBase;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel<B extends AbstractBlockWaterChannel<T>, T extends TileEntityChannel> extends RenderBlockCustomWood<B, T> {

    public static AtomicInteger renderCallCounter = new AtomicInteger(0);

    @SuppressWarnings("unchecked")
    public RenderChannel(B block, T channel) {
        super(block, channel, true, true, true);
    }

    protected void renderWoodChannel(ITessellator tessellator, IBlockState state, TextureAtlasSprite icon) {
        this.renderBottom(tessellator, icon);
        this.renderSide(tessellator, state, EnumFacing.NORTH, AgriProperties.CHANNEL_NORTH.getValue(state), icon);
        this.renderSide(tessellator, state, EnumFacing.EAST, AgriProperties.CHANNEL_EAST.getValue(state), icon);
        this.renderSide(tessellator, state, EnumFacing.SOUTH, AgriProperties.CHANNEL_SOUTH.getValue(state), icon);
        this.renderSide(tessellator, state, EnumFacing.WEST, AgriProperties.CHANNEL_WEST.getValue(state), icon);
    }

    protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
        //bottom
        tessellator.drawScaledPrism(4, 4, 4, 12, 5, 12, matIcon);
        //corners
        tessellator.drawScaledPrism(4, 5, 4, 5, 12, 5, matIcon);
        tessellator.drawScaledPrism(11, 5, 4, 12, 12, 5, matIcon);
        tessellator.drawScaledPrism(4, 5, 11, 5, 12, 12, matIcon);
        tessellator.drawScaledPrism(11, 5, 11, 12, 12, 12, matIcon);
    }

    protected void renderSide(ITessellator tessellator, IBlockState state, EnumFacing dir, boolean connect, TextureAtlasSprite matIcon) {
        switch (dir) {
            case EAST:
                //positive x
                if (connect) {
                    tessellator.drawScaledPrism(12, 4, 4, 16, 5, 12, matIcon);
                    tessellator.drawScaledPrism(12, 5, 4, 16, 12, 5, matIcon);
                    tessellator.drawScaledPrism(12, 5, 11, 16, 12, 12, matIcon);
                } else {
                    tessellator.drawScaledPrism(11, 5, 5, 12, 12, 11, matIcon);
                }
                break;
            case WEST:
                //negative x
                if (connect) {
                    tessellator.drawScaledPrism(0, 4, 4, 4, 5, 12, matIcon);
                    tessellator.drawScaledPrism(0, 5, 4, 4, 12, 5, matIcon);
                    tessellator.drawScaledPrism(0, 5, 11, 4, 12, 12, matIcon);
                } else {
                    tessellator.drawScaledPrism(4, 5, 5, 5, 12, 11, matIcon);
                }
                break;
            case NORTH:
                //negative z
                if (connect) {
                    tessellator.drawScaledPrism(4, 4, 0, 12, 5, 4, matIcon);
                    tessellator.drawScaledPrism(4, 5, 0, 5, 12, 4, matIcon);
                    tessellator.drawScaledPrism(11, 5, 0, 12, 12, 4, matIcon);
                } else {
                    tessellator.drawScaledPrism(5, 5, 4, 11, 12, 5, matIcon);
                }
                break;
            case SOUTH:
                //positive z
                if (connect) {
                    tessellator.drawScaledPrism(4, 4, 12, 12, 5, 16, matIcon);
                    tessellator.drawScaledPrism(4, 5, 12, 5, 12, 16, matIcon);
                    tessellator.drawScaledPrism(11, 5, 12, 12, 12, 16, matIcon);
                } else {
                    tessellator.drawScaledPrism(5, 5, 11, 11, 12, 12, matIcon);
                }
                break;
        }
    }

    protected void drawWater(ITessellator tessellator, T channel, TextureAtlasSprite icon) {
        if (channel.getFluidAmount(0) < 0) {
            // There exists no water to be rendered.
            return;
        }

        // There is water to render.
        // Increment the render counter.
        renderCallCounter.incrementAndGet();

        // Calculate water brightness.
        final int l = RenderUtilBase.getMixedBrightness(channel.getWorld(), channel.getPos(), Blocks.WATER);
        tessellator.setBrightness(l);
        tessellator.setColorRGBA(tessellator.getRedValueInt(), tessellator.getRedValueInt(), tessellator.getBlueValueInt(), 100);
        
        // Calculate y to avoid plane rendering conflicts
        final float y = channel.getFluidHeight() - 0.001f;

        //draw central water levels
        tessellator.drawScaledFaceDouble(5, 5, 11, 11, EnumFacing.UP, icon, y);
        //connect to edges
        if (channel.hasNeighbourCheck(EnumFacing.NORTH)) {
            tessellator.drawScaledFaceDouble(5, 0, 11, 5, EnumFacing.UP, icon, y);
        }
        if (channel.hasNeighbourCheck(EnumFacing.EAST)) {
            tessellator.drawScaledFaceDouble(11, 5, 16, 11, EnumFacing.UP, icon, y);
        }
        if (channel.hasNeighbourCheck(EnumFacing.SOUTH)) {
            tessellator.drawScaledFaceDouble(5, 11, 11, 16, EnumFacing.UP, icon, y);
        }
        if (channel.hasNeighbourCheck(EnumFacing.WEST)) {
            tessellator.drawScaledFaceDouble(0, 5, 5, 11, EnumFacing.UP, icon, y);
        }

    }

    @Override
    protected void renderWorldBlockWoodDynamic(ITessellator tess, World world, BlockPos pos, B block,
                                               T tile, TextureAtlasSprite icon) {
        this.drawWater(tess, tile, BaseIcons.WATER_STILL.getIcon());
    }

    @Override
    protected void renderWorldBlockWoodStatic(ITessellator tess, IExtendedBlockState state, B block, EnumFacing side, TextureAtlasSprite icon) {
        this.renderWoodChannel(tess, state, icon);
    }

    @Override
    protected void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, B block, T channel, ItemStack stack,
                                            EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
        this.renderBottom(tessellator, icon);
        this.renderSide(tessellator, state, EnumFacing.NORTH, false, icon);
        this.renderSide(tessellator, state, EnumFacing.EAST, false, icon);
        this.renderSide(tessellator, state, EnumFacing.SOUTH, false, icon);
        this.renderSide(tessellator, state, EnumFacing.WEST, false, icon);

    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }
}
